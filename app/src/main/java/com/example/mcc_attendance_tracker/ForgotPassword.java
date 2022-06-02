package com.example.mcc_attendance_tracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ForgotPassword extends AppCompatActivity implements View.OnClickListener{

    EditText userEmail;
    Button sendCode;
    SharedPreferences sp;
    TextView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        userEmail = findViewById(R.id.tbxEmail);
        sendCode = findViewById(R.id.btnSendCode);
        back = findViewById(R.id.forgotPassBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sp = getApplicationContext().getSharedPreferences("ForgotPasswordSharedPref", Context.MODE_PRIVATE);
        sendCode.setOnClickListener(this);

    }

    private void checkEmail() {
        final String inputEmail = userEmail.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_FORGOT_PASS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("error")){
                                SharedPreferences.Editor editor = sp.edit();
                                String verificationCode = obj.getString("code");
                                editor.putString("code", verificationCode);
                                editor.putString("email", inputEmail);
                                editor.apply();

                                Intent intent = new Intent(ForgotPassword.this, EmailVerificationFP.class);
                                startActivity(intent);
                                finish();

                            }else{
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params  = new HashMap<>();
                params.put("email", inputEmail);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void onClick(View v){
        if(v == sendCode){
            checkEmail();
        }
    }


}
