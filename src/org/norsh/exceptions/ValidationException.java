package org.norsh.exceptions;

/**
 * Custom exception for validation errors.
 * <p>
 * This exception is intended to be thrown when a validation check fails.
 * It extends {@link NorshException} to integrate seamlessly with the
 * Norsh platform's centralized error handling.
 * </p>
 * 
 * @license NCL-139
 * @since 2024
 * @version 1.0
 * @author Danthur Lice
 * @see NorshException
 */
public class ValidationException extends NorshException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ValidationException with the specified detail message.
     *
     * @param message the detail message, which can be retrieved later using
     *                {@link Throwable#getMessage()}.
     */
    public ValidationException(String message) {
        super(message);
    }
}
