package com.example.projecttracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class Detail extends AppCompatActivity {
    int id;
    View view;
    String key;
    Util util;
    Context context = this;
    TextView fixProjectNo;
    TextView projectNo;
    TextView fixCourseName;
    TextView courseName;
    TextView fixInstructorName;
    TextView instructorName;
    TextView fixDueDate;
    TextView dueDate;
    TextView fixStatus;
    TextView status;
    TextView fixDescription;
    TextView description;
    Button btnModify;
    Button btnDelete;
    SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        util = new Util(getApplicationContext());

        Intent i = getIntent();
        id = Integer.parseInt(i.getStringExtra("ID"));
        System.out.println("ID: "+id);
        key = "projectFile" + id + ".txt";
        Project p = util.getProject(key);
        if(p == null){
            System.out.println("p is null");
        }
        fixProjectNo = findViewById(R.id.cfixProjectNo);
        fixCourseName = findViewById(R.id.cfixCourseName);
        fixInstructorName = findViewById(R.id.cfixInstructorName);
        fixDueDate = findViewById(R.id.cfixDueDate);
        fixStatus = findViewById(R.id.cfixStatus);
        fixDescription = findViewById(R.id.cfixDescription);
        projectNo = findViewById(R.id.cprojectNo);
        courseName = findViewById(R.id.ccourseName);
        instructorName = findViewById(R.id.cinstructorName);
        dueDate = findViewById(R.id.cdueDate);
        status = findViewById(R.id.cstatus);
        description = findViewById(R.id.cdescription);
        btnModify = findViewById(R.id.btncCreate);
        btnDelete = findViewById(R.id.btnDelete);

        projectNo.setText(p.getProjectNumber());
        courseName.setText(p.getCourseTitle());
        instructorName.setText(p.getInstructorName());
        dueDate.setText(ft.format(p.getDueDate()));
        if(p.getIsCompleted() == true){
            status.setText("Completed");
        } else {
            status.setText("In Progress");
        }
        description.setText(p.getProjectDescription());

        btnDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Delete the project
                MainActivity.util.deleteProject(key);
                onBackPressed();
                Toast.makeText(context, "Project Deleted.", Toast.LENGTH_SHORT).show();
            }
        });
        btnModify.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Modify the project
            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
