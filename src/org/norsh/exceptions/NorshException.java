package org.norsh.exceptions;

import java.util.List;

/**
 * Custom exception for validation and general application-level errors.
 * <p>
 * This exception is intended for use throughout the Norsh platform to handle
 * common errors in a consistent and structured manner.
 * </p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Supports custom error messages.</li>
 *   <li>Includes support for storing additional details related to the exception.</li>
 *   <li>Allows chaining of exceptions via a cause.</li>
 * </ul>
 *
 * @license NCL-139
 * @since 12/2024
 * @version 1.0
 * @author Danthur Lice
 * @see <a href="https://docs.norsh.org">Norsh Documentation</a>
 */
public class NorshException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * A list of additional details related to the exception.
     * <p>
     * This field can be used to provide more context or supplementary information
     * about the error.
     * </p>
     */
    private List<String> details;

    /**
     * Constructs a new {@code NorshException} with no detail message.
     */
    public NorshException() {
        super();
    }

    /**
     * Constructs a new {@code NorshException} with the specified detail message.
     *
     * @param message the detail message, which can be retrieved later using
     *                {@link Throwable#getMessage()}.
     */
    public NorshException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code NorshException} with the specified detail message
     * and cause.
     *
     * @param message the detail message.
     * @param cause   the cause of the exception, which can be retrieved later using
     *                {@link Throwable#getCause()}.
     */
    public NorshException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Retrieves the additional details associated with this exception.
     * <p>
     * If no details have been set, this method returns an empty list.
     * </p>
     *
     * @return a list of details related to the exception.
     */
    public List<String> getDetails() {
        return details == null ? List.of() : details;
    }

    /**
     * Sets additional details for this exception.
     * <p>
     * This method allows adding context or supplementary information to the exception.
     * </p>
     *
     * @param details a list of details to associate with this exception.
     */
    public void setDetails(List<String> details) {
        this.details = details;
    }
}
