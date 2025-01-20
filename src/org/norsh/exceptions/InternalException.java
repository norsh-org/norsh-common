package org.norsh.exceptions;

/**
 * Custom exception for internal errors within the Norsh platform.
 * <p>
 * This exception is intended to represent unexpected internal errors or failures
 * that occur during the execution of the application. It extends {@link NorshException}
 * to integrate with the platform's centralized error-handling framework.
 * </p>
 * 
 * <h2>Usage:</h2>
 * <p>
 * This exception can be used to wrap lower-level exceptions, providing additional
 * context about the error and ensuring consistent error handling throughout the platform.
 * </p>
 * 
 * @since 1.0.0
 * @version 1.0.0
 * @author Danthur Lice
 * @see <a href="https://docs.norsh.org">Norsh Documentation</a>
 * @see NorshException
 */
public class InternalException extends NorshException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new InternalException with the specified detail message.
     *
     * @param message the detail message, which can be retrieved later using
     *                {@link Throwable#getMessage()}.
     */
    public InternalException(String message) {
        super(message);
    }

    /**
     * Constructs a new InternalException with the specified detail message and cause.
     * 
     * @param message the detail message, which can be retrieved later using
     *                {@link Throwable#getMessage()}.
     * @param cause   the cause of the exception, which can be retrieved later
     *                using {@link Throwable#getCause()}.
     */
    public InternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
