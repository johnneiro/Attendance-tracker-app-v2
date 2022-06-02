package com.example.mcc_attendance_tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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

public class TimeAlarmReceiver extends BroadcastReceiver {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String userEmail;
    int lastID;


    @Override
    public void onReceive(Context context, Intent intent) {
        //12:00AM Reset background service
        sharedPreferences = context.getSharedPreferences("UserDataSharedPref", Context.MODE_PRIVATE);
        editor= sharedPreferences.edit();

        userEmail= sharedPreferences.getString("email", "");
        lastID = sharedPreferences.getInt("last_id", 0);


        editor.putInt("button_status", 0);
        editor.commit();


        String required_hrs = sharedPreferences.getString("required_hours", "");
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_TIME_VOID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("error")) {
                                if(!obj.getString("message").equals("")){
                                    NotificationHelper notificationHelper = new NotificationHelper(context);
                                    notificationHelper.createNotification();
                                }
                            }else{
                                Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params  = new HashMap<>();
                params.put("email", userEmail);
                params.put("last_id", String.valueOf(lastID));
                params.put("required_hours", required_hrs);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }

}
