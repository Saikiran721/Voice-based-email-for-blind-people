package com.example.email;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Compose extends AppCompatActivity {

    Spinner from;
    EditText to,subject,compose;
    String date;
    ArrayAdapter adapter;
    ArrayList mails =new ArrayList();

    private TextToSpeech tts;
    private boolean IsInitialVoiceFinshed;
    private int numberOfClicks;
    String res;

    private static final String URL="http://wizzie.tech/Email/sendmail.php";
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        IsInitialVoiceFinshed = false ;
        //Toast.makeText(this, ""+ MainActivity.email, Toast.LENGTH_SHORT).show();

        from=findViewById(R.id.from);
        to=findViewById(R.id.to);
        subject=findViewById(R.id.subject);
        compose=findViewById(R.id.data);

        mails.add(MainActivity.em);

        adapter =new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,mails);
        from.setAdapter(adapter);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100&& IsInitialVoiceFinshed){
            IsInitialVoiceFinshed = false;
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if(result.get(0).equalsIgnoreCase("Send")){
                    composespeek();
                }
                else{
                    switch (numberOfClicks) {
                        case 1:
                            String too;
                            too = result.get(0).replaceAll("underscore", "_");
                            too = too.replaceAll("\\s+", "");
                            to.setText(too);
                            speak("Subject");
                            break;

                        case 2:
                            String sub;
                            sub = result.get(0)/*.replaceAll("\\s+","")*/;
                            subject.setText(sub);
                            speak("Mail");
                            break;

                        case 3:
                            String com;
                            com = result.get(0)/*.replaceAll("\\s+","")*/;
                            compose.setText(com);
                            speak("Please Confirm the to address\n To : " + to.getText().toString() + "your subject " +subject.getText().toString().trim() +"Your MAil Data "+compose.getText().toString().trim()+ "\nSpeak Yes to confirm");
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

    private void composespeek() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak("Your Assistant redirecting Send a Mail", TextToSpeech.QUEUE_FLUSH, null, null);

            fun();
           // startActivity(new Intent(getApplicationContext(),Compose.class));

        }else{
            tts.speak("Your Assistant redirecting Send a Mail", TextToSpeech.QUEUE_FLUSH, null);
        }

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

    public void send(View view) {
        if (to.getText().toString().isEmpty()) {
            Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Enter To Address", Snackbar.LENGTH_SHORT).show();
        }else if(subject.getText().toString().isEmpty()){
            Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Subject Is missing", Snackbar.LENGTH_SHORT).show();
        }else if(compose.getText().toString().isEmpty()){
            Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Content Missing", Snackbar.LENGTH_SHORT).show();
        }
        else {
            fun();
        }

    }

    private void fun() {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
        date = df.format(Calendar.getInstance().getTime());

        dialog = new ProgressDialog(Compose.this);
        dialog.setMessage("Loading, please wait.");
        dialog.show();

        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
               // Toast.makeText(Compose.this, ""+response, Toast.LENGTH_SHORT).show();

                try {
                    JSONObject jsonObject=new JSONObject(response);


                    if (jsonObject.getString("result").equals("success")){

                        Snackbar.make(Compose.this.getWindow().getDecorView().findViewById(android.R.id.content), "Mail Send Successfully", Snackbar.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Compose.this, error.toString(), Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<String, String>();
                params.put("from",from.getSelectedItem().toString().trim());
                params.put("mobile","NA");
                params.put("to",to.getText().toString().trim());
                params.put("sub",subject.getText().toString().trim());
                params.put("mail_data",compose.getText().toString().trim());
                params.put("dt",date.toString().trim());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void composclick(View view) {

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
            Toast.makeText(Compose.this, "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
        }

    }
}