package com.vaye.app.Model;

public class LessonModel {
    //   var teacherEmail : String!
    //    var teacherId : String!
    //    var teacherName : String!
    //    var lessonName : String!
    //    var lesson_key : String!
    String  teacherEmail,lesson_key,lessonName,teacherName,teacherId;

    public LessonModel() {
    }

    public LessonModel(String teacherEmail, String lesson_key, String lessonName, String teacherName, String teacherId) {
        this.teacherEmail = teacherEmail;
        this.lesson_key = lesson_key;
        this.lessonName = lessonName;
        this.teacherName = teacherName;
        this.teacherId = teacherId;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
    }

    public String getLesson_key() {
        return lesson_key;
    }

    public void setLesson_key(String lesson_key) {
        this.lesson_key = lesson_key;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }
}
