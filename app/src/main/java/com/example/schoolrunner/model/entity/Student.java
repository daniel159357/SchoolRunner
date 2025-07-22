package com.example.schoolrunner.model.entity;

public class Student {

    private Long id;

    private String studentNo;

    private String password;

    private String name;

    private Double averagePublisherScore; // 作为发布人的平均分
    private Double averageRunnerScore; // 作为接单人的平均分

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAveragePublisherScore() {
        return averagePublisherScore;
    }
    public void setAveragePublisherScore(Double averagePublisherScore) {
        this.averagePublisherScore = averagePublisherScore;
    }
    public Double getAverageRunnerScore() {
        return averageRunnerScore;
    }
    public void setAverageRunnerScore(Double averageRunnerScore) {
        this.averageRunnerScore = averageRunnerScore;
    }
}
