package com.example.email;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Verify extends AppCompatActivity {
    int code;
    EditText passcode1;
    EditText passcode2;
    EditText passcode3;
    EditText passcode4;
    EditText otp;

    private TextToSpeech tts;
    private boolean IsInitialVoiceFinshed;

    private int numberOfClicks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        passcode1= findViewById(R.id.digit1);
        passcode2= findViewById(R.id.digit2);
        passcode3= findViewById(R.id.digit3);
        passcode4= findViewById(R.id.digit4);
        otp = findViewById(R.id.etOTP);

        passcode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length()>0){
                    passcode2.requestFocus();
                }

            }
        });
        passcode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length()>0){
                    passcode3.requestFocus();
                }

            }
        });
        passcode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length()>0){
                    passcode4.requestFocus();
                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100&& IsInitialVoiceFinshed){
            IsInitialVoiceFinshed = false;
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if(result.get(0).equalsIgnoreCase("Send")){
                    fun();
                }
                else{
                    switch (numberOfClicks) {
                        case 1:
                            String too;
                            too = result.get(0).replaceAll("underscore", "_");
                            too = too.replaceAll("\\s+", "");
                            otp.setText(too);
                            speak("Subject");
                            break;



                        default:
                            if(result.get(0).equals("yes")||result.get(0).equals("s")) {
                                fun();
                            }
                            break;


                    }
                }

            }
            IsInitialVoiceFinshed=true;
        }
    }

    private void speak(String res) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(res, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(res, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void composespeek() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak("Your Assistant redirecting Send a Mail", TextToSpeech.QUEUE_FLUSH, null, null);

            fun();
            // startActivity(new Intent(getApplicationContext(),Compose.class));

        }else{
            tts.speak("Your Assistant redirecting Send a Mail", TextToSpeech.QUEUE_FLUSH, null);
        }

    }
    private void fun() {


    }
    public void sendverifyemail(View view){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            tts.speak("Your Assistant redirecting Send a Mail", TextToSpeech.QUEUE_FLUSH, null, null);

            Random random = new Random();
            code = random.nextInt(8999)+1000;
            final EditText editText = findViewById(R.id.etOTP);
            String url ="http://wizzie.tech/Email/otp.php";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(Verify.this, response,Toast.LENGTH_SHORT).show();
                    findViewById(R.id.box1).setVisibility(View.GONE);
                    findViewById(R.id.box2).setVisibility(View.VISIBLE);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Verify.this,error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }){
                protected Map<String, String> getParams()throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("email",editText.getText().toString());
                    params.put("code", String.valueOf(code));
                    return params;
                }
            };
            requestQueue.add(stringRequest);
            // startActivity(new Intent(getApplicationContext(),Compose.class));

        }else{
            tts.speak("Your Assistant redirecting Send a Mail", TextToSpeech.QUEUE_FLUSH, null);
        }

    }

    public void confirm(View view) {
        String inputCode;
        inputCode = passcode1.getText().toString()+passcode2.getText()+passcode3.getText()+passcode4.getText();

        if(inputCode.equals(String.valueOf(code))){
            Toast.makeText(this,"Success", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }else {
            Toast.makeText(this,"Failed", Toast.LENGTH_SHORT).show();
        }


    }
}
