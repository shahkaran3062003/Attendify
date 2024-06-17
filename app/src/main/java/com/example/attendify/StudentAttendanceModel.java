package com.example.attendify;

public class StudentAttendanceModel {
    String id;
    String name;
    String rollNo;


    String profilePicPath;

    String isPresent;

    public StudentAttendanceModel(String id, String name, String rollNo,  String profilePicPath){
        this.id = id;
        this.name = name;
        this.rollNo = rollNo;
        this.profilePicPath = profilePicPath;
        isPresent = "0";
    }

    public void setPresent(){
        isPresent = "1";
    }

    public void setAbsent(){
        isPresent = "0";
    }
}
