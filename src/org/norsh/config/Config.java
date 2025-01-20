package org.norsh.config;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import org.norsh.exceptions.InternalException;
import org.norsh.util.Converter;

import com.google.gson.reflect.TypeToken;

/**
 * Utility class for managing configuration settings loaded from a JSON file.
 * <p>
 * This class provides methods to load configurations into a thread-safe map and retrieve values with optional default
 * settings.
 * </p>
 *
 * @since 1.0.0
 * @version 1.0.0
 * @author Danthur Lice
 * @see <a href="https://docs.norsh.org">Norsh Documentation</a>
 */
public class Config {
	private final Map<String, Object> configMap = new ConcurrentHashMap<>();
	
	/**
	 * Initializes default localization settings.
	 * <p>
	 * This method sets the default locale to "en-US" and configures the time zone to GMT. These defaults ensure consistency
	 * in date formatting and time handling across all services.
	 * </p>
	 *
	 * @see NTP-4: Standards for Timestamps, Regionality, and Localization
	 */
	public static void initializeDefaultLocalization() {
		Locale locale = Locale.of("en", "US");
		Locale.setDefault(locale);
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
	}

	/**
	 * Loads configuration settings from a specified JSON file, using a default path and an environment variable.
	 * <p>
	 * This method attempts to load the configuration file from a default path. If the file is not found, 
	 * it checks for an alternative path provided by the specified environment variable. The loaded configuration 
	 * data is parsed into a map and used to update the internal configuration storage.
	 * </p>
	 *
	 * <h2>Loading Logic:</h2>
	 * <ul>
	 *   <li>Attempts to load the file from the path specified by {@code _default}.</li>
	 *   <li>If the default file is not found, checks the path specified in the {@code enviroment} variable.</li>
	 *   <li>Throws an {@link InternalException} if no valid configuration file is found.</li>
	 *   <li>Clears and updates the internal configuration map with the parsed data.</li>
	 * </ul>
	 *
	 * <h2>Example Usage:</h2>
	 * <pre>
	 *   Config config = new Config();
	 *   config.load("NORSH_API_CONFIG", "/etc/norsh/api.json");
	 * </pre>
	 *
	 * @param enviroment the name of the environment variable containing the alternative configuration file path.
	 * @param _default the default path for the configuration file.
	 * @throws InternalException if no configuration file is found or an error occurs during loading.
	 */

	public void load(String enviroment, String _default) {
		File file = new File(_default);

		if (!file.exists()) {
			String pathname = System.getenv(enviroment);

			if (pathname == null || !new File(pathname).exists()) {
				throw new InternalException("Configuration file not found.");
			}

			file = new File(pathname);
		}

		try {
			try (FileReader reader = new FileReader(file)) {
				Type type = new TypeToken<Map<String, Object>>() {}.getType();
				Map<String, Object> map = Converter.fromJson(reader, type);

				configMap.clear();
				configMap.putAll(map);
			}
		} catch (Exception e) {
			throw new InternalException("Failed to load configuration.", e);
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
	 * This method is useful for converting parts of the configuration (e.g., "log") into strongly typed objects.
	 * </p>
	 *
	 * @param key  the key corresponding to the nested configuration.
	 * @param type the class type to convert the configuration into.
	 * @param <T>  the type of the object to return.
	 * @return an object of the specified type representing the nested configuration, or {@code null} if the key is not
	 *         found.
	 */
	public <T> T getAsObject(String key, Class<T> type) {
		Object nestedConfig = configMap.get(key);
		if (nestedConfig == null) {
			return null;
		}
		return Converter.convert(nestedConfig, type);
	}

	/**
	 * Returns an unmodifiable view of all configuration settings.
	 * <p>
	 * This method provides a safe way to access configuration values without allowing modifications.
	 * </p>
	 *
	 * @return an unmodifiable map containing all configuration settings.
	 */
	public Map<String, Object> allConfigs() {
		return Collections.unmodifiableMap(configMap);
	}
	
	 /**
     * Retrieves the logging configuration.
     *
     * @return {@link LogConfig} object containing log settings.
     */
    public LogConfig getLogConfig() {
        return getAsObject("log", LogConfig.class);
    }
    
    /**
     * Retrieves the Kafka configuration.
     *
     * @return {@link KafkaConfig} object containing Kafka settings.
     */
    public KafkaConfig getKafkaConfig() {
        return getAsObject("kafka", KafkaConfig.class);
    }
    
    /**
     * Retrieves the Default configuration.
     *
     * @return {@link DefaultsConfig} object containing default settings.
     */
    public DefaultsConfig getDefaultsConfig() {
        return getAsObject("defaults", DefaultsConfig.class);
    }
    
    public MongoConfig getMongoConfig(String instance) {
        return getAsObject("mongo." + instance, MongoConfig.class);
    }
    
    /**
     * Retrieves the server properties as a {@link Properties} object.
     *
     * @return a {@link Properties} object containing server configuration.
     */
    @SuppressWarnings("unchecked")
    public Properties getServerProperties() {
        Properties properties = new Properties();
        Map<String, Object> serverConfig = (Map<String, Object>) get("framework.properties");

        if (serverConfig != null) {
            serverConfig.forEach(properties::put);
        }

        return properties;
    }
}
