package com.example.musicapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.musicapplication.R;
import com.google.firebase.auth.FirebaseAuth;

public class splashScreen extends AppCompatActivity {
    private Handler handler;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        firebaseAuth = FirebaseAuth.getInstance();
        handler =new Handler();
        handler.postDelayed(() -> {
            Intent intent;
            if(firebaseAuth.getCurrentUser() != null){
                intent = new Intent(getApplicationContext(),MainActivity.class);
            }else {
                intent = new Intent(getApplicationContext(),loginActivity.class);
            }
            startActivity(intent);
            finish();
        },3000);
    }
}