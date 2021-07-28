package com.example.BuzzVoiceAndroid.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.BuzzVoiceAndroid.AccountActivity;
import com.example.BuzzVoiceAndroid.HomeActivity;
import com.example.BuzzVoiceAndroid.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore fireS;
    private String userID;
    private EditText emailTxt, passTxt, confirmTxt;
    private ImageButton signupBtn, goLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        fireS = FirebaseFirestore.getInstance();

        emailTxt = (EditText) findViewById(R.id.email);
        passTxt = (EditText) findViewById(R.id.pass);
        confirmTxt = (EditText) findViewById(R.id.passConfirm);
        signupBtn = (ImageButton) findViewById(R.id.signUp);
        goLoginBtn = (ImageButton) findViewById(R.id.goLogin);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });

        goLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginActivity();
            }
        });
    }

    private void signUpUser() {
        String email = emailTxt.getText().toString().trim();
        String pass = passTxt.getText().toString().trim();
        String conPass = confirmTxt.getText().toString().trim();

        if (email.isEmpty()) {
            emailTxt.setError("Email Address is required!");
            emailTxt.requestFocus();
            return;
        }
        if (pass.isEmpty()) {
            passTxt.setError("Password is required!");
            passTxt.requestFocus();
            return;
        }
        if (conPass.isEmpty()) {
            confirmTxt.setError("Please confirm password!");
            confirmTxt.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTxt.setError("Please use a valid email!");
            emailTxt.requestFocus();
            return;
        }
        if (pass.length() < 8) {
            passTxt.setError("Min password length should be 8 characters");
            passTxt.requestFocus();
            return;
        }
        if (!pass.equals(conPass)) {
            emailTxt.setError("Password does not match!");
            emailTxt.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                            userID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fireS.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("email", email);
                            user.put("password", pass);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "onSuccess: user Profile is created for " + userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));


                        }
                        else {
                            Toast.makeText(SignupActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT);
                        }
                    }
                });
    }

    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


}