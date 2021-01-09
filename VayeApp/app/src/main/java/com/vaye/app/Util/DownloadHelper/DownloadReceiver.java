package com.vaye.app.Util.DownloadHelper;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class DownloadReceiver extends ResultReceiver {
    public DownloadReceiver(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        if (resultCode == DownloadService.UPDATE_PROGRESS) {
            int progress = resultData.getInt("progress");
           // mProgressDialog.setProgress(progress);
            if (progress == 100) {
             //   mProgressDialog.dismiss();
            }
        }
    }
}
