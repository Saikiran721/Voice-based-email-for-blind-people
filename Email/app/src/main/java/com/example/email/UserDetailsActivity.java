package com.example.email;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserDetailsActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private TextView status;
    private EditText From,Password;
    private int numberOfClicks;
    UserLocalStore userLocalStore;
    private boolean IsInitialVoiceFinshed;

    private static final String URL="http://wizzie.tech/Email/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        IsInitialVoiceFinshed = false ;
       // status.setText("Mail?");
        //Toast.makeText(this, "There", Toast.LENGTH_SHORT).show();

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                    speak("Welcome to Voice Assistant");
                    if (userLocalStore.getLoggedInUser() == null){
                       new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                speak("Tell me your mail address? or cancel to close the application ");
                                IsInitialVoiceFinshed = true;
                            }
                        }, 4000);
                    }
                    else {
                        SpeakOutDetails();
                    }

                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });

        status = (TextView)findViewById(R.id.status);
        From = (EditText) findViewById(R.id.from);
        Password =(EditText) findViewById((R.id.password));

        userLocalStore = new UserLocalStore(this);
        numberOfClicks = 0;
    }



    private void SpeakOutDetails(){

        User user = userLocalStore.getLoggedInUser();
        From.setText(user.username);
        Password.setText((user.password));
        speak(" your Mail is" + user.username + "and your Password is" + user.password + " say yes to confirm and proceed and no to change the mail and cancel to ");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                status.setText("Voice?");
                IsInitialVoiceFinshed = true;
                numberOfClicks = 2;
            }
        }, 4000);
    }

    private void speak(String text){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
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

    public void layoutClicked(View view) {
        if(IsInitialVoiceFinshed) {
            numberOfClicks++;
            listen();
        }
    }

    private void listen(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

        try {
            startActivityForResult(i, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(UserDetailsActivity.this, "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
        }
    }


    private void exitFromApp()
    {
       this.finishAffinity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && IsInitialVoiceFinshed) {
            IsInitialVoiceFinshed = false;
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (result.get(0).equals("cancel")) {
                    speak("Cancelled!");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            exitFromApp();
                        }
                    }, 4000);

                }

                else if(result.get(0).equals("Verify")) {
                    speak("Mail Verification!");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           Intent intent=new Intent(getApplicationContext(),Verify.class);
                            intent.putExtra("em",From.getText().toString());
                            startActivity(intent);
                            finish();
                        }
                    }, 4000);

                }
                else {

                    switch (numberOfClicks) {
                        case 1:
                            String from;
                            from = result.get(0).replaceAll("underscore", "_");
                            from = from.replaceAll("\\s+", "");
                            From.setText(from);
                            Config.EMAIL = from;
                            status.setText("Password?");
                            speak("Password?");
                            break;
                        case 2:
                            String pwd;
                            pwd = result.get(0).replaceAll("\\s+","");
                            Config.PASSWORD = pwd;
                            Password.setText(pwd);
                            status.setText("Confirm?");
                            speak("Please Confirm the mail\n User : " + From.getText().toString() + "your password" +Config.PASSWORD + "\nSpeak Yes to confirm");
                            break;
                        default:
                            if(result.get(0).equals("yes")||result.get(0).equals("s"))
                            {
                                User user;
                                String username = From.getText().toString();
                                String password = Password.getText().toString();
                                Config.EMAIL = username;
                                Config.PASSWORD = password;

                                user = new User(username, password);
                                userLocalStore.storeUserData(user);
                                userLocalStore.setUserLoggedIn(true);

                                Intent HomeIntent = new Intent(UserDetailsActivity.this,MainActivity.class);
                                HomeIntent.putExtra("em",username);
                                startActivity(HomeIntent);
                                finish();

                                /*Intent HomeIntent = new Intent(UserDetailsActivity.this,Verify.class);
                                HomeIntent.putExtra("em",username);
                                startActivity(HomeIntent);
                                finish();*/
                            }
                            else
                            {
                                userLocalStore.clearUserData();
                                status.setText("Mail?");
                                speak("Please provide your Mail?");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        numberOfClicks = 0;
                                    }
                                }, 2000);
                            }
                            break;


                    }
                }
            }
            IsInitialVoiceFinshed = true;
        }
    }

    public void send(View view) {
        if (From.getText().toString().isEmpty()) {
            Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Email is empty", Snackbar.LENGTH_SHORT).show();
        }else if(Password.getText().toString().isEmpty()){
            Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Password Is missing", Snackbar.LENGTH_SHORT).show();
        }
        else {
            fun();
        }

    }

    private void fun() {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd:HH:mm");


        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               // dialog.dismiss();
                // Toast.makeText(Compose.this, ""+response, Toast.LENGTH_SHORT).show();

                try {
                    JSONObject jsonObject=new JSONObject(response);


                    if (jsonObject.getString("result").equals("success")){

                        Snackbar.make(UserDetailsActivity.this.getWindow().getDecorView().findViewById(android.R.id.content), "Mail Send Successfully", Snackbar.LENGTH_SHORT).show();

                        startActivity(new Intent(getApplicationContext(),Verify.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(UserDetailsActivity.this, error.toString(), Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<String, String>();

                params.put("user",From.getText().toString().trim());
                params.put("pass",Password.getText().toString().trim());

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

}