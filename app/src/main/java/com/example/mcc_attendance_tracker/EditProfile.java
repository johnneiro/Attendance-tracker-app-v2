package com.example.mcc_attendance_tracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity implements View.OnClickListener{
    EditText street, barangay, city, contactNumber, email, province, driveLink;
    Button saveData, cancel;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userEmail;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait, we are processing your data.");
        progressDialog.show();
        sharedPreferences = getApplicationContext().getSharedPreferences("UserDataSharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        userEmail = sharedPreferences.getString("email", "");

        street = findViewById(R.id.tbxEditStreet);
        barangay = findViewById(R.id.tbxEditBarangay);
        city = findViewById(R.id.tbxEditCity);
        contactNumber = findViewById(R.id.tbxEditContactNumber);
        email = findViewById(R.id.tbxEditEmail);
        province = findViewById(R.id.tbxEditProvince);
        driveLink = findViewById(R.id.tbxEditGoogleDriveLink);
        cancel = findViewById(R.id.btnCancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveData = findViewById(R.id.btnSave);
        saveData.setOnClickListener(this);
        fillInTextbox();
    }

    private void fillInTextbox(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_VIEW_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("error")){

                                street.setText(obj.getString("street"));
                                barangay.setText(obj.getString("barangay"));
                                city.setText(obj.getString("city"));
                                contactNumber.setText(obj.getString("contact_number"));
                                email.setText(obj.getString("email"));
                                province.setText(obj.getString("province"));
                                driveLink.setText(obj.getString("drive"));
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                    }
                                }, 2000);

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
                params.put("email", userEmail);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void updateProfile(){
        final String userStreet = street.getText().toString().trim();
        final String userBarangay = barangay.getText().toString().trim();
        final String userCity = city.getText().toString().trim();
        final String userEmailAddress = email.getText().toString().trim();
        final String userContactNumber = contactNumber.getText().toString().trim();
        final String userProvince = province.getText().toString().trim();
        final String userDrive = driveLink.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_UPDATE_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("error")) {
                                String message = obj.getString("message");
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                }, 2000);

                            }else{
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        }catch(JSONException e){
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
                params.put("user_email", userEmail);
                params.put("street", userStreet);
                params.put("barangay", userBarangay);
                params.put("city", userCity);
                params.put("email", userEmailAddress);
                params.put("contact_number", userContactNumber);
                params.put("province", userProvince);
                params.put("drive", userDrive);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    @Override
    public void onClick(View v){
        if(v==saveData){
            progressDialog.setMessage("Please wait, we are processing your data.");
            progressDialog.setTitle("Updating Profile");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            updateProfile();
        }
    }
}
