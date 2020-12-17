package com.example.a117429464_ca2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class splash_screen extends AppCompatActivity {
    Animation topAnim , bottomAnim;
    ImageView ivLogo;
    TextView tvTracker, tvDesc;
    final int duration = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        attachResources();
        attachAnimations(topAnim, bottomAnim);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
               Intent intent = new Intent(splash_screen.this, MainActivity.class);
               startActivity(intent);
               finish();
            }
        }, duration);
    }

    private void attachAnimations(Animation topAnim, Animation bottomAnim) {
        ivLogo.setAnimation(topAnim);
        tvTracker.setAnimation(bottomAnim);
        tvDesc.setAnimation(bottomAnim);
    }

    private void attachResources() {
        ivLogo = findViewById(R.id.ivLogo);
        tvTracker = findViewById(R.id.tvTracker);
        tvDesc = findViewById(R.id.tvDesc);
    }
}