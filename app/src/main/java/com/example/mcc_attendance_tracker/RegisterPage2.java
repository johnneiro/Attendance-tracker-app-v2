package com.example.mcc_attendance_tracker;

import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterPage2 extends AppCompatActivity {
    final Calendar myCalendar= Calendar.getInstance();
    View footer;
    TextView previous, next, back;
    ImageView dot1, dot2;
    LinearLayout header;
    LinearLayout dotLayout;
    EditText street,barangay,city,birthdate, province, religion;
    Spinner gender, civilStatus;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page_2);
        getWindow().setEnterTransition(null);
        sharedPreferences = getApplicationContext().getSharedPreferences("RegisterSharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();


        footer = findViewById(R.id.footer);
        previous = footer.findViewById(R.id.btnPrevious);
        next = footer.findViewById(R.id.btnNext);
        previous.setVisibility(View.VISIBLE);

        header = findViewById(R.id.register_page_header_layout);
        dotLayout = footer.findViewById(R.id.dotLayout);
        dot1 = footer.findViewById(R.id.dot1);
        dot2 = footer.findViewById(R.id.dot2);
        dot1.setBackgroundTintList(getResources().getColorStateList(R.color.green));
        dot2.setBackgroundTintList(getResources().getColorStateList(R.color.sky));

        street = findViewById(R.id.register_street_textbox);
        barangay = findViewById(R.id.register_barangay_textbox);
        city = findViewById(R.id.register_city_textbox);
        birthdate =findViewById(R.id.register_birthdate_textbox);
        gender = findViewById(R.id.register_gender_spinner);
        civilStatus = findViewById(R.id.register_civil_status_spinner);
        province = findViewById(R.id.register_province_textbox);
        religion = findViewById(R.id.register_religion_textbox);
        back = findViewById(R.id.register_back_text);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clear();
                editor.commit();
                Intent intent = new Intent(RegisterPage2.this, LoginPage.class);
                startActivity(intent);
                finish();
            }
        });



        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateLabel();
            }
        };

        birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RegisterPage2.this, date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        String[] genderItems = {"Male", "Female", "Rather Not Say"};
        String[] civilStatusItems = {"Single", "Married", "Divorced", "Separated", "Widowed"};
        ArrayAdapter aa = new ArrayAdapter(getApplicationContext(), R.layout.custom_spinner, genderItems);
        aa.setDropDownViewResource(R.layout.custom_spinner_background);
        gender.setAdapter(aa);

        ArrayAdapter bb = new ArrayAdapter(getApplicationContext(), R.layout.custom_spinner, civilStatusItems);
        bb.setDropDownViewResource(R.layout.custom_spinner_background);
        civilStatus.setAdapter(bb);
        getData();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(street.getText().toString().equals("")) {
                    street.setError("This field is required.");
                }else if(barangay.getText().toString().equals("")){
                    barangay.setError("This field is required.");
                }else if(city.getText().toString().equals("")){
                    city.setError("This field is required.");
                }else if(birthdate.getText().toString().equals("")){
                    birthdate.setError("This field is required.");
                }else if(province.getText().toString().equals("")){
                    province.setError("This field is required.");
                }else if(religion.getText().toString().equals("")){
                    religion.setError("This field is required.");
                }else{
                    inputData(street.getText().toString().trim(), barangay.getText().toString().trim(),
                            city.getText().toString().trim(), birthdate.getText().toString().trim(),
                            gender.getSelectedItem().toString().trim(), civilStatus.getSelectedItem().toString().trim(),
                            province.getText().toString().trim(), religion.getText().toString().trim());
                    Intent intent = new Intent(getApplicationContext(), RegisterPage3.class);
                    Pair[] pairs = new Pair[4];

                    pairs[0] = new Pair<View, String>(previous, "transition-previous-btn");
                    pairs[1] = new Pair<View, String>(next, "transition-next-btn");
                    pairs[2] = new Pair<View, String>(header, "transition-header");
                    pairs[3] = new Pair<View, String>(dotLayout, "transition-dot");

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterPage2.this,pairs);
                    getWindow().setExitTransition(null);
                    startActivity(intent, options.toBundle());
                }

            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputData(street.getText().toString().trim(), barangay.getText().toString().trim(),
                        city.getText().toString().trim(), birthdate.getText().toString().trim(),
                        gender.getSelectedItem().toString().trim(), civilStatus.getSelectedItem().toString().trim(),
                        province.getText().toString().trim(), religion.getText().toString().trim());
                Intent intent = new Intent(getApplicationContext(), RegisterPage1.class);
                Pair[] pairs = new Pair[4];

                pairs[0] = new Pair<View, String>(previous, "transition-previous-btn");
                pairs[1] = new Pair<View, String>(next, "transition-next-btn");
                pairs[2] = new Pair<View, String>(header, "transition-header");
                pairs[3] = new Pair<View, String>(dotLayout, "transition-dot");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterPage2.this,pairs);
                getWindow().setExitTransition(null);
                startActivity(intent, options.toBundle());


            }
        });
    }

    private void updateLabel(){
        String myFormat="yyyy/MM/dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        birthdate.setText(dateFormat.format(myCalendar.getTime()));
    }

    @Override
    protected void onDestroy() {
        editor.clear();
        editor.commit();
        super.onDestroy();
    }

    private void getData(){
        if(sharedPreferences.contains("street")){
            street.setText(sharedPreferences.getString("street", ""));
        }
        if(sharedPreferences.contains("barangay")){
            barangay.setText(sharedPreferences.getString("barangay", ""));
        }
        if(sharedPreferences.contains("city")){
            city.setText(sharedPreferences.getString("city", ""));
        }
        if (sharedPreferences.contains("birthdate")){
            birthdate.setText(sharedPreferences.getString("birthdate", ""));
        }
        if(sharedPreferences.contains("province")){
            province.setText(sharedPreferences.getString("province", ""));
        }
        if(sharedPreferences.contains("religion")){
            religion.setText(sharedPreferences.getString("religion", ""));
        }
        if(sharedPreferences.contains("gender")){
            for(int i = 0; i< gender.getAdapter().getCount(); i++){
                if(sharedPreferences.getString("gender", "").equals(gender.getItemAtPosition(i))){
                    gender.setSelection(i);
                }
            }

        }
        if(sharedPreferences.contains("civil_status")){
            for(int i = 0; i< civilStatus.getAdapter().getCount(); i++){
                if(sharedPreferences.getString("civil_status", "").equals(civilStatus.getItemAtPosition(i))){
                    civilStatus.setSelection(i);
                }
            }
        }


    }

    private void inputData(String street, String barangay, String city, String birthdate, String gender, String civil_status, String province, String religion){
        editor.putString("street", street);
        editor.putString("barangay", barangay);
        editor.putString("city", city);
        editor.putString("birthdate", birthdate);
        editor.putString("gender", gender);
        editor.putString("civil_status", civil_status);
        editor.putString("province", province);
        editor.putString("religion", religion);
        editor.commit();
    }
}
