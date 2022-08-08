package com.example.mcc_attendance_tracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class TeamMonitoringStatus extends Fragment implements View.OnClickListener {

    ProgressDialog progressDialog;
    public TeamMonitoringStatus(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }
    RecyclerView recyclerView;
    TextView name, teamName, posName;
    CircleImageView imageView;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userEmail;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_team_monitoring, container, false);
        progressDialog.setMessage("Please wait, we are loading your data.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("UserDataSharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        userEmail = sharedPreferences.getString("email", "");

        name = v.findViewById(R.id.txtUserName);
        imageView = v.findViewById(R.id.view_profile_imageview);
        teamName = v.findViewById(R.id.team_name);
        posName = v.findViewById(R.id.position_name);

        getUserData();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 5000);
        return v;
    }

        private void getUserData(){
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constants.URL_VIEW_PROFILE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject obj = new JSONObject(response);
                                if(!obj.getBoolean("error")){
                                    String full_name = obj.getString("first_name") + " " + obj.getString("last_name");
                                    name.setText(full_name);
                                    teamName.setText(obj.getString("team"));
                                    posName.setText(obj.getString("position"));


                                    if(obj.getString("image").equals("") || obj.getString("image").isEmpty()){
                                        Glide.with(getActivity().getApplicationContext())
                                                .load("https://lh6.googleusercontent.com/-aVxLdAudhNw/AAAAAAAAAAI/AAAAAAAAAAA/fk51M3swPdQ/c-rp-mo-s256-br100/photo.jpg")
                                                .centerCrop()
                                                .into(imageView);

                                    }else{
                                        String url = "http://uip.melhamconstruction.ph/attendance/api/uploaded_profile/";
                                        Glide.with(getActivity().getApplicationContext())
                                                .load(url + obj.getString("image"))
                                                .centerCrop()
                                                .into(imageView);
                                    }

                                }else{
                                    Toast.makeText(getActivity().getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                                }

                            }catch(JSONException e){
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
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
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            requestQueue.add(stringRequest);
        }

    @Override
    public void onClick(View view) {
        getUserData();
        super.onResume();
    }
}
