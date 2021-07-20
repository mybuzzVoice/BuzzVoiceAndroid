package com.example.BuzzVoiceAndroid;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import androidx.annotation.NonNull;

import java.util.concurrent.Executors;

public class Recorder {

    private static final int max_Speech_length_Mills = 30 * 1000;
    private static final int speech_Timeout_Millis = 2000;
    private static final int amplitude_Threshold = 1500;

    private static final int[] samp_Rate_Candi = new int[]{16000, 11025, 22050, 44100};
    private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    public static abstract class Callback {

        public void onVoiceBeg() { /** recorder hears voice **/
        }

        public void onVoice(byte[] data, int size) { /** recorder hearing voice **/

        }

        public void onVoiceFin() { /** recorder stops hearing voice **/

        }
    }

    private final Callback callback;
    private AudioRecord audioRecord;
    private Thread thread;
    private byte[] buffer;
    private final Object lock = new Object();
    private long lastVoiceHeard = Long.MAX_VALUE;
    private long voiceStarted;

    public Recorder(@NonNull Callback nCallback) {
        callback = nCallback;
    }

    public void start() {
        stop();
        audioRecord = createAudioRecord();
        if(audioRecord == null) {
            throw new RuntimeException("Cannot instantiate Recorder");
        }

        audioRecord.startRecording();
        thread = new Thread(new ProcessVoice());
        thread.start();
    }

    public void stop() {
        synchronized (lock) {
            dismiss();
            if (thread != null) {
                thread.interrupt();
                thread = null;
            }
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
            }
            buffer = null;
        }
    }

    public void dismiss() {
        if (lastVoiceHeard != Long.MAX_VALUE) {
            lastVoiceHeard = Long.MAX_VALUE;
            callback.onVoiceFin();
        }
    }

    public int getSampleRate() {
        if (audioRecord != null) {
            return audioRecord.getSampleRate();
        }
        return 0;
    }

    private AudioRecord createAudioRecord() {
        for (int sampleRate : samp_Rate_Candi) {
            final int byteSize = AudioRecord.getMinBufferSize(sampleRate, CHANNEL, ENCODING);
            if (byteSize == AudioRecord.ERROR_BAD_VALUE) {
                continue;
            }

            final AudioRecord nAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, CHANNEL, ENCODING, byteSize);
            if (nAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                buffer = new byte[byteSize];
                return nAudioRecord;
            }
            else {
                nAudioRecord.release();
            }
        }
        return null;
    }

    private class ProcessVoice implements Runnable {
        @Override
        public void run() {
            while(true) {
                synchronized (lock) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    final int size = audioRecord.read(buffer, 0, buffer.length);
                    final long now = System.currentTimeMillis();
                    if (hearingVoice(buffer, size)) {
                        if (lastVoiceHeard == Long.MAX_VALUE) {
                            voiceStarted = now;
                            callback.onVoiceBeg();
                        }
                        callback.onVoice(buffer, size);
                        lastVoiceHeard = now;
                        if (now - voiceStarted > max_Speech_length_Mills) {
                            end();
                        }
                    }
                    else if (lastVoiceHeard > speech_Timeout_Millis) {
                        end();
                    }
                }
            }
        }
    }

    private void end() {
        lastVoiceHeard = Long.MAX_VALUE;
        callback.onVoiceFin();
    }

    private boolean hearingVoice(byte[] buffer, int size) {
        for (int i = 0; i < size - 1; i += 2) {
            int j = buffer[i + 1];
            if (j < 0) j *= -1;
            j <<= 8;
            j += Math.abs(buffer[i]);
            if (j > amplitude_Threshold) {
                return true;
            }
        }
        return false;
    }
}
