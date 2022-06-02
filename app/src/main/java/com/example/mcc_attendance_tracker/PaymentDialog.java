package com.example.mcc_attendance_tracker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
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

import static android.content.Context.MODE_PRIVATE;

public class PaymentDialog extends Dialog {

    private Activity activity;
    Button submit;
    EditText mop, pop;
    int webinarID;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Button joinBtn;
    TextView link;
    TextView text;
    public PaymentDialog(Activity activity, int webinarID, Button joinBtn, TextView link, TextView text) {
        super(activity);
        this.activity = activity;
        this.webinarID = webinarID;
        this.joinBtn = joinBtn;
        this.link = link;
        this.text = text;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_webinar_dialog);
        sharedPreferences = activity.getApplicationContext().getSharedPreferences("UserDataSharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();


        submit= findViewById(R.id.join_webinar_submit);
        mop = findViewById(R.id.join_webinar_mop);
        pop = findViewById(R.id.join_webinar_pop);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mop.getText().toString().equals("")){
                    mop.setError("This field is required.");
                }else if(pop.getText().toString().equals("")){
                    pop.setError("This field is required.");
                }else{
                    addToWebinarProcess();
                }
            }
        });

    }

    public void addToWebinarProcess(){
        final String userEmail = sharedPreferences.getString("email", "");
        final String mode_of_payment = mop.getText().toString().trim();
        final String proof_of_payment = pop.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_PARTICIPATE_WEBINAR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("error")) {
                                joinBtn.setText("Already Registered");
                                joinBtn.setEnabled(false);
                                link.setVisibility(View.VISIBLE);
                                text.setText("Here is the link for the webinar:");
                                dismiss();
                            }

                            Toast.makeText(activity.getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();

                        }catch(JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity.getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params  = new HashMap<>();
                params.put("email", userEmail);
                params.put("webinar_id", String.valueOf(webinarID));
                params.put("mop", mode_of_payment);
                params.put("pop", proof_of_payment);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(activity.getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
