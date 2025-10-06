# Orqueio BPM - Getting Started with DMN

This project is a simple example of using the **Orqueio BPM platform** to execute **DMN (Decision Model and Notation)** decision tables within a Java application.


## Prerequisites
* Java 17 or 21  
* Orqueio BPM server (Tomcat-based)

## Setup

1. Add the following dependencies to your `pom.xml`:

```xml
<dependency>
  <groupId>io.orqueio.bpm</groupId>
  <artifactId>orqueio-engine</artifactId>
  <scope>provided</scope>
</dependency>
<dependency>
  <groupId>jakarta.servlet</groupId>
  <artifactId>jakarta.servlet-api</artifactId>
  <version>6.0.0</version>
  <scope>provided</scope>
</dependency>
```


2. Create a processes.xml file in src/main/resources/META-INF
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<process-application
    xmlns="http://www.orqueio.io/schema/1.0/ProcessApplication"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <process-archive name="dinner-dmn">
    <process-engine>default</process-engine>
    <properties>
      <property name="isDeleteUponUndeploy">false</property>
      <property name="isScanForProcessDefinitions">true</property>
    </properties>
  </process-archive>
</process-application> 
```
This file tells Orqueio BPM to automatically deploy all DMN/BPMN/CMMN files in your project.

3. Create a main process application class annotated with `@ProcessApplication`. This class defines your application and contains logic to evaluate decision tables or initialize processes after deployment. It is the central entry point for your BPMN/DMN integration and ensures that the engine can detect and run your processes.
```java
@ProcessApplication("Dinner App DMN")
public class DinnerApplication extends JakartaServletProcessApplication
{
    protected final static Logger LOGGER = Logger.getLogger(DinnerApplication.class.getName());
    @PostDeploy
    public void evaluateDecisionTable(ProcessEngine processEngine) {
      DecisionService decisionService = processEngine.getDecisionService();
      VariableMap variables = Variables.createVariables()
        .putValue("season", "Spring")
        .putValue("guestCount", 10)
        .putValue("guestsWithChildren", false);
      DmnDecisionTableResult dishDecisionResult = decisionService.evaluateDecisionTableByKey("dish", variables);
      String desiredDish = dishDecisionResult.getSingleEntry();
      LOGGER.log(Level.INFO, "\n\nDesired dish: {0}\n\n", desiredDish);
      DmnDecisionTableResult beveragesDecisionResult = decisionService.evaluateDecisionTableByKey("beverages", variables);
      List<Object> beverages = beveragesDecisionResult.collectEntries("beverages");
      LOGGER.log(Level.INFO, "\n\nDesired beverages: {0}\n\n", beverages);
    }
}
```

4. You can also put additional BPMN, CMMN and DMN files in your classpath, they will be automatically deployed and 
registered within the process application. Forms HTML needs to be added in the `/resources/static/forms` directory.

5. Run the application and use Orqueio Platform
    * Build the application
    ```bash
    mvn clean install
    ```
    * Deploy the WAR
    Copy the generated WAR file from the target folder into the webapps/ directory of your Orqueio Tomcat server.
    * Start the server
     ```bash
    ./start-orqueio.sh
    ```
    * Access the application
    Then you can access the Orqueio Webapps in your browser: `http://localhost:8080/`