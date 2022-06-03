package com.example.mcc_attendance_tracker;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterPage4 extends AppCompatActivity implements View.OnClickListener {
    final Calendar myCalendar= Calendar.getInstance();
    View footer;
    TextView previous, next, back;
    ImageView dot3, dot4, dot1;
    LinearLayout header;
    LinearLayout dotLayout;
    EditText googleDrive, imageText;
    Spinner startShift, endShift, schedule;

    Boolean checkPermission= false;
    private static final int STORAGE_PERMISSION_CODE = 0701;
    private static final int PICK_IMAGE_REQUEST = 0173;

    private Uri filePath;
    private Bitmap bitmap;
    String storeImage = "";
    String fileName = "";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page_4);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        getWindow().setEnterTransition(null);
        sharedPreferences = getApplicationContext().getSharedPreferences("RegisterSharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        footer = findViewById(R.id.footer);
        previous = footer.findViewById(R.id.btnPrevious);
        next = footer.findViewById(R.id.btnNext);
        previous.setVisibility(View.VISIBLE);
        next.setText("Sign-up");

        header = findViewById(R.id.register_page_header_layout);
        dotLayout = footer.findViewById(R.id.dotLayout);
        dot1 = footer.findViewById(R.id.dot1);
        dot1.setBackgroundTintList(getResources().getColorStateList(R.color.green));
        dot3 = footer.findViewById(R.id.dot3);
        dot4 = footer.findViewById(R.id.dot4);
        dot3.setBackgroundTintList(getResources().getColorStateList(R.color.green));
        dot4.setBackgroundTintList(getResources().getColorStateList(R.color.sky));

        schedule = findViewById(R.id.register_ojt_schedule_spinner);
        imageText = findViewById(R.id.register_photo_textbox);
        imageText.setOnClickListener(this);

        back = findViewById(R.id.register_back_text);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clear();
                editor.commit();
                Intent intent = new Intent(RegisterPage4.this, LoginPage.class);
                startActivity(intent);
                finish();

            }
        });

        googleDrive = findViewById(R.id.register_gdrive_textbox);
        startShift = findViewById(R.id.register_start_shift_spinner);
        endShift = findViewById(R.id.register_end_shift_spinner);

        String[] startshiftItems = {"8:00 AM", "9:00 AM", "3:00 PM"};
        String[] endshiftItems = {"5:00 PM", "6:00 PM", "12:00 AM"};

        ArrayAdapter aa = new ArrayAdapter(getApplicationContext(), R.layout.custom_spinner, startshiftItems);
        aa.setDropDownViewResource(R.layout.custom_spinner_background);
        startShift.setAdapter(aa);

        ArrayAdapter bb = new ArrayAdapter(getApplicationContext(), R.layout.custom_spinner, endshiftItems);
        aa.setDropDownViewResource(R.layout.custom_spinner_background);
        endShift.setAdapter(bb);

        String[] scheduleItems = {"Monday, Wednesday, Friday", "Tuesday, Thursday, Saturday", "Monday to Friday", "Monday to Saturday"};
        ArrayAdapter cc = new ArrayAdapter(getApplicationContext(), R.layout.custom_spinner, scheduleItems);
        cc.setDropDownViewResource(R.layout.custom_spinner_background);
        schedule.setAdapter(cc);

        getData();

        startShift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                endShift.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData(googleDrive.getText().toString().trim(), startShift.getSelectedItem().toString().trim(),
                        endShift.getSelectedItem().toString().trim(), storeImage, schedule.getSelectedItem().toString().trim());
                Intent intent = new Intent(getApplicationContext(), RegisterPage3.class);
                Pair[] pairs = new Pair[4];

                pairs[0] = new Pair<View, String>(previous, "transition-previous-btn");
                pairs[1] = new Pair<View, String>(next, "transition-next-btn");
                pairs[2] = new Pair<View, String>(header, "transition-header");
                pairs[3] = new Pair<View, String>(dotLayout, "transition-dot");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterPage4.this,pairs);
                getWindow().setExitTransition(null);
                startActivity(intent, options.toBundle());
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(googleDrive.getText().toString().equals("")){
                    googleDrive.setError("This field is required.");
                }else if(imageText.getText().toString().equals("")){
                    imageText.setError("This field is required.");
                }else {
                    progressDialog.setTitle("Registering Intern");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage("Please wait, we are processing your data.");
                    progressDialog.show();
                    if(URLUtil.isValidUrl(googleDrive.getText().toString())){
                        insertData(googleDrive.getText().toString().trim(), startShift.getSelectedItem().toString().trim(),
                                endShift.getSelectedItem().toString().trim(), storeImage, schedule.getSelectedItem().toString().trim());
                        insertDataToServer();
                    }else{
                        googleDrive.setError("Invalid URL.");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                            }
                        }, 2000);
                    }

                }

            }
        });


    }

    private void insertDataToServer(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                            editor.clear().apply();
                            Intent intent = new Intent(RegisterPage4.this, LoginPage.class);
                            String message = obj.getString("message");
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    startActivity(intent);
                                    finish();
                                }
                            }, 2000);

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
                params.put("first_name", sharedPreferences.getString("first_name", ""));
                params.put("last_name", sharedPreferences.getString("last_name", ""));
                params.put("middle_name", sharedPreferences.getString("middle_name", ""));
                params.put("email", sharedPreferences.getString("email", ""));
                params.put("image", sharedPreferences.getString("image", ""));
                params.put("app_id", sharedPreferences.getString("application_id", ""));
                params.put("street", sharedPreferences.getString("street", ""));
                params.put("barangay", sharedPreferences.getString("barangay", ""));
                params.put("city", sharedPreferences.getString("city", ""));
                params.put("province", sharedPreferences.getString("province", ""));
                params.put("birthdate", sharedPreferences.getString("birthdate", ""));
                params.put("mobile_no", sharedPreferences.getString("contact_number", ""));
                params.put("sex", sharedPreferences.getString("gender", ""));
                params.put("religion", sharedPreferences.getString("religion", ""));
                params.put("civil_status", sharedPreferences.getString("civil_status", ""));
                params.put("university", sharedPreferences.getString("university", ""));
                params.put("company", sharedPreferences.getString("company", ""));
                params.put("department", sharedPreferences.getString("department", ""));
                params.put("start_date", sharedPreferences.getString("start_date", ""));
                params.put("required_hrs", sharedPreferences.getString("required_hours", ""));
                params.put("gdrive_link", sharedPreferences.getString("google_drive_link", ""));
                params.put("end_date", sharedPreferences.getString("end_date", ""));
                params.put("start_shift", sharedPreferences.getString("start_shift", ""));
                params.put("end_shift", sharedPreferences.getString("end_shift", ""));
                params.put("schedule", sharedPreferences.getString("schedule", ""));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void requestStoragePermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            checkPermission = true;
            return;
        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            String temp = filePath.getLastPathSegment();
            File temp_file = new File(temp);
            fileName = temp_file.getName();
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                storeImage = getStringImage(bitmap);
                editor.putString("image", storeImage);
                editor.putString("image_name", fileName);
                editor.commit();
                imageText.setText(fileName);

            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onClick(View v) {

        if(v == imageText){
            requestStoragePermission();
            if(checkPermission){
                showFileChooser();
            }else{
                requestStoragePermission();
            }
        }


    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }

    private void getData(){
        if(sharedPreferences.contains("google_drive_link")){
            googleDrive.setText(sharedPreferences.getString("google_drive_link", ""));
        }
        if(sharedPreferences.contains("start_shift")){
            for(int i = 0; i< startShift.getAdapter().getCount(); i++){
                if(sharedPreferences.getString("start_shift","").equals(startShift.getItemAtPosition(i))){
                    startShift.setSelection(i);
                }
            }
        }
        if(sharedPreferences.contains("end_shift")){
            for(int i = 0; i< endShift.getAdapter().getCount(); i++){
                if(sharedPreferences.getString("end_shift","").equals(endShift.getItemAtPosition(i))){
                    endShift.setSelection(i);
                }
            }
        }
        if(sharedPreferences.contains("schedule")){
            for(int i=0; i<schedule.getAdapter().getCount(); i++){
                if(sharedPreferences.getString("schedule", "").equals(schedule.getItemAtPosition(i))){
                    schedule.setSelection(i);
                }
            }
        }
        if(sharedPreferences.contains("image")){
           imageText.setText(sharedPreferences.getString("image_name", ""));
        }
    }

    private void insertData(String gdrive, String start, String end, String image, String sched){
        editor.putString("google_drive_link", gdrive);
        editor.putString("start_shift", start);
        editor.putString("end_shift", end);
        editor.putString("image", image);
        editor.putString("schedule", sched);
        editor.commit();
    }
}
