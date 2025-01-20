package org.norsh.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for string manipulation and concatenation.
 * <p>
 * Provides functionality to concatenate various types of objects, including simple objects, collections, and maps, into a
 * single string with a "|" delimiter.
 * </p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Handles simple objects by converting them to strings.</li>
 *   <li>Processes collections by concatenating their elements.</li>
 *   <li>Processes maps by concatenating entries as "key|value" pairs.</li>
 *   <li>Handles {@code null} values gracefully by treating them as empty strings.</li>
 * </ul>
 *
 * @since 1.0.0
 * @version 1.0.0
 * @author Danthur Lice
 * @version 1.0
 * @see <a href="https://docs.norsh.org">Norsh Documentation</a>
 */
public class Strings {
	/**
     * Concatenates the provided objects into a single string, separating elements with "|".
     *
     * <p>
     * Handles a variety of object types, including:
     * <ul>
     *   <li>Simple objects: Converts to string.</li>
     *   <li>Collections: Concatenates elements with "|".</li>
     *   <li>Maps: Concatenates entries as "key|value" pairs, separated by "|".</li>
     * </ul>
     * {@code null} values are handled gracefully by converting them to empty strings.
     * </p>
     *
     * @param values the objects to be concatenated.
     * @return the concatenated string.
     */
    public static String concatenate(Object... values) {
        if (values == null) {
            values = new Object[0];
        }

        return Arrays.stream(values).map(o -> {
            if (o == null) {
                return ""; // Handle null values
            }
            if (o instanceof Collection) {
                // Concatenate elements of a collection with "|"
                return ((Collection<?>) o).stream()
                        .map(e -> e == null ? "" : e.toString()) // Handle null in collections
                        .collect(Collectors.joining("|"));
            }
            if (o instanceof Map) {
                // Concatenate entries of a map as "key|value" with "|"
                return ((Map<?, ?>) o).entrySet().stream()
                        .map(e -> {
                            String key = e.getKey() == null ? "" : e.getKey().toString();
                            String value = e.getValue() == null ? "" : e.getValue().toString();
                            return key + "|" + value;
                        })
                        .collect(Collectors.joining("|"));
            }
            return o.toString(); // Convert other objects to string
        }).collect(Collectors.joining("|")); // Concatenate with "|"
    }
}
