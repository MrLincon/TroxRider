package com.netro.troxrider.util;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.netro.troxrider.R;

public class Tools {

    public void setLightStatusBar(View view, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            activity.getWindow().setStatusBarColor(view.getResources().getColor(R.color.colorWhiteHighEmp));
        }
    }

    public void makeSnack(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void loading(Dialog popup, Boolean isShown) {

        popup.setContentView(R.layout.popup_loading);
        popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setCancelable(false);
        if (isShown){
            popup.show();
        }else {
            popup.dismiss();
        }

    }

    public void logMessage(String tag, String data){
        Log.d(tag, "logMessage: "+data);
    }
}
