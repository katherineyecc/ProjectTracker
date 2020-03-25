package com.example.projecttracker;

import java.util.Date;

public class Project implements java.io.Serializable{
    private String projectNumber;
    private String courseNumber;
    private String courseTitle;
    private String instructorName;
    private String projectDescription;
    private Date dueDate;
    private String studentNumber;
    private String studentName;
    private Boolean isCompleted = false;

    public String getProjectNumber(){
        return projectNumber;
    }

    public void setProjectNumber(String s){
        this.projectNumber = s;
    }

    public String getCourseNumber(){
        return courseNumber;
    }

    public void setCourseNumber(String s){
        this.courseNumber = s;
    }

    public String getCourseTitle(){
        return courseTitle;
    }

    public void setCourseTitle(String s){
        this.courseTitle = s;
    }

    public String getInstructorName(){
        return instructorName;
    }

    public void setInstructorName(String s){
        this.instructorName = s;
    }

    public Date getDueDate(){
        return dueDate;
    }

    public void setDueDate(Date d){
        this.dueDate = d;
    }

    public String getProjectDescription(){
        return projectDescription;
    }

    public void setProjectDescription(String s){
        this.projectDescription = s;
    }

    public String getStudentNumber(){
        return studentNumber;
    }

    public void setStudentNumber(String s){
        this.studentNumber = s;
    }

    public String getStudentName(){
        return studentName;
    }

    public void setStudentName(String s){
        this.studentName = s;
    }

    public Boolean getIsCompleted(){
        return isCompleted;
    }

    public void setIsCompleted(){
        this.isCompleted = true;
    }
}
