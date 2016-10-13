# testifier-rule

A jUnit rule that can be used to report test execution results in real time to a configurable REST endpoint.

This project was created for my work at The Iron Yard. I want a way to be able to monitor students work in real time in class. Students have a tendency to _say_ they're following, understanding, or doing the work, when they actually are not. 

This plugin is a jUnit rule which is notified on test success, failure, or errors. It wraps details of the test execution into a REST request which is posted to a configurable URL.

I've created another application (testifier-webapp) which can receive the notification and show data in realtime. This allows me to pinpoint students who have not been able to run tests, who are unable to make them pass, or who are trying randomly.

## To Use

Add this dependency to your pom.xml:

```xml
<repositories>
    <repository>
        <id>testifier-rule-mvn-repo</id>
        <url>https://raw.githubusercontent.com/dhughes/testifier-rule/mvn-repo/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>
```

## Note to Self:

To deploy updated versions of this dependency, use this maven command:

```bash
mvn clean deploy
```