# OrqueIO BPM - Getting Started with DMN

A Spring Boot application demonstrating how to use the **OrqueIO BPM Platform** to execute **DMN (Decision Model and Notation)** decision tables for delivery carrier selection based on destination, package weight, and delivery type.

## Features

- ✅ Spring Boot integration with OrqueIO BPM 1.0.3
- ✅ Automatic DMN deployment and evaluation
- ✅ Embedded H2 database (no external database required)
- ✅ Built-in web applications (Tasklist, Cockpit, Admin)
- ✅ REST API for process automation
- ✅ Event-driven DMN evaluation using Spring events

## Prerequisites

- **Java 21** (required)
- **Maven 3.9+**

## Quick Start

### 1. Build the Application

```bash
mvn clean install
```

### 2. Run the Application

```bash
# On Linux/Mac
export JAVA_HOME="/path/to/jdk-21"
mvn spring-boot:run

# On Windows (PowerShell)
$env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-21.0.8.9-hotspot"
mvn spring-boot:run

# On Windows (CMD)
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.8.9-hotspot
mvn spring-boot:run
```

### 3. Access the Application

- **Web Applications**: http://localhost:8080/
  - Username: `demo`
  - Password: `demo`
  - Available apps: Tasklist, Cockpit, Admin

- **REST API**: http://localhost:8080/engine-rest

## Project Structure

```
src/
├── main/
│   ├── java/io/orqueio/bpm/getstarted/dmn/
│   │   └── Application.java              # Main Spring Boot application
│   └── resources/
│       ├── META-INF/
│       │   └── processes.xml             # Process application descriptor
│       ├── application.yaml              # Spring Boot configuration
│       └── dinnerDecisions.dmn           # DMN decision table
```

## How It Works

### 1. Main Application Class

The `Application.java` class is the entry point and uses Spring Boot with OrqueIO integration:

```java
@SpringBootApplication
@EnableProcessApplication
public class Application {

    @Autowired
    private DecisionService decisionService;

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    @EventListener
    public void processPostDeploy(PostDeployEvent event) {
        VariableMap variables = Variables.createVariables()
                .putValue("paysDestination", "France")
                .putValue("poidsColis", 6.0)
                .putValue("typeLivraison", "Standard");

        DmnDecisionTableResult carrierDecisionResult =
            decisionService.evaluateDecisionTableByKey("carrier", variables);
        String transporteur = carrierDecisionResult.getSingleEntry();

        LOGGER.log(Level.INFO, "\n\nSelected carrier: {0}\n\n", transporteur);
    }
}
```

**Key Annotations:**
- `@SpringBootApplication`: Enables Spring Boot auto-configuration
- `@EnableProcessApplication`: Activates OrqueIO process application features
- `@EventListener`: Reacts to `PostDeployEvent` after DMN deployment

### 2. DMN Decision Table

The `dinnerDecisions.dmn` file contains the carrier selection logic:

- **Input Variables**:
  - `paysDestination`: Destination country (e.g., "France", "Germany")
  - `poidsColis`: Package weight in kg
  - `typeLivraison`: Delivery type ("Express", "Standard")

- **Output**: Carrier name (e.g., "Chronopost", "Colissimo")

### 3. Process Application Descriptor

The `processes.xml` file marks the application as an OrqueIO process application:

```xml
<process-application xmlns="http://www.camunda.org/schema/1.0/ProcessApplication">
    <process-archive name="carrier-dmn">
        <process-engine>default</process-engine>
        <properties>
            <property name="isDeleteUponUndeploy">false</property>
            <property name="isScanForProcessDefinitions">true</property>
        </properties>
    </process-archive>
</process-application>
```

### 4. Configuration

The `application.yaml` configures the admin user and task filters:

```yaml
orqueio.bpm:
  admin-user:
    id: demo
    password: demo
    firstName: Demo
  filter:
    create: All tasks
```

## Dependencies

The project uses OrqueIO Spring Boot starters for automatic configuration:

```xml
<dependencies>
    <!-- OrqueIO Web Applications (Tasklist, Cockpit, Admin) -->
    <dependency>
        <groupId>io.orqueio.bpm.springboot</groupId>
        <artifactId>orqueio-bpm-spring-boot-starter-webapp</artifactId>
    </dependency>

    <!-- OrqueIO REST API -->
    <dependency>
        <groupId>io.orqueio.bpm.springboot</groupId>
        <artifactId>orqueio-bpm-spring-boot-starter-rest</artifactId>
    </dependency>

    <!-- H2 Database -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
    </dependency>
</dependencies>
```

## Customization

### Adding New DMN Files

1. Place `.dmn` files in `src/main/resources/`
2. They will be automatically deployed at startup

### Changing Decision Variables

Modify the `processPostDeploy` method in `Application.java`:

```java
VariableMap variables = Variables.createVariables()
    .putValue("paysDestination", "Germany")
    .putValue("poidsColis", 10.0)
    .putValue("typeLivraison", "Express");
```

### Using External Database

Update `application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/orqueio
    username: your_username
    password: your_password
    driver-class-name: org.postgresql.Driver
```

## Technology Stack

- **OrqueIO BPM**: 1.0.3
- **Spring Boot**: 3.5.9
- **Java**: 21
- **Database**: H2 (embedded, can be replaced)
- **Build Tool**: Maven

## Troubleshooting

### Java Version Error

If you get "release version 21 not supported", ensure Maven uses Java 21:

```bash
export JAVA_HOME="/path/to/jdk-21"
mvn --version  # Verify Java version
```

### Port Already in Use

Change the port in `application.yaml`:

```yaml
server:
  port: 8081
```

## License

This project follows OrqueIO BPM licensing terms.