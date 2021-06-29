package com.example.testsub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class HomeActivity extends AppCompatActivity {

    private ImageButton voiceBtn, accBtn, settBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        voiceBtn = (ImageButton) findViewById(R.id.VoiceBtn);
        accBtn = (ImageButton) findViewById(R.id.AccBtn);
        settBtn = (ImageButton) findViewById(R.id.SettBtn);

        voiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVoiceActivity();
            }
        });

        accBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAccountActivity();
            }
        });

        settBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsActivity();
            }
        });
    }

    public void openVoiceActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void openAccountActivity() {
        Intent intent = new Intent(this, AccountActivty.class);
        startActivity(intent);
    }

    public void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}