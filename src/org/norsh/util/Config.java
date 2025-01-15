package org.norsh.util;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.reflect.TypeToken;

/**
 * Utility class for managing configuration settings loaded from a JSON file.
 * <p>
 * This class provides methods to load configurations into a thread-safe map
 * and retrieve values with optional default settings.
 * </p>
 *
 * @license NCL-R
 * @since 01/2025
 * @version 1.0
 * @author Danthur Lice
 * @see <a href="https://docs.norsh.org">Norsh Documentation</a>
 */
public class Config {
    private final Map<String, Object> configMap = new ConcurrentHashMap<>();

    /**
     * Loads configuration settings from a specified JSON file.
     * <p>
     * This method reads the JSON file, parses its contents into a map, and updates
     * the internal configuration map.
     * </p>
     *
     * @param path the path to the JSON configuration file.
     * @throws IOException if an error occurs while reading the file.
     */
    public void load(String path) throws IOException {
        try (FileReader reader = new FileReader(path)) {
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> map = Converter.fromJson(reader, type);
            configMap.clear();
            configMap.putAll(map);
        }
    }

    /**
     * Retrieves the value associated with a key, or returns a default value if the key is not present.
     *
     * @param key          the key to look up in the configuration map.
     * @param defaultValue the default value to return if the key is not found.
     * @param <T>          the expected type of the value.
     * @return the value associated with the key, or the default value if the key is not found.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        return (T) configMap.getOrDefault(key, defaultValue);
    }

    /**
     * Retrieves the value associated with a key, or {@code null} if the key is not present.
     *
     * @param key the key to look up in the configuration map.
     * @param <T> the expected type of the value.
     * @return the value associated with the key, or {@code null} if the key is not found.
     */
    public <T> T get(String key) {
        return get(key, null);
    }
    
    /**
     * Retrieves a nested configuration as an object of a specified type.
     * <p>
     * This method is useful for converting parts of the configuration (e.g., "log")
     * into strongly typed objects.
     * </p>
     *
     * @param key   the key corresponding to the nested configuration.
     * @param type the class type to convert the configuration into.
     * @param <T>   the type of the object to return.
     * @return an object of the specified type representing the nested configuration,
     * or {@code null} if the key is not found.
     */
    public <T> T getAsObject(String key, Class<T> type) {
        Object nestedConfig = configMap.get(key);
        if (nestedConfig == null) {
            return null;
        }
        // Convert the nested map or object into the desired type
        String json = Converter.toJson(nestedConfig);
        return Converter.fromJson(json, type);
    }
}
