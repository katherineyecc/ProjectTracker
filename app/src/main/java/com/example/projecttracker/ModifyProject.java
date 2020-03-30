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

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ModifyProject extends AppCompatActivity {
    private int projectID;
    Project project;
    Util util;
    TextView mfixProjectNo;
    TextView mfixCourseName;
    TextView mfixInstructorName;
    TextView mfixDueDate;
    TextView mfixStatus;
    TextView mfixDecription;
    TextView mprojectNo;
    EditText mcourseName;
    EditText minstructorName;
    EditText mdueDate;
    CheckedTextView mstatus;
    EditText mdescription;
    Button btncSave;
    boolean completedIsChecked = false;
    SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_project);

        Intent intent = getIntent();
        projectID = intent.getIntExtra("ID", 0);
        project = new Project(projectID);
        util = new Util(this);
        String key = "projectFile" + projectID + ".txt";
        project = util.getProject(key);
        initUI();
    }

    public void initUI(){
        mfixProjectNo = findViewById(R.id.mfixProjectNo);
        mfixCourseName = findViewById(R.id.mfixCourseName);
        mfixInstructorName = findViewById(R.id.mfixInstructorName);
        mfixDueDate = findViewById(R.id.mfixDueDate);
        mfixStatus = findViewById(R.id.mfixDueDate);
        mfixDecription = findViewById(R.id.mfixDescription);
        mprojectNo = findViewById(R.id.mprojectNo);
        mprojectNo.setText(Integer.toString(projectID));
        mcourseName = findViewById(R.id.mcourseName);
        minstructorName = findViewById(R.id.minstructorName);
        mdueDate = findViewById(R.id.mdueDate);
        mstatus = findViewById(R.id.mstatus);
        mdescription = findViewById(R.id.mdescription);
        btncSave = findViewById(R.id.btncSave);

        mcourseName.setText(project.getCourseTitle());
        minstructorName.setText(project.getInstructorName());
        mdueDate.setText(ft.format(project.getDueDate()));
        if(project.getIsCompleted() == true){
            mstatus.setText("Change the status to In Progress");
            //completedIsChecked = true;
        } else{
            mstatus.setText("Change the status to Completed");
            //completedIsChecked = false;
        }
        mstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mstatus.setChecked(true);
                project.setIsCompleted();
            }
        });
        mdescription.setText(project.getProjectDescription());

        btncSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                project.setCourseTitle(mcourseName.getText().toString());
                project.setInstructorName(minstructorName.getText().toString());
                try {
                    project.setDueDate(ft.parse(mdueDate.getText().toString()));
                }catch(ParseException e) {
                    e.printStackTrace();
                }
                project.setProjectDescription(mdescription.getText().toString());
                uploadProject();
            }
        });
    }

    public void uploadProject(){
        //util.uploadWithTransferUtility(project);
        String key = "projectFile"+project.getProjectNumber()+".txt";
        util.uploadProjectObject(key, project);
        goToMain();
        Toast.makeText(this, "Project Modified.", Toast.LENGTH_SHORT).show();
    }

    private void goToMain(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
