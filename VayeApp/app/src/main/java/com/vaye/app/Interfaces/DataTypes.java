package com.vaye.app.Interfaces;

public interface DataTypes {
    // case image
    //    case pdf
    //    case doc
    //    case pptx
    //    case thumb
    //    case auido
    String image = "jpeg";
    String pdf = "pdf";
    String doc = "doc";
    String thumb = "jpeg";
    String audio = "m4a";

    interface  mimeType{
        String image = ".jpg";
        String pdf = ".pdf";
        String doc = ".doc";
        String thumb = ".jpg";
        String audio = ".m4a";
        String docx = ".docx";
    }
    interface  contentType {
        String image = "image/jpeg";
        String pdf = "application/pdf";
        String doc = "application/msword";
        String docx = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        String thumb = "image/jpeg";
        String audio = "audio/m4a";
    }
}
