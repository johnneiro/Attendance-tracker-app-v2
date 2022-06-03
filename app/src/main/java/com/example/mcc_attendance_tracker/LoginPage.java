package com.example.mcc_attendance_tracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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

public class LoginPage extends AppCompatActivity {
    EditText username, password;
    TextView signUp, eye, forgotPassword;
    Button logIn;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        sharedPreferences = getApplicationContext().getSharedPreferences("UserDataSharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        username = findViewById(R.id.login_email_textbox);
        password = findViewById(R.id.login_password_textbox);
        eye = findViewById(R.id.login_password_eye);

        eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (eye.getText().toString()) {
                    case "\uf06e":
                        password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        password.setSelection(password.getText().length());
                        eye.setText("\uf070");
                        break;

                    case "\uf070":
                        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        password.setSelection(password.getText().length());
                        eye.setText("\uf06e");
                        break;
                }
            }
        });

        forgotPassword = findViewById(R.id.login_forgot_password_text);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        signUp = findViewById(R.id.login_signup_text);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, RegisterPage1.class);
                startActivity(intent);
            }
        });

        logIn = findViewById(R.id.login_signin_button);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setTitle("Logging In");
                progressDialog.setMessage("Please wait, we are processing your data.");
                progressDialog.show();
                checkInputs();
            }
        });
    }

    private void checkInputs(){
        final String email = username.getText().toString().trim();
        final String userPass = password.getText().toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("error")){
                                editor.putString("email", email);
                                editor.putBoolean("isLoggedIn", true);
                                editor.putString("firstname", obj.getString("firstname"));
                                editor.putString("lastname", obj.getString("lastname"));
                                editor.putString("middle_name", obj.getString("middle_name"));
                                editor.putString("position", obj.getString("position"));
                                editor.putString("school", obj.getString("school"));
                                editor.putString("company", obj.getString("company"));
                                editor.putString("department", obj.getString("department"));
                                editor.putString("start_shift", obj.getString("start_shift"));
                                editor.putString("end_shift", obj.getString("end_shift"));
                                editor.putString("required_hours", obj.getString("required_hours"));
                                editor.commit();

                                String message= obj.getString("message");
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(LoginPage.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                }, 2000);


                            }else{
                                String message= obj.getString("message");
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    }
                                }, 2000);

                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params  = new HashMap<>();
                params.put("email", email);
                params.put("password", userPass);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


}
