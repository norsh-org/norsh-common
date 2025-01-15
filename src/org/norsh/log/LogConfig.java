package org.norsh.log;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration class for log settings in the Norsh application.
 * <p>
 * This class encapsulates various logging-related configurations such as log output, storage location, file rotation, and log
 * levels. It provides a centralized way to manage log behavior throughout the application.
 * </p>
 *
 * @license NCL-139
 * @since 01/2025
 * @version 1.0
 * @author Danthur Lice
 * @see <a href="https://docs.norsh.org">Norsh Documentation</a>
 */
@Getter
@Setter
public class LogConfig { 
	/**  
	 * Determines if logs should be printed to the standard output (e.g., console).
	 * <p>
	 * If set to {@code true}, logs will be displayed in the system output.
	 * </p>
	 */ 
	private Boolean sysout;

	/**
	 * The directory path where log files are stored.
	 * <p>
	 * Logs will be saved in this specified directory, which must be writable. If the directory does not exist, the application will
	 * attempt to create it.
	 * </p>
	 */
	private String path;

	/**
	 * The prefix for log filenames.
	 * <p>
	 * This prefix is prepended to all log filenames, allowing easier identification of log files.
	 * </p>
	 */
	private String prefix;

	/**
	 * The date pattern used in log filenames.
	 * <p>
	 * Specifies the format of the date included in the log filename, typically used for organization and rotation. Example:
	 * {@code yyyy-MM-dd}.
	 * </p>
	 */
	private String datePattern;

	/**
	 * The maximum allowed size of a log file in bytes.
	 * <p>
	 * When the size of the current log file exceeds this limit, a new file is created based on the rotation policy.
	 * </p>
	 */
	private Integer maxLength;

	/**
	 * The maximum number of rows (entries) in a single log file.
	 * <p>
	 * When this limit is reached, the current log file is closed and a new file is created.
	 * </p>
	 */
	private Integer maxRows;

	/**
	 * Defines the logging level for filtering log messages.
	 * <h2>Logging Levels:</h2>
	 * <ul>
	 * <li>{@code 0}: SYS</li>
	 * <li>{@code 1}: EXCEPTION</li>
	 * <li>{@code 2}: ERROR</li>
	 * <li>{@code 3}: WARNING</li>
	 * <li>{@code 4}: INFO</li>
	 * <li>{@code 5}: TRACE</li>
	 * <li>{@code 6}: DEBUG</li>
	 * </ul>
	 */
	private Integer level;

	/**
	 * The time interval (in minutes) for rotating log files.
	 * <p>
	 * After this interval, the current log file is closed and a new one is created.
	 * </p>
	 */
	private Long rotationIntervalMinutes;
}
