package com.vaye.app.Model;

import android.net.Uri;

public class NewPostDataModel {
    String dataName;
    Uri file;
    String fileUrl;
    String thumb_url;
    String mimeType;
    String contentType;

    public NewPostDataModel(String dataName, Uri file, String fileUrl, String thumb_url, String mimeType, String contentType) {
        this.dataName = dataName;
        this.file = file;
        this.fileUrl = fileUrl;
        this.thumb_url = thumb_url;
        this.mimeType = mimeType;
        this.contentType = contentType;
    }

    public NewPostDataModel() {
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public Uri getFile() {
        return file;
    }

    public void setFile(Uri file) {
        this.file = file;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
