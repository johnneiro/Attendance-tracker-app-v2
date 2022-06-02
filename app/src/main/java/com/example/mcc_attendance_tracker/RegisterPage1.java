package com.example.mcc_attendance_tracker;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;
import java.util.regex.Pattern;

public class RegisterPage1 extends AppCompatActivity {
    View footer;
    TextView previous, next, back;
    LinearLayout header;
    LinearLayout dotLayout;
    EditText last,first,middle, cnum, email, university;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page_1);
        getWindow().setEnterTransition(null);
        sharedPreferences = getApplicationContext().getSharedPreferences("RegisterSharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        footer = findViewById(R.id.footer);
        previous = footer.findViewById(R.id.btnPrevious);
        next = footer.findViewById(R.id.btnNext);
        previous.setVisibility(View.INVISIBLE);

        header = findViewById(R.id.register_page_header_layout);
        dotLayout = footer.findViewById(R.id.dotLayout);

        last = findViewById(R.id.register_last_name_textbox);
        first = findViewById(R.id.register_first_name_textbox);
        middle = findViewById(R.id.register_middle_name_textbox);
        cnum = findViewById(R.id.register_contact_textbox);
        email = findViewById(R.id.register_email_textbox);
        university = findViewById(R.id.register_university_textbox);
        back = findViewById(R.id.register_back_text);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clear();
                editor.commit();
                Intent intent = new Intent(RegisterPage1.this, LoginPage.class);
                startActivity(intent);
                finish();
            }
        });

        if(sharedPreferences.contains("first_name")){
            first.setText(sharedPreferences.getString("first_name", ""));
        }
        if(sharedPreferences.contains("middle_name")){
            middle.setText(sharedPreferences.getString("middle_name", ""));
        }
        if(sharedPreferences.contains("last_name")){
            last.setText(sharedPreferences.getString("last_name", ""));
        }
        if(sharedPreferences.contains("contact_number")){
            cnum.setText(sharedPreferences.getString("contact_number", ""));
        }
        if(sharedPreferences.contains("email")){
            email.setText(sharedPreferences.getString("email", ""));
        }
        if(sharedPreferences.contains("university")){
            university.setText(sharedPreferences.getString("university", ""));
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(last.getText().toString().equals("")){
                    last.setError("This field is required.");
                }else if(first.getText().toString().equals("")){
                    first.setError("This field is required.");
                }else if(middle.getText().toString().equals("")){
                    middle.setError("This field is required.");
                }else if(cnum.getText().toString().equals("")){
                    cnum.setError("This field is required.");
                }else if(email.getText().toString().equals("")){
                    email.setError("This field is required.");
                }else if(university.getText().toString().equals("")){
                    university.setError("This field is required.");
                }else{
                    if(isValid(email.getText().toString().trim())){
                        editor.putString("first_name", first.getText().toString().trim());
                        editor.putString("middle_name", middle.getText().toString().trim());
                        editor.putString("last_name", last.getText().toString().trim());
                        editor.putString("contact_number", cnum.getText().toString().trim());
                        editor.putString("email", email.getText().toString().trim());
                        editor.putString("university", university.getText().toString().trim());
                        editor.commit();

                        Intent intent = new Intent(getApplicationContext(), RegisterPage2.class);
                        Pair[] pairs = new Pair[4];

                        pairs[0] = new Pair<View, String>(previous, "transition-previous-btn");
                        pairs[1] = new Pair<View, String>(next, "transition-next-btn");
                        pairs[2] = new Pair<View, String>(header, "transition-header");
                        pairs[3] = new Pair<View, String>(dotLayout, "transition-dot");

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterPage1.this,pairs);
                        getWindow().setExitTransition(null);
                        startActivity(intent, options.toBundle());
                    }else{
                        email.setError("Invalid email");
                    }
                }

                /*}*/


            }
        });
    }

    @Override
    protected void onDestroy() {
        editor.clear();
        editor.commit();
        super.onDestroy();
    }

    public static boolean isValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}
