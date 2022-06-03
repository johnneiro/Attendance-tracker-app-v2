package com.example.mcc_attendance_tracker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jkcarino.rtexteditorview.RTextEditorView;

public class TextDetails extends AppCompatActivity implements View.OnClickListener{
    Spinner heading;
    TextView bold, italic, underlined, indent, outdent, centerAlign, rightAlign, justifyAlign, leftAlign, bulletList, numberedList, redo, undo;
    RTextEditorView rTextEditorView;
    TextView check;
    int checkColor;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_text);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait, we are loading your data.");
        sharedPreferences = getApplicationContext().getSharedPreferences("DetailsSharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        rTextEditorView = (RTextEditorView) findViewById(R.id.rtexteditor);

        if(sharedPreferences.contains("details")){
            rTextEditorView.setHtml(sharedPreferences.getString("details", ""));
        }

        heading = findViewById(R.id.headingSpinner);
        bold = findViewById(R.id.boldText);
        italic = findViewById(R.id.italicText);
        underlined = findViewById(R.id.underlinedText);
        indent = findViewById(R.id.indentText);
        outdent = findViewById(R.id.outdentText);
        centerAlign = findViewById(R.id.alignCenterText);
        rightAlign = findViewById(R.id.alignRightText);
        leftAlign = findViewById(R.id.alignLeftText);
        justifyAlign = findViewById(R.id.alignJustifyText);
        bulletList = findViewById(R.id.dotListText);
        numberedList = findViewById(R.id.numberListText);
        redo = findViewById(R.id.redoText);
        undo = findViewById(R.id.undoText);
        check = findViewById(R.id.checkText);

        checkColor = bold.getCurrentTextColor();

        bold.setOnClickListener(this);
        italic.setOnClickListener(this);
        underlined.setOnClickListener(this);
        indent.setOnClickListener(this);
        outdent.setOnClickListener(this);
        centerAlign.setOnClickListener(this);
        rightAlign.setOnClickListener(this);
        leftAlign.setOnClickListener(this);
        justifyAlign.setOnClickListener(this);
        bulletList.setOnClickListener(this);
        numberedList.setOnClickListener(this);
        undo.setOnClickListener(this);
        redo.setOnClickListener(this);
        check.setOnClickListener(this);



        String[] headingItems = {"Paragraph","H1", "H2", "H3", "H4", "H5", "H6"};
        ArrayAdapter aa = new ArrayAdapter(getApplicationContext(), R.layout.custom_spinner, headingItems);
        aa.setDropDownViewResource(R.layout.custom_spinner_background);
        heading.setAdapter(aa);

        heading.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        rTextEditorView.setNormal();
                        break;
                    case 1:
                        rTextEditorView.setHeading(1);
                        break;
                    case 2:
                        rTextEditorView.setHeading(2);
                        break;
                    case 3:
                        rTextEditorView.setHeading(3);
                        break;
                    case 4:
                        rTextEditorView.setHeading(4);
                        break;
                    case 5:
                        rTextEditorView.setHeading(5);
                        break;
                    case 6:
                        rTextEditorView.setHeading(6);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 2000);

    }


    @Override
    public void onClick(View v) {
        if(v==bold){
            rTextEditorView.setBold();
            if(bold.getCurrentTextColor() == checkColor){
                bold.setTextColor(getResources().getColor(R.color.black));
            }else{
                bold.setTextColor(checkColor);
            }

        }

        if(v==italic){
            rTextEditorView.setItalic();
            if(italic.getCurrentTextColor() == checkColor){
                italic.setTextColor(getResources().getColor(R.color.black));
            }else{
                italic.setTextColor(checkColor);
            }
        }

        if(v==underlined){
            rTextEditorView.setUnderline();
            if(underlined.getCurrentTextColor() == checkColor){
                underlined.setTextColor(getResources().getColor(R.color.black));
            }else{
                underlined.setTextColor(checkColor);
            }
        }
        if(v==indent){
            rTextEditorView.setIndent();
        }
        if(v==outdent){
            rTextEditorView.setOutdent();


        }
        if(v==centerAlign){
            rTextEditorView.setAlignCenter();

        }
        if(v==leftAlign){
            rTextEditorView.setAlignLeft();
        }
        if(v==rightAlign){
            rTextEditorView.setAlignRight();
        }
        if(v==justifyAlign){
            rTextEditorView.setAlignJustify();
        }
        if(v==bulletList){
            rTextEditorView.setUnorderedList();
        }
        if(v==numberedList){
            rTextEditorView.setOrderedList();
        }
        if(v==undo){
            rTextEditorView.undo();

        }
        if(v==redo){
            rTextEditorView.redo();
        }
        if(v==check){
            String data = rTextEditorView.getHtml();
            editor.putString("details", data);
            editor.commit();

            finish();

        }
    }

}
