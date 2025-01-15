package org.norsh.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * LogService provides structured logging functionality for the Norsh system.
 * <p>
 * Handles log creation, formatting, rotation, and writing to files or console based on configuration. Supports JSON-formatted
 * logs, multiple log levels, and ensures efficient logging for large-scale applications.
 * </p>
 * 
 * Log levels:
 * <ul>
 * <li>{@code 0}: SYSTEM - System messages, such as initializations.</li>
 * <li>{@code 1}: ERROR - Critical errors and exceptions.</li>
 * <li>{@code 2}: WARNING - Warnings that do not interrupt the main flow.</li>
 * <li>{@code 3}: INFO - General operational information.</li>
 * <li>{@code 4}: DEBUG - Technical details for developers.</li>
 * </ul>
 *
 * @license NCL-139
 * @since 01/2025
 * @version 1.0
 * @author Danthur Lice
 * @see LogConfig
 * @see <a href="https://docs.norsh.org">Norsh Documentation</a>
 */
public class Log {
	private final AtomicLong sequence = new AtomicLong(0);
	private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").create();
	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
	private final LogConfig config;
	private final String uuid;
	private PrintStream out;
	private Instant start;

	/**
	 * Constructs a LogService with the specified configuration.
	 *
	 * @param config the log configuration.
	 */
	public Log(LogConfig config) {
		this.config = config;
		this.uuid = UUID.randomUUID().toString();
		this.start = Instant.now();
		ensureLogDirectoryExists();
	}

	private void ensureLogDirectoryExists() {
		File logDir = new File(config.getPath());
		if (!logDir.exists()) {
			boolean created = logDir.mkdirs();
			if (!created) {
				System.err.println("Failed to create log directory at: " + config.getPath());
			}
		}
	}

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

	private void writeHeader(PrintStream out) {
		out.println("# UUID: " + uuid);
		out.println("# Log Start Time: " + dateFormatter.format(ZonedDateTime.ofInstant(start, ZoneId.systemDefault())));
	}

	private boolean needsRotation() {
		File currentFile = new File(getFilename());
		boolean sizeExceeded = currentFile.length() >= config.getMaxLength();
		boolean rowsExceeded = sequence.get() >= config.getMaxRows();
		boolean timeExceeded = Duration.between(start, Instant.now()).toMinutes() >= config.getRotationIntervalMinutes();
		return sizeExceeded || rowsExceeded || timeExceeded;
	}

	private String getFilename() {
		String timestamp = LocalDateTime.ofInstant(start, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(config.getDatePattern()));
		return String.format("%s%s_%s_%s.log", config.getPath(), config.getPrefix(), uuid, timestamp);
	}

	private String getCurrentClassAndLine() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		boolean foundLogService = false;

		for (StackTraceElement element : stackTrace) {
			if (element.getClassName().equals(Log.class.getCanonicalName())) {
				foundLogService = true;
			} else if (foundLogService) {
				String className = element.getClassName();
				className = className.substring(className.lastIndexOf(".") + 1);

				return String.format("%s.%s:%d", className, element.getMethodName(), element.getLineNumber());
			}
		}

		return "Unknown";
	}

	private synchronized String write(String level, String message, Object obj) {
		try {
			String json = (obj instanceof String string) ? " -> ".concat(string) : (obj == null ? "" : " -> ".concat(gson.toJson(obj)));
			String timestamp = dateFormatter.format(ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
			long seq = sequence.incrementAndGet();

			String logEntry = String.format("%s %d %s [%s %s] %s %s", timestamp, seq, level.toUpperCase(), Thread.currentThread().getName(), getCurrentClassAndLine(), message == null ? "-" : message, json);

			if (config.getSysout()) {
				System.out.println(logEntry);
			}

			getOut().println(logEntry);
			return String.valueOf(seq);
		} catch (IOException e) {
			System.err.println("Log write failed: " + e.getMessage());
			return null;
		}
	}

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

	public void breakLine() {
		writeBreakLine();
	}

	public String system(String message) {
		return write("SYSTEM", message, null);
	}

	public String error(String message, Throwable t) {
		if (config.getLevel() >= 1) {
			return write("ERROR", message, t.getMessage());
		}
		return null;
	}

	public String error(String message, Object obj) {
		if (config.getLevel() >= 1) {
			return write("ERROR", message, obj);
		}
		return null;
	}

	public String warning(String message, Object obj) {
		if (config.getLevel() >= 2) {
			return write("WARNING", message, obj);
		}
		return null;
	}

	public String info(String message, Object obj) {
		if (config.getLevel() >= 3) {
			return write("INFO", message, obj);
		}
		return null;
	}

	public String debug(String message, Object obj) {
		if (config.getLevel() >= 4) {
			return write("DEBUG", message, obj);
		}
		return null;
	}

	public String error(String message) {
		return error(message, null);
	}

	public String warning(String message) {
		return warning(message, null);
	}

	public String info(String message) {
		return info(message, null);
	}

	public String debug(String message) {
		return debug(message, null);
	}
}
