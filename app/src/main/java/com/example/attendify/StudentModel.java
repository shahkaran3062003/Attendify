package com.example.attendify;

public class StudentModel {

    String id;
    String name;
    String rollNo;
    String contact;

    String profilePicPath;

    public StudentModel(String id,String name,String rollNo,String contact,String profilePicPath){
        this.id = id;
        this.name = name;
        this.rollNo = rollNo;
        this.contact = contact;
        this.profilePicPath = profilePicPath;
    }
}
