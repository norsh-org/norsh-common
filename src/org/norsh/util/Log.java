package org.norsh.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.norsh.config.LogConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * LogService provides structured logging functionality for the Norsh system.
 * <p>
 * Handles log creation, formatting, rotation, and writing to files or console based on configuration. Supports
 * JSON-formatted logs, multiple log levels, and ensures efficient logging for large-scale applications.
 * </p>
 * 
 * <h2>Log Levels:</h2>
 * <ul>
 * <li>{@code 0}: SYSTEM - System messages, such as initializations.</li>
 * <li>{@code 1}: ERROR - Critical errors and exceptions.</li>
 * <li>{@code 2}: WARNING - Warnings that do not interrupt the main flow.</li>
 * <li>{@code 3}: INFO - General operational information.</li>
 * <li>{@code 4}: DEBUG - Technical details for developers.</li>
 * </ul>
 *
 * @since 1.0.0
 * @version 1.0.0
 * @author Danthur Lice
 * @see LogConfig
 * @see <a href="https://docs.norsh.org">Norsh Documentation</a>
 */
public class Log {
	private final AtomicLong sequence = new AtomicLong(0);
	private final Gson gson;
	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private final LogConfig config;
	private final String uuid;
	private PrintStream out;
	private Instant start;

	class ExceptionSerializer implements JsonSerializer<Exception> {
		@Override
		public JsonElement serialize(Exception src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("cause", new JsonPrimitive(String.valueOf(src.getCause())));
			jsonObject.add("message", new JsonPrimitive(src.getMessage()));
			return jsonObject;
		}
	}

	/**
	 * Constructs a LogService with the specified configuration.
	 *
	 * @param config the log configuration.
	 */
	public Log(LogConfig config) {
		this.config = config;
		this.uuid = UUID.randomUUID().toString();
		this.start = Instant.now();

		GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		gsonBuilder.registerTypeAdapterFactory(new LogThrowableTypeAdapter());
		gson = gsonBuilder.create();

		ensureLogDirectoryExists();
	}

	/**
	 * Ensures that the log directory exists. If it does not exist, the method attempts to create it.
	 */
	private void ensureLogDirectoryExists() {
		File logDir = new File(config.getPath());
		if (!logDir.exists()) {
			boolean created = logDir.mkdirs();
			if (!created) {
				System.err.println("Failed to create log directory at: " + config.getPath());
			}
		}
	}

	/**
	 * Retrieves the output stream for logging.
	 * <p>
	 * If a new log file is required due to rotation policies, it initializes a new output stream.
	 * </p>
	 *
	 * @return the output PrintStream.
	 * @throws IOException if there is an issue accessing the log file.
	 */
	private synchronized PrintStream getOut() throws IOException {
		if (out == null || needsRotation()) {
			if (out != null) {
				out.close();
			}
			out = new PrintStream(new FileOutputStream(getFilename(), true));
			writeHeader(out);
		}
		return out;
	}

	/**
	 * Writes a header to the log file indicating UUID and log start time.
	 *
	 * @param out the output stream for writing.
	 */
	private void writeHeader(PrintStream out) {
		out.println("# UUID: " + uuid);
		out.println("# Log Start Time: " + dateFormatter.format(ZonedDateTime.ofInstant(start, ZoneId.systemDefault())));
	}

	/**
	 * Checks if the log file needs rotation based on size, row count, or time interval.
	 *
	 * @return {@code true} if the log file needs rotation, {@code false} otherwise.
	 */
	private boolean needsRotation() {
		File currentFile = new File(getFilename());
		boolean sizeExceeded = currentFile.length() >= config.getMaxLength();
		boolean rowsExceeded = sequence.get() >= config.getMaxRows();
		boolean timeExceeded = Duration.between(start, Instant.now()).toMinutes() >= config.getRotationIntervalMinutes();
		return sizeExceeded || rowsExceeded || timeExceeded;
	}

	/**
	 * Generates the filename for the current log file.
	 *
	 * @return the formatted log filename.
	 */
	private String getFilename() {
		String timestamp = LocalDateTime.ofInstant(start, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(config.getDatePattern()));
		return String.format("%s%s_%s.log", config.getPath(), config.getPrefix(), timestamp);
	}

	/**
	 * Retrieves the class and line number from where the log method was invoked.
	 *
	 * @return a formatted string representing class, method, and line number.
	 */
	private String getCurrentClassAndLine() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		boolean foundLogService = false;

		for (StackTraceElement element : stackTrace) {
			if (element.getClassName().equals(Log.class.getCanonicalName())) {
				foundLogService = true;
			} else if (foundLogService) {
				String className = element.getClassName();
				className = className.substring(className.lastIndexOf(".") + 1);

				return String.format("%s:%d", className, element.getLineNumber());
			}
		}
		return "Unknown";
	}

	/**
	 * Writes a log entry to the output stream.
	 *
	 * @param level   the log level (SYSTEM, ERROR, WARNING, INFO, DEBUG).
	 * @param message the log message.
	 * @param obj     optional object data to include in the log.
	 */
	private synchronized void write(String level, String message, Object obj) {
		try {
			String json = (obj instanceof String string) ? " -> ".concat(string) : (obj == null ? "" : " -> ".concat(gson.toJson(obj)));
			String timestamp = dateFormatter.format(ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
			long seq = sequence.incrementAndGet();

			String logEntry = String.format("%s %d %s [%s %s] %s %s", timestamp, seq, level.toUpperCase(), Thread.currentThread().getName(), getCurrentClassAndLine(), message == null ? "-" : message, json);

			if (config.getSysout()) {
				System.out.println(logEntry);
			}

			getOut().println(logEntry);
		} catch (IOException e) {
			System.err.println("Log write failed: " + e.getMessage());
		}
	}

	/**
	 * Writes a blank line to the log file, typically for readability.
	 */
	private synchronized void writeBreakLine() {
		try {
			if (config.getSysout()) {
				System.out.println();
			}
			getOut().println("");
		} catch (IOException e) {
			System.err.println("Log write failed: " + e.getMessage());
		}
	}

	/**
	 * Logs a blank line for readability.
	 */
	public void breakLine() {
		writeBreakLine();
	}

	/**
	 * Logs a system-level message.
	 *
	 * @param message the message to log.
	 * @return the sequence number of the log entry.
	 */
	public void system(String message) {
		if (config.getLevel() >= 0)
			write("SYSTEM", message, null);
	}

	public void error(String message, Object obj) {
		if (config.getLevel() >= 1)
			write("ERROR", message, obj);
	}

	public void warning(String message, Object obj) {
		if (config.getLevel() >= 2)
			write("WARNING", message, obj);
	}

	public void info(String message, Object obj) {
		if (config.getLevel() >= 3)
			write("INFO", message, obj);
	}

	public void debug(String message, Object obj) {
		if (config.getLevel() >= 4)
			write("DEBUG", message, obj);
	}

	public void error(String message) {
		error(message, null);
	}

	public void warning(String message) {
		warning(message, null);
	}

	public void info(String message) {
		info(message, null);
	}

	public void debug(String message) {
		debug(message, null);
	}
}
