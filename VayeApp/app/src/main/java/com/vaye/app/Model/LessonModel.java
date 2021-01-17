package com.vaye.app.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class LessonModel implements Parcelable {
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

    protected LessonModel(Parcel in) {
        teacherEmail = in.readString();
        lesson_key = in.readString();
        lessonName = in.readString();
        teacherName = in.readString();
        teacherId = in.readString();
    }

    public static final Creator<LessonModel> CREATOR = new Creator<LessonModel>() {
        @Override
        public LessonModel createFromParcel(Parcel in) {
            return new LessonModel(in);
        }

        @Override
        public LessonModel[] newArray(int size) {
            return new LessonModel[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(teacherEmail);
        parcel.writeString(lesson_key);
        parcel.writeString(lessonName);
        parcel.writeString(teacherName);
        parcel.writeString(teacherId);
    }
}
