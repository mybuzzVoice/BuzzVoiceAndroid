package com.example.BuzzVoiceAndroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import com.example.BuzzVoiceAndroid.API.Recorder;
import com.example.BuzzVoiceAndroid.API.SpeechAPI;

public class HomeActivity extends AppCompatActivity implements MessageDialog.Listener {

    private ImageButton voiceBtn, accBtn, settBtn;

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
            //stat.setVisibility(View.VISIBLE);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            speechAPI = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        voiceBtn = (ImageButton) findViewById(R.id.VoiceBtn);
        accBtn = (ImageButton) findViewById(R.id.AccBtn);
        settBtn = (ImageButton) findViewById(R.id.SettBtn);

        final Resources resources = getResources();
        final Resources.Theme theme = getTheme();
        //colorHearing = ResourcesCompat.getColor(resources, R.color.stat, theme);
        //colorNotHearing = ResourcesCompat.getColor(resources, R.color.notStat, theme);
        //stat = (TextView) findViewById(R.id.stat);
        //text = (TextView) findViewById(R.id.text);

        voiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartRecording();
            }
        });

        accBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAccountActivity();
                onStopRecording();
            }
        });

        settBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsActivity();
                onStopRecording();
            }
        });
    }

    public void openVoiceActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void openAccountActivity() {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }

    public void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
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
        bindService(new Intent(this, SpeechAPI.class), serviceConnection, BIND_AUTO_CREATE);

        if (GrantedPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            onStartRecording();
        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            showPermissionMessageDialog();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_PERMISSION);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        onStopRecording();
        speechAPI.removeListener(listener);
        speechAPI.onDestroy();
        speechAPI = null;
        super.onStop();
    }

    private void showPermissionMessageDialog() {
        MessageDialog
                .newInstance(getString(R.string.permission_message))
                .show(getSupportFragmentManager(), fragmentDial);
    }

    @Override
    public void onMessageDismissed() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_RECORD_PERMISSION);
    }

}