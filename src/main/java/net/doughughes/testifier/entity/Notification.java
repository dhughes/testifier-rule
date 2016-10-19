package net.doughughes.testifier.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Notification {

    private String studentName;
    private String studentEmail;
    private String projectName;
    private String className;
    private String methodName;

    private String unitTestName;
    private String testMethodName;

    private String result;

    private List<String> arguments = new ArrayList<>();

    private String methodSource;
    private String constructorSource;
    private String classSource;

    private TestException exception = null;

    private String instructor;

    public Notification() {
    }

    public Notification(String studentName, String studentEmail, String projectName, String className, String methodName, Class[] arguments, String unitTestName, String testMethodName, String result, String methodSource, String constructorSource, String classSource, TestException exception, String instructor) {
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.projectName = projectName;
        this.className = className;
        this.methodName = methodName;
        this.unitTestName = unitTestName;
        this.testMethodName = testMethodName;
        this.result = result;
        this.methodSource = methodSource;
        this.constructorSource = constructorSource;
        this.classSource = classSource;
        this.exception = exception;
        this.instructor = instructor;

        // get the method arguments from the array of classes provided
        this.arguments = Arrays.stream(arguments).map(Class::getName).collect(Collectors.toList());

    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    public String getUnitTestName() {
        return unitTestName;
    }

    public void setUnitTestName(String unitTestName) {
        this.unitTestName = unitTestName;
    }

    public String getTestMethodName() {
        return testMethodName;
    }

    public void setTestMethodName(String testMethodName) {
        this.testMethodName = testMethodName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMethodSource() {
        return methodSource;
    }

    public void setMethodSource(String methodSource) {
        this.methodSource = methodSource;
    }

    public TestException getException() {
        return exception;
    }

    public void setException(TestException exception) {
        this.exception = exception;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getConstructorSource() {
        return constructorSource;
    }

    public void setConstructorSource(String constructorSource) {
        this.constructorSource = constructorSource;
    }

    public String getClassSource() {
        return classSource;
    }

    public void setClassSource(String classSource) {
        this.classSource = classSource;
    }
}
