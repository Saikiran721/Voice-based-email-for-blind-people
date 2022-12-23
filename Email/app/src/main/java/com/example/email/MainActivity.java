package com.example.email;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private TextToSpeech tts;
    private boolean IsInitialVoiceFinshed;
    private int numberOfClicks;
    static  String em;
    String res;


    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Phone = "phoneKey";
    public static final String eml = "eml";
    static  String m,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IsInitialVoiceFinshed = false ;
        em=getIntent().getStringExtra("em");

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if (sharedpreferences.contains(Phone)) {

            m=sharedpreferences.getString(Phone, "");
            email=sharedpreferences.getString(eml, "");
            //Toast.makeText(this, ""+email, Toast.LENGTH_SHORT).show();

        }else {

            Intent i = getIntent();
            m = i.getStringExtra("mob");
            email = i.getStringExtra("em");
        }

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                    res ="Welcome to Voice Assistant";
                    speak(res);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            IsInitialVoiceFinshed=true;
                        }
                    }, 10000);
                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });


    }

    private void speak(String res) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(res, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(res, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onDestroy() {
        if (tts != null) {

            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100&& IsInitialVoiceFinshed){
            IsInitialVoiceFinshed = false;
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);


                 if(result.get(0).equalsIgnoreCase("Inbox")){
                    inboxspeek();
                }
                else if(result.get(0).equalsIgnoreCase("Send")){
                    sendspeek();
                }
                else if(result.get(0).equalsIgnoreCase("Favourite")){
                    favspeek();
                }
                else if(result.get(0).equalsIgnoreCase("Delete")){
                    delspeek();
                }

            }
            else {
                switch (numberOfClicks) {
                    case 1:
                        speak("Inbox");
                        break;
                    case 2:
                        speak("Send");
                        break;
                    case 3:
                        speak("Favourite");
                        break;
                    case 4:
                        speak("Delete");
                        break;
                    default:
                        speak("say yes or no");
                        break;
                }
                numberOfClicks--;
            }
        }
        IsInitialVoiceFinshed=true;
    }

    private void delspeek() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak("Your Assistant redirecting Delete", TextToSpeech.QUEUE_FLUSH, null, null);

            startActivity(new Intent(getApplicationContext(),Delete.class));

        }else{
            tts.speak("Your Assistant redirecting Delete", TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void favspeek() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak("Your Assistant redirecting Favourite box", TextToSpeech.QUEUE_FLUSH, null, null);

            startActivity(new Intent(getApplicationContext(),Favourites.class));

        }else{
            tts.speak("Your Assistant redirecting Favourite box", TextToSpeech.QUEUE_FLUSH, null);
        }

    }

    private void sendspeek() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak("Your Assistant redirecting Send Box", TextToSpeech.QUEUE_FLUSH, null, null);

            startActivity(new Intent(getApplicationContext(),Send.class));

        }else{
            tts.speak("Your Assistant redirecting Send box", TextToSpeech.QUEUE_FLUSH, null);
        }

    }

    private void inboxspeek(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak("Your Assistant redirecting Inbox", TextToSpeech.QUEUE_FLUSH, null, null);

            startActivity(new Intent(getApplicationContext(),Inboxx.class));

        }else{
            tts.speak("Your Assistant redirecting Inbox", TextToSpeech.QUEUE_FLUSH, null);
        }
    }


    public void inbox(View view) {
       // startActivity(new Intent(getApplicationContext(),Inboxx.class));
    }

    public void send(View view) {
       // startActivity(new Intent(getApplicationContext(),Send.class));
    }

    public void favourite(View view) {
       // startActivity(new Intent(getApplicationContext(),Favourites.class));

    }

    public void delete(View view) {
       // startActivity(new Intent(getApplicationContext(),Delete.class));
    }

    public void logout(View view) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirm Logout..!!!");
        alertDialogBuilder.setIcon(R.drawable.ic_baseline_power_settings_new_24);
        alertDialogBuilder.setMessage("Are you sure,You want to Logout");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();

                Toast.makeText(getApplicationContext(), "Logout Success!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),UserDetailsActivity.class));
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void clicked(View view) {
        if(IsInitialVoiceFinshed) {
            numberOfClicks++;
            listen();
        }

    }

    private void listen() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

        try {
            startActivityForResult(i, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(MainActivity.this, "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
        }
    }
}