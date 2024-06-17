package com.example.attendify;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

public class CustomProgressBar {
    private Dialog progressBarDialog;
    private ProgressBar progressBar;

    public CustomProgressBar(Activity activity){
        progressBarDialog = new Dialog(activity);
        progressBarDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        progressBar = new ProgressBar(activity);
        progressBar.setIndeterminateDrawable(activity.getDrawable(R.drawable.progress_bar));
        progressBar.setIndeterminate(true);
        progressBar.setProgress(0);

        progressBarDialog.setContentView(progressBar);
        progressBarDialog.setCancelable(false);


        WindowManager.LayoutParams params = progressBarDialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        progressBarDialog.getWindow().setAttributes(params);

    }

    public void show(){
        if(progressBarDialog != null && !progressBarDialog.isShowing()){
            progressBarDialog.show();
        }
    }

    public void hide(){
        if(progressBarDialog != null && progressBarDialog.isShowing()){
            progressBarDialog.dismiss();
        }
    }
}
