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

public class RegisterPage3 extends AppCompatActivity implements View.OnClickListener{
    final Calendar myCalendar= Calendar.getInstance();
    View footer;
    TextView previous, next, back;
    ImageView dot2, dot3, dot1;
    LinearLayout header;
    LinearLayout dotLayout;
    EditText startDate, endDate, requiredHrs, appID;
    Spinner company, department;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page_3);
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
        dot1.setBackgroundTintList(getResources().getColorStateList(R.color.green));
        dot2 = footer.findViewById(R.id.dot2);
        dot3 = footer.findViewById(R.id.dot3);

        dot2.setBackgroundTintList(getResources().getColorStateList(R.color.green));
        dot3.setBackgroundTintList(getResources().getColorStateList(R.color.sky));

        startDate = findViewById(R.id.register_start_date_textbox);
        endDate = findViewById(R.id.register_end_date_textbox);
        requiredHrs = findViewById(R.id.register_required_hours_textbox);
        company = findViewById(R.id.register_company_spinner);
        department = findViewById(R.id.register_department_spinner);
        appID = findViewById(R.id.register_app_id_textbox);
        back = findViewById(R.id.register_back_text);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clear();
                editor.commit();
                Intent intent = new Intent(RegisterPage3.this, LoginPage.class);
                startActivity(intent);
                finish();
            }
        });

        startDate.setOnClickListener(this);
        endDate.setOnClickListener(this);

        String[] companyItems = {"Melham Construction Corporation", "Anafara Corporation", "VisVis Travel & Tours"};
        String[] departmentItems = {"Information Technology", "Mechanical Engineering",
                "Mechatronics Engineering",
                "Industrial Engineering",
                "Petroleum Engineering",
                "Electrical Engineering",
                "Electronics Engineering",
                "Industrial Technology",
                "Civil Engineering",
                "Sales And Marketing Department",
                "Operations Department",
                "Creative Team Department",
                "Business Development Department",
                "Liaison Department",
                "Client Relations Department",
                "Social Media Marketing Department",
                "Human Resources Department",
                "Quality Control Department",
                "Learning and Development Department",
                "Manager",
                "General Manager",
                "Supervisor",
                "Officer-in-Charge",
                "Over all Officer-in-Charge"};
        ArrayAdapter aa = new ArrayAdapter(getApplicationContext(), R.layout.custom_spinner, companyItems);
        aa.setDropDownViewResource(R.layout.custom_spinner_background);
        company.setAdapter(aa);

        ArrayAdapter bb = new ArrayAdapter(getApplicationContext(), R.layout.custom_spinner, departmentItems);
        bb.setDropDownViewResource(R.layout.custom_spinner_background);
        department.setAdapter(bb);
        getData();


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startDate.getText().toString().equals("")){
                    startDate.setError("This field is required.");
                }else if(endDate.getText().toString().equals("")){
                    endDate.setError("This field is required.");
                }else if(requiredHrs.getText().toString().equals("")){
                    requiredHrs.setError("This field is required.");
                }else if(appID.getText().toString().equals("")){
                    appID.setError("This field is required.");
                }else{
                    inputData(startDate.getText().toString().trim(), endDate.getText().toString().trim(),
                            requiredHrs.getText().toString().trim(), company.getSelectedItem().toString().trim(),
                            department.getSelectedItem().toString().trim(), appID.getText().toString().trim());
                    Intent intent = new Intent(getApplicationContext(), RegisterPage4.class);
                    Pair[] pairs = new Pair[4];

                    pairs[0] = new Pair<View, String>(previous, "transition-previous-btn");
                    pairs[1] = new Pair<View, String>(next, "transition-next-btn");
                    pairs[2] = new Pair<View, String>(header, "transition-header");
                    pairs[3] = new Pair<View, String>(dotLayout, "transition-dot");

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterPage3.this,pairs);
                    getWindow().setExitTransition(null);
                    startActivity(intent, options.toBundle());
                }

            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputData(startDate.getText().toString().trim(), endDate.getText().toString().trim(),
                        requiredHrs.getText().toString().trim(), company.getSelectedItem().toString().trim(),
                        department.getSelectedItem().toString().trim(), appID.getText().toString().trim());
                Intent intent = new Intent(getApplicationContext(), RegisterPage2.class);
                Pair[] pairs = new Pair[4];

                pairs[0] = new Pair<View, String>(previous, "transition-previous-btn");
                pairs[1] = new Pair<View, String>(next, "transition-next-btn");
                pairs[2] = new Pair<View, String>(header, "transition-header");
                pairs[3] = new Pair<View, String>(dotLayout, "transition-dot");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterPage3.this,pairs);
                getWindow().setExitTransition(null);
                startActivity(intent, options.toBundle());



            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_start_date_textbox:
                DatePickerDialog.OnDateSetListener date = updateLabel(startDate);
                new DatePickerDialog(RegisterPage3.this, date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.register_end_date_textbox:
                DatePickerDialog.OnDateSetListener date1 = updateLabel(endDate);
                new DatePickerDialog(RegisterPage3.this, date1,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;


        }
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

    private void getData(){
        if(sharedPreferences.contains("start_date")){
            startDate.setText(sharedPreferences.getString("start_date", "").trim());
        }
        if(sharedPreferences.contains("end_date")){
            endDate.setText(sharedPreferences.getString("end_date", "").trim());
        }
        if(sharedPreferences.contains("required_hours")){
            requiredHrs.setText(sharedPreferences.getString("required_hours", "").trim());
        }
        if(sharedPreferences.contains("company")){
            for(int i = 0; i< company.getAdapter().getCount(); i++){
                if(sharedPreferences.getString("company","").equals(company.getItemAtPosition(i))){
                    company.setSelection(i);
                }
            }
        }
        if(sharedPreferences.contains("department")){
            for(int i = 0; i< department.getAdapter().getCount(); i++){
                if(sharedPreferences.getString("department","").equals(department.getItemAtPosition(i))){
                    department.setSelection(i);
                }
            }
        }
        if(sharedPreferences.contains("application_id")){
            appID.setText(sharedPreferences.getString("application_id", ""));
        }
    }

    private void inputData(String start, String end, String reqHrs, String companyName, String departmentName, String applicationID){
        editor.putString("start_date", start);
        editor.putString("end_date", end);
        editor.putString("required_hours", reqHrs);
        editor.putString("company", companyName);
        editor.putString("department", departmentName);
        editor.putString("application_id", applicationID);
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        editor.clear();
        editor.commit();
        super.onDestroy();
    }
}
