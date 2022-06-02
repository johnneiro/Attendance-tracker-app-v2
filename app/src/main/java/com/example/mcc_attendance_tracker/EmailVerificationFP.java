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

import androidx.annotation.Nullable;
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

public class EmailVerificationFP extends AppCompatActivity implements View.OnClickListener{

    EditText inputCode;
    TextView resendCode;
    Button submit;
    SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        sp = getApplicationContext().getSharedPreferences("ForgotPasswordSharedPref", Context.MODE_PRIVATE);
        inputCode = findViewById(R.id.tbxVerificationCode);
        resendCode = findViewById(R.id.txtResendCode);
        submit = findViewById(R.id.btnSubmitCode);
        submit.setOnClickListener(this);
        resendCode.setOnClickListener(this);


    }

    private void verifyCode(String code){
        final String codeInput = inputCode.getText().toString().trim();
        final String inputEmail = sp.getString("email", "");

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_VERIFY_CODE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("error")){
                                if(codeInput.equals(code)) {
                                    Toast.makeText(getApplicationContext(), "Verified successfully.", Toast.LENGTH_LONG).show();
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("code", "");
                                    editor.apply();
                                    Intent intent = new Intent(EmailVerificationFP.this, ResetPassword.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Toast.makeText(getApplicationContext(), "Wrong code.", Toast.LENGTH_LONG).show();
                                }
                            }else{
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params  = new HashMap<>();
                params.put("code", codeInput);
                params.put("email", inputEmail);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

    private void codeResend(){
        final String inputEmail = sp.getString("email", "");

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
                                editor.apply();

                                Toast.makeText(getApplicationContext(), "Code sent successfully.", Toast.LENGTH_LONG).show();

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
        if(v == submit){
            String verificationCode = sp.getString("code", "");
            String codeInput = inputCode.getText().toString().trim();
            verifyCode(verificationCode);
        }

        if(v == resendCode){
            codeResend();
        }
    }
}
