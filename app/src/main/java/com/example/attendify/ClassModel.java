package com.example.attendify;

public class ClassModel {

    String classId;
    String classTitle;
    String classBranch;
    String classSem;
    String port;
    String studentCount;

    public ClassModel(String classId,String classTitle,String classBranch, String classSem,String port,String studentCount){
        this.classId = classId;
        this.classTitle = classTitle;
        this.classBranch = classBranch;
        this.classSem = classSem;
        this.port = port;
        this.studentCount = studentCount;
    }
}
