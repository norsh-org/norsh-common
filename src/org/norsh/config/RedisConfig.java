package org.norsh.config;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration class for Redis settings in the Norsh application.
 * <p>
 * This class encapsulates Redis-related configurations, including connection details
 * such as host, port, authentication credentials, and database selection.
 * </p>
 *
 * <h2>Configuration Fields:</h2>
 * <ul>
 *   <li>{@link #host} - The Redis server host.</li>
 *   <li>{@link #port} - The Redis server port.</li>
 *   <li>{@link #username} - The username for Redis authentication (if applicable).</li>
 *   <li>{@link #password} - The password for Redis authentication.</li>
 *   <li>{@link #database} - The Redis database index (default: 0).</li>
 * </ul>
 *
 * <h2>Example Configuration:</h2>
 * <pre>
 * {
 *   "redis": {
 *     "host": "localhost",
 *     "port": 6379,
 *     "username": "redisUser",
 *     "password": "securePass",
 *     "database": 0
 *   }
 * }
 * </pre>
 *
 * @since 1.0.0
 * @version 1.0.0
 * @author Danthur Lice
 * @see <a href="https://docs.norsh.org">Norsh Documentation</a>
 */
@Getter
@Setter
public class RedisConfig {
    /** The Redis server host. */
    private String host;

    /** The Redis server port. */
    private Integer port;

    /** The username for Redis authentication (optional, depends on Redis setup). */
    private String username;

    /** The password for Redis authentication (optional, depends on Redis setup). */
    private String password;

    /** The Redis database index to use. */
    private Integer database;
}
