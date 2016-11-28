package net.doughughes.testifier.entity;

public class Config {
    private String instructor;
    private Long studentId;

    public Config(String instructor, Long studentId) {
        this.instructor = instructor;
        this.studentId = studentId;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
}
