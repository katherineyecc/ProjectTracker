package com.example.projecttracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
        sS3Client = util.getS3Client();
    }

    public void initUI(){
        cfixProjectNo = findViewById(R.id.mfixProjectNo);
        cfixCourseName = findViewById(R.id.mfixCourseName);
        cfixInstructorName = findViewById(R.id.mfixInstructorName);
        cfixDueDate = findViewById(R.id.mfixDueDate);
        cfixStatus = findViewById(R.id.mfixDueDate);
        cfixDecription = findViewById(R.id.mfixDescription);
        cprojectNo = findViewById(R.id.mprojectNo);
        cprojectNo.setText(Integer.toString(currentProjectNum));
        ccourseName = findViewById(R.id.mcourseName);
        cinstructorName = findViewById(R.id.minstructorName);
        cdueDate = findViewById(R.id.mdueDate);
        cstatus = findViewById(R.id.mstatus);
        cstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completedIsChecked = true;
            }
        });
        cdescription = findViewById(R.id.mdescription);
        btncCreate = findViewById(R.id.btncSave);
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
        //util.uploadWithTransferUtility(project);
        String key = "projectFile"+project.getProjectNumber()+".txt";
        util.uploadProjectObject(key, project);
        onBackPressed();
        Toast.makeText(this, "Project Created.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
