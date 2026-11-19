
package com.kiet.studentdb;

public class Student {

    private int studentId;
    private String studentName;
    private String email;
    private String course;
    private double programmingScore;
    private double mathematicsScore;
    private double average;
    private String performance;
    private String strongerSubject;

    // No-argument constructor for JSON
    public Student() {
    }

    // Parameterized constructor
    public Student(int studentId, String studentName, String email,
                   String course, double programmingScore, double mathematicsScore) {

        this.studentId = studentId;
        this.studentName = studentName;
        this.email = email;
        this.course = course;
        this.programmingScore = programmingScore;
        this.mathematicsScore = mathematicsScore;

        calculateAverage();
        calculatePerformance();
        calculateStrongerSubject();
    }

    // Setters
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setProgrammingScore(double programmingScore) {
        this.programmingScore = programmingScore;
    }

    public void setMathematicsScore(double mathematicsScore) {
        this.mathematicsScore = mathematicsScore;
    }

    // Calculate average
    public void calculateAverage() {
        average = (programmingScore + mathematicsScore) / 2.0;
    }

    // Assign performance category
    public void calculatePerformance() {
        if (average >= 85)
            performance = "Excellent";
        else if (average >= 70)
            performance = "Good";
        else if (average >= 50)
            performance = "Satisfactory";
        else
            performance = "Needs Improvement";
    }

    // Find stronger subject
    public void calculateStrongerSubject() {
        if (programmingScore > mathematicsScore)
            strongerSubject = "Programming";
        else if (mathematicsScore > programmingScore)
            strongerSubject = "Mathematics";
        else
            strongerSubject = "Equal";
    }

    // Getters
    public int getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getEmail() {
        return email;
    }

    public String getCourse() {
        return course;
    }

    public double getProgrammingScore() {
        return programmingScore;
    }

    public double getMathematicsScore() {
        return mathematicsScore;
    }

    public double getAverage() {
        return average;
    }

    public String getPerformance() {
        return performance;
    }

    public String getStrongerSubject() {
        return strongerSubject;
    }
}
