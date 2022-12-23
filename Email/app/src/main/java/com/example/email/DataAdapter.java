package com.example.email;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    Context context;
    ArrayList to=new ArrayList();
    ArrayList from=new ArrayList();
    ArrayList subject=new ArrayList();
    ArrayList data =new ArrayList();
    ArrayList datetime=new ArrayList();
    ArrayList id=new ArrayList();
    ArrayList status=new ArrayList();
    static String to_addrs,subj;


    private static final String URL="http://wizzie.tech/Email/add_favourite.php";
    private static final String URLD="http://wizzie.tech/Email/add_delete.php";
    private static final String URL1="http://wizzie.tech/Email/delete.php";

    public DataAdapter(Inboxx inboxx, ArrayList to, ArrayList subject, ArrayList data, ArrayList datetime, ArrayList id, ArrayList status, ArrayList from) {

    this.context=inboxx;
    this.to=to;
    this.from=from;
    this.subject=subject;
    this.data=data;
    this.datetime=datetime;
    this.id=id;
    this.status=status;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.request_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.from_address.setText(from.get(position).toString().trim());
        holder.dtime.setText(datetime.get(position).toString().trim());
        holder.subject.setText(subject.get(position).toString().trim());
        holder.data.setText(data.get(position).toString().trim());


        if(status.get(position).toString().equalsIgnoreCase("favourite")){

            holder.star.setImageResource(R.drawable.ic_baseline_star_24);
        }

        holder.star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest stringRequest=new StringRequest(Request.Method.POST,URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // Toast.makeText(context, ""+response, Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject jsonObject=new JSONObject(response);

                            if (jsonObject.getString("result").equals("success")){
                                Toast.makeText(context, "Added To Favourite", Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params=new HashMap<String, String>();
                        params.put("id",id.get(position).toString().trim());
                        params.put("to",to.get(position).toString().trim());
                        params.put("dt",data.get(position).toString().trim());
                        params.put("sub",subject.get(position).toString().trim());
                        params.put("date",datetime.get(position).toString().trim());
                        params.put("st",status.get(position).toString().trim());
                        params.put("mail",MainActivity.em);
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(stringRequest);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StringRequest stringRequest=new StringRequest(Request.Method.POST,URL1, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(context, ""+response, Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject jsonObject=new JSONObject(response);

                            if (jsonObject.getString("result").equals("success")){
                                Toast.makeText(context, "Mail Deleted", Toast.LENGTH_SHORT).show();
                                delete(id.get(position).toString().trim());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params=new HashMap<String, String>();
                        params.put("id",id.get(position).toString().trim());
                        params.put("to",to.get(position).toString().trim());
                        params.put("dt",data.get(position).toString().trim());
                        params.put("sub",subject.get(position).toString().trim());
                        params.put("date",datetime.get(position).toString().trim());
                        params.put("st",status.get(position).toString().trim());
                        params.put("mail",MainActivity.em);
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(stringRequest);
                //delete(id.get(position).toString().trim());
            }
        });

        holder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                to_addrs=Inboxx.to.get(position).toString().trim();
                subj=Inboxx.subject.get(position).toString().trim();
                context.startActivity(new Intent(context,Compose.class));
            }
        });

        holder.read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(context,ReadMail.class);
                intent.putExtra("rr",data.get(position).toString().trim());
              //  intent.putExtra("fr",to.get(position).toString().trim());
                intent.putExtra("fr",from.get(position).toString().trim());
                intent.putExtra("sub",subject.get(position).toString().trim());
                context.startActivity(intent);

            }
        });

    }

    private void delete(final String trim) {

        StringRequest stringRequest=new StringRequest(Request.Method.POST,URLD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(context, ""+response, Toast.LENGTH_SHORT).show();

                try {
                    JSONObject jsonObject=new JSONObject(response);

                    if (jsonObject.getString("result").equals("success")){
                       // Toast.makeText(context, "Mail Deleted", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<String, String>();
                params.put("id",trim);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }

    private void starfun(String s, String toString, String string, String s1, String em) {


    }

    @Override
    public int getItemCount() {
        return id.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayout,layout;
        TextView from_address,dtime,subject,data;
        ImageView star,delete,reply,read;
        int z = 0;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            from_address=itemView.findViewById(R.id.frm);
            dtime=itemView.findViewById(R.id.dt);
            subject=itemView.findViewById(R.id.sub);
            data=itemView.findViewById(R.id.mail);
            linearLayout=itemView.findViewById(R.id.parent);
            layout=itemView.findViewById(R.id.dta);
            star=itemView.findViewById(R.id.str);
            delete=itemView.findViewById(R.id.dlt);
            reply=itemView.findViewById(R.id.reply);
            read=itemView.findViewById(R.id.read);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (z == 0) {
                        layout.setVisibility(View.VISIBLE);
                        z++;
                    } else {
                        layout.setVisibility(View.GONE);
                        z = 0;
                    }

                }
            });

        }
    }
}
