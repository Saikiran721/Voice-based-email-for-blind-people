package com.example.email;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Inboxx extends AppCompatActivity {

    RecyclerView recyclerView;

    private TextToSpeech tts;
    private boolean IsInitialVoiceFinshed;
    private int numberOfClicks;


   static ArrayList to=new ArrayList();
   static ArrayList from=new ArrayList();
   static ArrayList subject=new ArrayList();
    ArrayList data =new ArrayList();
    ArrayList datetime=new ArrayList();
    ArrayList id=new ArrayList();
    ArrayList status=new ArrayList();
    String res;

    private static final String URL="http://wizzie.tech/Email/getmails.php";
    DataAdapter dataAdapter;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inboxx);
        IsInitialVoiceFinshed = false ;

        recyclerView=findViewById(R.id.recycler);
        getData(MainActivity.em);
        Toast.makeText(this, ""+MainActivity.em, Toast.LENGTH_SHORT).show();

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

                if(result.get(0).equalsIgnoreCase("Compose")){
                    composespeek();
                }


            }
            else {
                switch (numberOfClicks) {
                    case 1:
                        speak("Compose");
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

    private void composespeek() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak("Your Assistant redirecting Compose a Mail", TextToSpeech.QUEUE_FLUSH, null, null);

            startActivity(new Intent(getApplicationContext(),Compose.class));

        }else{
            tts.speak("Your Assistant redirecting Compose a Mail", TextToSpeech.QUEUE_FLUSH, null);
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


    private void getData(final String m) {
        dialog = new ProgressDialog(Inboxx.this);
        dialog.setMessage("Loading, please wait.");
        dialog.show();
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            // Toast.makeText(Inboxx.this, ""+response, Toast.LENGTH_SHORT).show();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                to.add(jsonObject.getString("to_address"));
                                from.add(jsonObject.getString("from_address"));
                                subject.add(jsonObject.getString("sub"));
                                data.add(jsonObject.getString("mail_data"));
                                datetime.add(jsonObject.getString("dtime"));
                                id.add(jsonObject.getString("id"));
                                status.add(jsonObject.getString("status"));

                            }

                            dataAdapter = new DataAdapter(Inboxx.this,to,subject,data,datetime,id,status,from);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            recyclerView.setAdapter(dataAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("m", m);

                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    public void compose(View view) {
        //startActivity(new Intent(getApplicationContext(),Compose.class));

    }

    public void onclicked(View view) {

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
            Toast.makeText(Inboxx.this, "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
        }
    }


}