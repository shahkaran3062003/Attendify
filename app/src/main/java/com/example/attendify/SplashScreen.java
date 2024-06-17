package com.example.attendify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ScalableVideoView videoPlayer = findViewById(R.id.videoPlayer);

        String path = "android.resource://" + getPackageName() + "/" + R.raw.attendify_animation_trim;

        videoPlayer.setVideoURI(Uri.parse(path));
        videoPlayer.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), TeacherLogin.class));
                finish();
            }
        },7000);
    }
}