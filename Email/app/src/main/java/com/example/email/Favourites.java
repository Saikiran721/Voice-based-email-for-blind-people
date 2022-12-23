package com.example.email;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
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
import java.util.Map;

public class Favourites extends AppCompatActivity {

    private static final String URL="http://wizzie.tech/Email/get_favourites.php";

    RecyclerView recyclerView;
    FavouriteAdapter favouriteAdapter;
    ProgressDialog dialog;
    ArrayList to=new ArrayList();
    ArrayList subject=new ArrayList();
    ArrayList data =new ArrayList();
    ArrayList datetime=new ArrayList();
    ArrayList id=new ArrayList();
    ArrayList status=new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        recyclerView=findViewById(R.id.fav);
        getData(MainActivity.em);
    }

    private void getData(final String email) {
        dialog = new ProgressDialog(Favourites.this);
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
                                subject.add(jsonObject.getString("subject"));
                                data.add(jsonObject.getString("data"));
                                datetime.add(jsonObject.getString("date"));
                                id.add(jsonObject.getString("iid"));
                                status.add(jsonObject.getString("status"));


                            }

                            favouriteAdapter = new FavouriteAdapter(Favourites.this,to,subject,data,datetime,id,status);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            recyclerView.setAdapter(favouriteAdapter);

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
                params.put("m", email);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }
}