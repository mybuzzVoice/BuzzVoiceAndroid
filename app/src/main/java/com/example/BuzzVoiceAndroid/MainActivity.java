package com.example.BuzzVoiceAndroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private ImageButton loginBtn, signupBtn, guestBtn;
    private SpeechAPI speechAPI;
    private static final String stateResult = "results";
    private static final String fragmentDial = "Message_dialog";
    private static final int REQUEST_RECORD_PERMISSION = 1;
    private Recorder recorder;
    private final Recorder.Callback callback = new Recorder.Callback() {
        @Override
        public void onVoiceBeg() {
            //showStat(true);
            if (speechAPI != null) {
                speechAPI.startRecognizing(recorder.getSampleRate());
            }
        }

        @Override
        public void onVoice(byte[] data, int size) {
            if (speechAPI != null) {
                speechAPI.recognize(data, size);
            }
        }

        @Override
        public void onVoiceFin() {
            //showStat(false);
            if (speechAPI != null) {
                speechAPI.finishRecognizing();
            }
        }
    };

    private int colorHearing;
    private int colorNotHearing;
    private TextView stat;
    private TextView textView;

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            speechAPI = SpeechAPI.from(binder);
            speechAPI.addListener(listener);
            stat.setVisibility(View.VISIBLE);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            speechAPI = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtn = (ImageButton) findViewById(R.id.StartLogin);
        signupBtn = (ImageButton) findViewById(R.id.StartSignUp);
        guestBtn = (ImageButton) findViewById(R.id.GoAsGuest);

        final Resources resources = getResources();
        final Resources.Theme theme = getTheme();
        //colorHearing = ResourcesCompat.getColor(resources, R.color.stat, theme);
        //colorNotHearing = ResourcesCompat.getColor(resources, R.color.notStat, theme);
        //stat = (TextView) findViewById(R.id.stat);
        //text = (TextView) findViewById(R.id.text);
        
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

    private int GrantedPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission);
    }

    private void makeRequest(String permission) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_RECORD_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_PERMISSION) {
            if (grantResults.length == 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                finish();
            } else {
                onStartRecording();
            }
        }
    }
    private final SpeechAPI.Listener listener = new SpeechAPI.Listener() {
        @Override
        public void onSpeechRecognized(String text, boolean isFinal) {
            if (isFinal) {
                recorder.dismiss();
            }
            if (textView != null && !TextUtils.isEmpty(text)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isFinal) {
                            textView.setText(null);
                        }
                        else {
                            textView.setText(text);
                            textView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }
    };

    private void onStartRecording() {
        if (recorder != null) {
            recorder.stop();
        }
        recorder = new Recorder(callback);
        recorder.start();
    }

    private void onStopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (GrantedPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            onStartRecording();
        }
        else {
            makeRequest(Manifest.permission.RECORD_AUDIO);
        }
        speechAPI.addListener(listener);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        onStopRecording();
        speechAPI.removeListener(listener);
        //speechAPI.destroy();
        speechAPI = null;
        super.onStop();
    }
}