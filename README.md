# machine-automation
  Machine Automation is a Scheduler Application and Calculated the Machine Alarm Re-Start & Active.
  
## Requirements
For building and running the application you need:
- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven 3](https://maven.apache.org)
## Running the application locally
here are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method
in the `org.automation.AutomationLuncher` class from your IDE.
Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn spring-boot:run
```

## Run application on production server using jar file and required below commands to execute on prompt or terminal
- Run below command with interaction mode with external application proerties
```shell
java -jar machine-automation.jar -DSpring.config.location=application.properties
```
- Run below command without interaction mode & run in background
```shell
javaw -Dspring.config.location=application.properties -jar machine-automation.jar 
```
