package com.example.BuzzVoiceAndroid.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;


import com.example.BuzzVoiceAndroid.Auth.LoginActivity;
import com.example.BuzzVoiceAndroid.Auth.SignupActivity;
import com.example.BuzzVoiceAndroid.HomeActivity;
import com.example.BuzzVoiceAndroid.R;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    private ImageButton loginBtn, signupBtn, guestBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtn = (ImageButton) findViewById(R.id.StartLogin);
        signupBtn = (ImageButton) findViewById(R.id.StartSignUp);
        guestBtn = (ImageButton) findViewById(R.id.GoAsGuest);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginActivity();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignupActivity();
            }
        });

        guestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomeActivity();
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();


    }

    public void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void openSignupActivity() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    public void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}