package com.example.mcc_attendance_tracker;

import android.app.ProgressDialog;
import android.content.Context;
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

public class ResetPassword extends AppCompatActivity implements View.OnClickListener{
    TextView eye1, eye2;
    EditText newPassword, confirmNewPassword;
    Button btnSavePassword, btnCancel;
    SharedPreferences sp;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        sp = getApplicationContext().getSharedPreferences("ForgotPasswordSharedPref", Context.MODE_PRIVATE);
        eye1 = findViewById(R.id.txtEye1);
        eye2 = findViewById(R.id.txtEye2);
        newPassword = findViewById(R.id.tbxNewPassword);
        confirmNewPassword = findViewById(R.id.tbxConfirmNewPassword);
        btnSavePassword = findViewById(R.id.btnResetPassword);
        btnCancel = findViewById(R.id.btnCancel);

        btnSavePassword.setOnClickListener(this);
        eye1.setOnClickListener(this);
        eye2.setOnClickListener(this);

    }

    private void passwordReset(){
        final String inputEmail = sp.getString("email", "");
        final String userPassword = newPassword.getText().toString().trim();
        final String userConfirmPassword = confirmNewPassword.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_RESET_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject obj = new JSONObject(response);
                            if(userPassword.equals(userConfirmPassword)){
                                if(!obj.getBoolean("error")){
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.clear();
                                    editor.commit();

                                    Intent intent = new Intent(ResetPassword.this, LoginPage.class);
                                    String message = obj.getString("message");
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Password has successfully changed.", Toast.LENGTH_LONG).show();
                                            startActivity(intent);
                                            finish();
                                        }
                                    }, 2000);

                                }else{
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                                }

                            }else{
                                Toast.makeText(getApplicationContext(), "Password did not match.", Toast.LENGTH_LONG).show();
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params  = new HashMap<>();
                params.put("email", inputEmail);
                params.put("password", userPassword);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void onClick(View v){
        if(v==btnSavePassword){
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setTitle("Updating Password");
            progressDialog.setMessage("Please wait, we are loading your data.");
            progressDialog.show();
            passwordReset();
        }
        if(v==eye1){
            switch (eye1.getText().toString()) {
                case "\uf06e":
                    newPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    eye1.setText("\uf070");
                    break;

                case "\uf070":
                    newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    eye1.setText("\uf06e");
                    break;
            }
        }

        if(v==eye2){
            switch (eye2.getText().toString()) {
                case "\uf06e":
                    confirmNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    eye2.setText("\uf070");
                    break;

                case "\uf070":
                    confirmNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    eye2.setText("\uf06e");
                    break;
            }
        }

        




    }
}
