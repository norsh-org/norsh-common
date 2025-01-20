package org.norsh.config;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration class for MongoDB settings in the Norsh application.
 * <p>
 * This class encapsulates MongoDB-related configurations, including connection details
 * such as connection string and database selection.
 * </p>
 *
 * <h2>Configuration Fields:</h2>
 * <ul>
 *   <li>{@link #connectionString} - The MongoDB connection string.</li>
 *   <li>{@link #database} - The name of the MongoDB database.</li>
 * </ul>
 *
 * <h2>Example Configuration:</h2>
 * <pre>
 * {
 *   "mongo": {
 *     "connectionString": "mongodb://localhost:27017",
 *     "database": "norsh"
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
public class MongoConfig {
    /** The MongoDB connection string. */
    private String connectionString;

    /** The MongoDB database name. */
    private String database;
}
