package com.vaye.app.Model;

import android.net.Uri;

public class UploadFiles {
    Uri fileUri;
    String type;

    public UploadFiles(Uri fileUri, String type) {
        this.fileUri = fileUri;
        this.type = type;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public String getType() {
        return type;
    }
}
