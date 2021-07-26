package com.example.BuzzVoiceAndroid;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class HomeActivity extends AppCompatActivity {

    private ImageButton voiceBtn, accBtn, settBtn;


    private static final int REQUEST_RECORD_PERMISSION = 1;
    private TextView outputText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        voiceBtn = (ImageButton) findViewById(R.id.VoiceBtn);
        accBtn = (ImageButton) findViewById(R.id.AccBtn);
        settBtn = (ImageButton) findViewById(R.id.SettBtn);
        outputText = (TextView) findViewById(R.id.voiceTxt);


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
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_RECORD_PERMISSION);
                Log.i(TAG, "Device is Connected ++++++++++++ ");
        }
        else {
            Toast.makeText(getApplicationContext(), "Your device doesn't support voice software.", Toast.LENGTH_SHORT).show();
        }
    }

    public void openAccountActivity() {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }

    public void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestPerm, int resultPerm, Intent data) {
        super.onActivityResult(requestPerm, resultPerm, data);
        switch (requestPerm) {
            case REQUEST_RECORD_PERMISSION: {
                if (resultPerm == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    outputText.setText(result.get(0));
                }
                break;
            }
        }
    }
    



}