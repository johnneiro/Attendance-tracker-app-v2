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


public class ViewProfile extends Fragment implements View.OnClickListener{

    com.github.clans.fab.FloatingActionButton fabSignOut;
    com.github.clans.fab.FloatingActionButton fabChangePassword;
    com.github.clans.fab.FloatingActionButton fabEditProfile;
    com.github.clans.fab.FloatingActionButton fabViewLeave;
    com.github.clans.fab.FloatingActionMenu fabMenu;

    ProgressDialog progressDialog;
    public ViewProfile(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    TextView name, applicationID, email, contactNumber, street, address, birthdate, gender, reqHrs, renHrs, remHrs;
    CircleImageView imageView;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userEmail;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_profile, container, false);
        progressDialog.setMessage("Please wait, we are loading your data.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("UserDataSharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        userEmail = sharedPreferences.getString("email", "");

        name = v.findViewById(R.id.txtUserName);
        applicationID = v.findViewById(R.id.txtApplicationID);
        email = v.findViewById(R.id.txtUserEmail);
        contactNumber = v.findViewById(R.id.txtUserContactNumber);
        street = v.findViewById(R.id.txtStreetName);
        address = v.findViewById(R.id.txtAddress);
        birthdate = v.findViewById(R.id.txtBirthdate);
        gender = v.findViewById(R.id.txtGender);
        imageView = v.findViewById(R.id.view_profile_imageview);
        remHrs = v.findViewById(R.id.remainingHrs);
        renHrs = v.findViewById(R.id.renderedHrs);
        reqHrs = v.findViewById(R.id.requiredHrs);

        fabSignOut = v.findViewById(R.id.fabSignOut);
        fabChangePassword = v.findViewById(R.id.fabChangePassword);
        fabViewLeave = v.findViewById(R.id.fabLeave);
        fabEditProfile = v.findViewById(R.id.fabEditProfile);
        fabMenu = v.findViewById(R.id.fabMenu);

        fabSignOut.setOnClickListener(this);
        fabChangePassword.setOnClickListener(this);
        fabViewLeave.setOnClickListener(this);
        fabEditProfile.setOnClickListener(this);


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
                                applicationID.setText(obj.getString("application_id"));
                                email.setText(obj.getString("email"));
                                contactNumber.setText(obj.getString("contact_number"));
                                street.setText(obj.getString("address1"));
                                address.setText(obj.getString("address2"));
                                birthdate.setText(obj.getString("birthdate"));
                                gender.setText(obj.getString("gender"));
                                reqHrs.setText(sharedPreferences.getString("required_hours", ""));
                                remHrs.setText(obj.getString("remaining_hrs"));
                                renHrs.setText(obj.getString("rendered_hrs"));


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



    public void onClick(View v){
        if(v == fabEditProfile){
            fabMenu.close(true);
            Intent intent = new Intent(getActivity(), EditProfile.class);
            startActivity(intent);
        }

        if(v==fabChangePassword){
            fabMenu.close(true);
            Intent intent = new Intent(getActivity(), ChangePassword.class);
            startActivity(intent);
        }

        if(v==fabSignOut){
            editor.putBoolean("isLoggedIn", false);
            editor.commit();
            Intent intent = new Intent(getActivity(), LoginPage.class);
            startActivity(intent);
            getActivity().finish();
        }

        if(v==fabViewLeave){
            fabMenu.close(true);
            Intent intent = new Intent(getActivity(), LeaveStatus.class);
            startActivity(intent);

        }
    }

    @Override
    public void onResume() {
        getUserData();
        super.onResume();
    }
}