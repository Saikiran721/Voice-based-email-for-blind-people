/*
package com.example.email;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Signup extends AppCompatActivity {
    EditText name,username,password,mobile;
    Button register;
    TextView goto_login;
    LinearLayout linearLayout;
    UserLocalStore userLocalStore;
    private TextToSpeech tts;
    private int numberOfClicks;
    private boolean IsInitialVoiceFinshed;
    private static final String SIGNUP="http://wizzie.tech/Email/signup.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        IsInitialVoiceFinshed = false ;

        linearLayout=findViewById(R.id.llt);

        goto_login=findViewById(R.id.backto_login);
        register=findViewById(R.id.register);

        name=findViewById(R.id.name);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        mobile=findViewById(R.id.mobile);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                    speak("Welcome to voice mail");

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


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().trim().isEmpty()) {
                    Snackbar.make(Signup.this.getWindow().getDecorView().findViewById(android.R.id.content), "Enter First Name", Snackbar.LENGTH_SHORT).show();
                }
                else if(username.getText().toString().trim().isEmpty()){
                    Snackbar.make(Signup.this.getWindow().getDecorView().findViewById(android.R.id.content), "Enter User Name", Snackbar.LENGTH_SHORT).show();
                }
                else if(password.getText().toString().trim().isEmpty()){
                    Snackbar.make(Signup.this.getWindow().getDecorView().findViewById(android.R.id.content), "Enter Password", Snackbar.LENGTH_SHORT).show();
                }
                else if(mobile.getText().toString().trim().isEmpty()){
                    Snackbar.make(Signup.this.getWindow().getDecorView().findViewById(android.R.id.content), "Enter Mobile", Snackbar.LENGTH_SHORT).show();
                }
                else if(mobile.getText().toString().length()>10||mobile.getText().toString().length()<10||mobile.getText().toString().isEmpty()){
                    Snackbar.make(Signup.this.getWindow().getDecorView().findViewById(android.R.id.content), "Enter 10 Digits", Snackbar.LENGTH_SHORT).show();
                }
                else {
                   // function();
                }
            }
        });


        goto_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });

    }

    private void SpeakOutDetails(){

        User user = userLocalStore.getLoggedInUser();
        username.setText(user.username);
        password.setText((user.password));
        name.setText(user.name);
        mobile.setText((user.mobile));


        speak(" your Mail is" + user.username + "and your Password is" + user.password + " say yes to confirm and proceed and no to change the mail and cancel to ");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               // status.setText("Mail?");
                IsInitialVoiceFinshed = true;
                numberOfClicks = 2;
            }
        }, 4000);
    }

    private void speak(String s) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null);
        }

    }


    private void function() {

        StringRequest stringRequest=new StringRequest(Request.Method.POST, SIGNUP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(Signup.this, ""+response, Toast.LENGTH_SHORT).show();

                try {
                    JSONObject jsonObject=new JSONObject(response);

                    if (jsonObject.getString("result").equals("success")){

                        Snackbar.make(Signup.this.getWindow().getDecorView().findViewById(android.R.id.content), "Register Successfully", Snackbar.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),Login.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Signup.this, error.toString(), Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<String, String>();
                params.put("name",name.getText().toString().trim());
                params.put("user",username.getText().toString().trim());
                params.put("pass",password.getText().toString().trim());
                params.put("mobile",mobile.getText().toString().trim());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

    @Override
    public void onDestroy() {
        if (tts != null) {

            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    public void layoutclicked(View view) {

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
            Toast.makeText(Signup.this, "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
        }
    }

    private void exitFromApp() {
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

                } else {

                    switch (numberOfClicks) {
                        case 1:
                            String nam;
                            nam = result.get(0).replaceAll("underscore", "_");
                            nam = nam.replaceAll("\\s+", "");
                            name.setText(nam);
                            Config.NAME = nam;
                           // status.setText("Password?");
                            speak("User name?");
                            break;
                        case 2:
                            String user1;
                            user1 = result.get(0).replaceAll("\\s+","");
                            Config.EMAIL = user1;
                            username.setText(user1);
                            //status.setText("Confirm?");
                           // speak("Please Confirm the mail\n User : " + From.getText().toString() + "your password" +Config.PASSWORD + "\nSpeak Yes to confirm");
                            break;
                        case 3:
                            String pwd;
                            pwd = result.get(0).replaceAll("\\s+","");
                            Config.PASSWORD = pwd;
                            password.setText(pwd);
                            //status.setText("Confirm?");
                            //speak("Please Confirm the mail\n User : " + From.getText().toString() + "your password" +Config.PASSWORD + "\nSpeak Yes to confirm");
                            break;
                        case 4:
                            String mobile1;
                            mobile1 = result.get(0).replaceAll("\\s+","");
                            Config.MOBILE = mobile1;
                            mobile.setText(mobile1);
                           // status.setText("Confirm?");
                            speak("Please Confirm the mail\n User : " + username.getText().toString() + "your password" +Config.PASSWORD + "\nSpeak Yes to confirm");
                            break;


                        default:
                            if(result.get(0).equals("yes")||result.get(0).equals("s"))
                            {
                                function();

                                User user;
                                String username1 = username.getText().toString();
                                String password1 = password.getText().toString();
                                String name1 = name.getText().toString();
                                String mobil = mobile.getText().toString();

                                Config.EMAIL = username1;
                                Config.PASSWORD = password1;
                                Config.NAME = name1;
                                Config.MOBILE = mobil;

                                user = new User(username1, password1,name1,mobil);
                                userLocalStore.storeUserData(user);
                                userLocalStore.setUserLoggedIn(true);

                                Intent HomeIntent = new Intent(Signup.this,Login.class);
                                startActivity(HomeIntent);
                                finish();
                            }else
                            {
                                userLocalStore.clearUserData();
                               // status.setText("Mail?");
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
}*/
