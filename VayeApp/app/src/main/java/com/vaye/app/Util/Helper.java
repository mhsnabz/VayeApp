package com.vaye.app.Util;

import android.app.Activity;

import com.vaye.app.R;
import com.vaye.app.Services.UserService;

public class Helper {
    private static final Helper instance = new Helper();
    public static Helper shared() {  return instance;}
    public void back(Activity activity){
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    public void go(Activity activity){
        activity.overridePendingTransition(R.anim.slide_in_rigth, R.anim.slide_out_left);
    }
    public void slideDown(Activity activity){
        activity.overridePendingTransition(R.anim.bottom_up, R.anim.bottom_down);
    }
}
