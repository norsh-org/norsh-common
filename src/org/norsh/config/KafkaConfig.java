package org.norsh.config;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration class for Kafka settings in the Norsh application.
 * <p>
 * This class encapsulates Kafka-related configurations, such as bootstrap servers, consumer group ID, and topic mappings.
 * It provides a centralized way to manage Kafka behavior throughout the application.
 * </p>
 *
 * <h2>Configuration Fields:</h2>
 * <ul>
 *   <li>{@link #bootstrapServers} - The Kafka broker addresses.</li>
 *   <li>{@link #groupId} - The consumer group ID for Kafka clients.</li>
 *   <li>{@link #topics} - A list of Kafka topics this application subscribes to.</li>
 * </ul>
 *
 * <h2>Example Configuration:</h2>
 * <pre>
 * {
 *   "kafka": {
 *     "bootstrapServers": "localhost:9092",
 *     "groupId": "norsh-group",
 *     "topics": ["norsh.smart_elements", "norsh.transactions"]
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
public class KafkaConfig {
    /** The Kafka bootstrap servers (comma-separated list of brokers). */
    private String bootstrapServers;

    /** The Kafka consumer group ID for message consumption. */
    private String groupId;

    /** List of Kafka topics this application subscribes to. */
    private List<String> topics;
}
