package com.example.email;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class ReadMail extends AppCompatActivity {


    private TextToSpeech tts;
    private boolean IsInitialVoiceFinshed;
    private int numberOfClicks;
    String res;

    String sub,fr,dt,to;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_mail);
        IsInitialVoiceFinshed = false ;
        sub=getIntent().getStringExtra("sub");
        fr=getIntent().getStringExtra("fr");
        dt=getIntent().getStringExtra("rr");


        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                    res="You Got a Mail from"+ fr +" Mail Subject is  "+sub+" Mail data is"+dt;
                    Toast.makeText(getApplicationContext(),""+fr+""+sub+""+dt+""+"",Toast.LENGTH_SHORT).show();


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tts.speak(res, TextToSpeech.QUEUE_FLUSH, null, null);
                    }else{
                        tts.speak(res, TextToSpeech.QUEUE_FLUSH, null);
                    }

                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });

    }


}