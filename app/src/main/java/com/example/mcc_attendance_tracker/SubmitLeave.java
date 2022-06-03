package com.example.mcc_attendance_tracker;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.jkcarino.rtexteditorview.RTextEditorToolbar;
import com.jkcarino.rtexteditorview.RTextEditorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class SubmitLeave extends AppCompatActivity {
    final Calendar myCalendar= Calendar.getInstance();
    EditText start, end;
    Spinner reason;
    Button submit, cancel;
    EditText details;
    SharedPreferences sharedPreferences1;
    SharedPreferences.Editor editor1;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userEmail;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_leave_form);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);

        sharedPreferences = getApplicationContext().getSharedPreferences("UserDataSharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        sharedPreferences1 = getApplicationContext().getSharedPreferences("DetailsSharedPref", MODE_PRIVATE);
        editor1 =sharedPreferences1.edit();

        userEmail = sharedPreferences.getString("email", "");

        start = findViewById(R.id.submit_leave_start_date);
        end = findViewById(R.id.submit_leave_end_date);
        reason = findViewById(R.id.submit_leave_reason);
        details = findViewById(R.id.submit_leave_details);

        String[] reasonItems = {"Sick Leave", "Casual Leave", "Public Holiday", "Religious Holidays", "Maternity Leave", "Paternity Leave", "Bereavement Leave", "Compensatory Leave", "Sabbatical Leave", "Other"};
        ArrayAdapter aa = new ArrayAdapter(getApplicationContext(), R.layout.custom_spinner, reasonItems);
        aa.setDropDownViewResource(R.layout.custom_spinner_background);
        reason.setAdapter(aa);

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor1.putString("start_date", start.getText().toString().trim());
                editor1.putString("end_date", end.getText().toString().trim());
                editor1.putString("reason", reason.getSelectedItem().toString());
                editor1.commit();
                Intent intent = new Intent(SubmitLeave.this, TextDetails.class);
                startActivity(intent);
            }
        });

        if(sharedPreferences1.contains("details")){
            if(!sharedPreferences1.getString("details", "").equals("<p><br></p>")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    details.setText(Html.fromHtml(sharedPreferences1.getString("details", ""), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    details.setText(Html.fromHtml(sharedPreferences1.getString("details", "")));
                }
            }else{
                details.getText().clear();
            }
        }

        if(sharedPreferences1.contains("start_date")){
            start.setText(sharedPreferences1.getString("start_date", ""));
        }
        if(sharedPreferences1.contains("end_date")){
            end.setText(sharedPreferences1.getString("end_date", ""));
        }
        if(sharedPreferences1.contains("reason")){
            for(int i = 0; i< reason.getAdapter().getCount(); i++){
                if(sharedPreferences1.getString("reason","").equals(reason.getItemAtPosition(i))){
                    reason.setSelection(i);
                }
            }
        }

        submit =  findViewById(R.id.submit_leave_button);
        cancel = findViewById(R.id.submit_leave_cancel_button);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener date = updateLabel(start);
                new DatePickerDialog(SubmitLeave.this, date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener date = updateLabel(end);
                new DatePickerDialog(SubmitLeave.this, date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor1.clear();
                editor1.commit();
                finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("Submitting Leave Form");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Please wait, we are processing your data.");
                progressDialog.show();
                insertData(userEmail);

            }
        });
    }

    private DatePickerDialog.OnDateSetListener updateLabel(EditText editText){
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                String myFormat="yyyy/MM/dd";
                SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
                editText.setText(dateFormat.format(myCalendar.getTime()));
            }
        };

        return date;
    }

    private void insertData(String email){
        final String leaveReason = reason.getSelectedItem().toString();
        /*final String leaveDetails = details.getText().toString().trim();*/
        final String startDate = start.getText().toString().trim();
        final String endDate = end.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_SUBMIT_LEAVE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("error")){
                                editor1.clear();
                                editor1.commit();
                            }

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
                params.put("leave_reason", leaveReason);
                params.put("leave_reason_details", sharedPreferences1.getString("details", ""));
                params.put("date_start", startDate);
                params.put("date_end", endDate);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}
