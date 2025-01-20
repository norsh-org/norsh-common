package org.norsh.config;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration class for default system settings in the Norsh application.
 * <p>
 * This class encapsulates default configurations such as semaphore timeouts, 
 * retry thresholds, and backoff settings for threading operations.
 * </p>
 *
 * <h2>Configuration Fields:</h2>
 * <ul>
 *   <li>{@link #semaphoreLockTimeoutMs} - Maximum timeout for semaphore locks in milliseconds.</li>
 *   <li>{@link #semaphoreRetryThreshold} - Number of retries before failing a semaphore operation.</li>
 *   <li>{@link #threadInitialBackoffMs} - Initial backoff time in milliseconds for thread retries.</li>
 *   <li>{@link #threadMaxBackoffMs} - Maximum backoff time in milliseconds for thread retries.</li>
 * </ul>
 *
 * <h2>Example Configuration:</h2>
 * <pre>
 * {
 *   "defaults": {
 *     "semaphoreLockTimeoutMs": 5000,
 *     "semaphoreRetryThreshold": 3,
 *     "threadInitialBackoffMs": 100,
 *     "threadMaxBackoffMs": 2000
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
public class DefaultsConfig {
    
    /** Maximum timeout for semaphore locks in milliseconds. */
    private int semaphoreLockTimeoutMs;

    /** Number of retries before failing a semaphore operation. */
    private int semaphoreRetryThreshold;

    /** Initial backoff time in milliseconds for thread retries. */
    private long threadInitialBackoffMs;

    /** Maximum backoff time in milliseconds for thread retries. */
    private long threadMaxBackoffMs;
}
