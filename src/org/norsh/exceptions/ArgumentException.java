package org.norsh.exceptions;

/**
 * Custom exception for invalid arguments.
 * <p>
 * This exception is intended to be thrown when an argument passed to a method
 * does not meet the required criteria. It extends {@link NorshException} to
 * integrate seamlessly with the Norsh platform's centralized error handling.
 * </p>
 * 
 * @license NCL-139
 * @since 2024
 * @version 1.0
 * @author Danthur Lice
 * @see NorshException
 */
public class ArgumentException extends NorshException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ArgumentException with the specified detail message.
     *
     * @param message the detail message, which can be retrieved later using
     *                {@link Throwable#getMessage()}.
     */
    public ArgumentException(String message) {
        super(message);
    }
}
