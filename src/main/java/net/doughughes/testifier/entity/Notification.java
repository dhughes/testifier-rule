package net.doughughes.testifier.entity;

public class Notification {

    private Long studentId;
    private String studentName;
    private String studentEmail;
    private String projectName;
    private String className;

    private String unitTestName;
    private String testMethodName;

    private String result;

    private String classSource;

    private TestException exception = null;

    private String instructor;

    public Notification() {
    }

    public Notification(Long studentId, String studentName, String studentEmail, String projectName, String className, String unitTestName, String testMethodName, String result, String classSource, TestException exception, String instructor) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.projectName = projectName;
        this.className = className;
        this.unitTestName = unitTestName;
        this.testMethodName = testMethodName;
        this.result = result;
        this.classSource = classSource;
        this.exception = exception;
        this.instructor = instructor;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
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

    public String getClassSource() {
        return classSource;
    }

    public void setClassSource(String classSource) {
        this.classSource = classSource;
    }
}
