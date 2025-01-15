# Norsh Common Library

The **Norsh Common Library** provides reusable components and utilities used across all modules in the Norsh platform. It serves as a foundational dependency for projects such as `norsh-blockchain` and `norsh-stream`.

## Features

This library contains core functionalities such as:
- **Logging**: Configurable and extensible logging utilities.
- **Security**: Tools for cryptographic operations, hashing, and signature validation.
- **Utilities**: General-purpose utilities for configuration, string handling, and data conversion.
- **Exception Handling**: Predefined exceptions for consistent error management.

### Key Components

1. **Exceptions**
   - `ValidationException`: A standardized exception for validation errors across modules.

2. **Logging**
   - `Log`: A robust logging utility with support for file-based and console logging.
   - `LogConfig`: Handles the configuration of logging settings, including paths, rotation policies, and levels.

3. **Security**
   - `Cryptography`: Provides encryption and decryption tools.
   - `Hasher`: Implements hashing algorithms like SHA-256 and SHA-3.
   - `Signature`: Facilitates signature creation and verification.

4. **Utilities**
   - `Config`: Manages application configuration loaded from JSON files.
   - `Converter`: Offers data conversion methods (e.g., Base64, Hexadecimal).
   - `Strings`: Includes helper methods for string manipulations.

## Prerequisites

- Java 23 or higher.
- Maven 3.13.0 or higher.

## Installation

To include the Norsh Common Library in your project, follow these steps:

### 1. Clone the repository
```bash
git clone https://github.com/your-org/norsh-common.git
cd norsh-common
```

### 2. Install the library
Run the following command to install the library into your local Maven repository:
```bash
mvn clean install
```

### 3. Add the dependency
Include the following dependency in your `pom.xml` file:
```xml
<dependency>
    <groupId>org.norsh</groupId>
    <artifactId>norsh-common</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

### Log Configuration

The Norsh Common module requires a configuration JSON file to define runtime parameters. Below is an example of the `config.json` file:

```json
{
  "log": {
    "sysout": true,
    "path": "/var/log/norsh/",
    "prefix": "norsh",
    "datePattern": "yyyy-MM-dd",
    "maxLength": 10485760,
    "maxRows": 10000,
    "level": 4,
    "rotationIntervalMinutes": 60
  }
}
```

#### Configuration Fields:

- **`log`**: Contains logging-related configurations:
  - **`sysout`**: If `true`, logs will also be printed to the console.
  - **`path`**: The directory where log files are stored.
  - **`prefix`**: Prefix added to log filenames for identification.
  - **`datePattern`**: Format for the date in log filenames (e.g., `norsh_2025-01-01.log`).
  - **`maxLength`**: Maximum size (in bytes) of a log file before rotation.
  - **`maxRows`**: Maximum number of log entries in a single log file.
  - **`level`**: Minimum log level to record:
    - `0`: SYSTEM
    - `1`: ERROR
    - `2`: WARNING
    - `3`: INFO
    - `4`: DEBUG
  - **`rotationIntervalMinutes`**: Time interval (in minutes) for rotating log files.

---

### Example: Logging
```java
import org.norsh.log.Log;
import org.norsh.log.LogConfig;
import org.norsh.util.Config;

public class Example {
    public static void main(String[] args) {
        try {
            // Load configuration from JSON file
            Config config = new Config();
            config.load("/etc/norsh/config.json");

            // Get LogConfig from the loaded configuration
            LogConfig logConfig = config.getAsObject("log", LogConfig.class);

            // Initialize the Log instance
            Log log = new Log(logConfig);

            // Example usage of the logger
            log.info("Application started.");
            log.error("An error occurred.", new Exception("Example exception"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### Example: Hashing
```java
import org.norsh.security.Hasher;

public class Example {
    public static void main(String[] args) {
        String hash = Hasher.sha256Hex("example-input");
        System.out.println("SHA-256 Hash: " + hash);
    }
}
```

### Example: Config
```java
import org.norsh.util.Config;

public class Example {
    public static void main(String[] args) {
        Config config = new Config();
        config.load("/etc/norsh/config.json");
        String value = config.get("key", "default-value");
        System.out.println("Config Value: " + value);
    }
}
```

## License

This project is licensed under the Norsh Commons License (NCL). For more details, refer to the [LICENSE-NCL-139](LICENSE) file.

## Contributing

Contributions are welcome! Please submit a pull request or open an issue for any suggestions or improvements.

---
**Maintainer**: Danthur Lice  
**Version**: 1.0.0  
**Since**: 2025
