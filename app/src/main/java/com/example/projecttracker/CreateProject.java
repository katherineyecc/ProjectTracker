package com.example.projecttracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.services.s3.AmazonS3Client;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CreateProject extends AppCompatActivity {
    int currentProjectNum;
    Project project;
    TextView cfixProjectNo;
    TextView cfixCourseName;
    TextView cfixInstructorName;
    TextView cfixDueDate;
    TextView cfixStatus;
    TextView cfixDecription;
    TextView cprojectNo;
    EditText ccourseName;
    EditText cinstructorName;
    EditText cdueDate;
    CheckedTextView cstatus;
    EditText cdescription;
    Button btncCreate;

    Util util;
    private AmazonS3Client sS3Client;
    private boolean completedIsChecked = false;
    SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        Intent intent = getIntent();
        currentProjectNum = intent.getIntExtra("ProjectNumber",0);
        project = new Project(currentProjectNum);
        initUI();
        util = new Util(this);
        sS3Client = util.getS3Client(this);
    }

    public void initUI(){
        cfixProjectNo = findViewById(R.id.cfixProjectNo);
        cfixCourseName = findViewById(R.id.cfixCourseName);
        cfixInstructorName = findViewById(R.id.cfixInstructorName);
        cfixDueDate = findViewById(R.id.cfixDueDate);
        cfixStatus = findViewById(R.id.cfixDueDate);
        cfixDecription = findViewById(R.id.cfixDescription);
        cprojectNo = findViewById(R.id.cprojectNo);
        cprojectNo.setText(Integer.toString(currentProjectNum));
        ccourseName = findViewById(R.id.ccourseName);
        cinstructorName = findViewById(R.id.cinstructorName);
        cdueDate = findViewById(R.id.cdueDate);
        cstatus = findViewById(R.id.cstatus);
        cstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completedIsChecked = true;
            }
        });
        cdescription = findViewById(R.id.cdescription);
        btncCreate = findViewById(R.id.btncCreate);
        btncCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                project.setCourseTitle(ccourseName.getText().toString());
                project.setInstructorName(cinstructorName.getText().toString());
                try {
                    project.setDueDate(ft.parse(cdueDate.getText().toString()));
                }catch(ParseException e) {
                    e.printStackTrace();
                }
                if(completedIsChecked == true){
                    project.setIsCompleted();
                }
                project.setProjectDescription(cdescription.getText().toString());
                uploadProject();
            }
        });
    }

    public void uploadProject(){
        util.uploadWithTransferUtility(this, project);
        onBackPressed();
        Toast.makeText(this, "Project Created.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
