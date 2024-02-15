package com.example.selectivehighschoolpreparationtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

// class for splash screen, calls main activity once complete
public class SplashScreen extends AppCompatActivity {

    ImageView splashImage;
    ScaleAnimation scaleImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();

        splashImage = findViewById(R.id.splashImage);
        splashImage.setVisibility(View.GONE);

        scaleImg = new ScaleAnimation(0, 1, 0,1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleImg.setDuration(1000);
        splashImage.startAnimation(scaleImg);
        splashImage.setVisibility(View.VISIBLE);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainLaunchIntent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(mainLaunchIntent);
                finish();
            }
        }, 5000);
    }
}