package com.example.projecttracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;

public class Detail extends AppCompatActivity {
    int id;
    View view;
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
    SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent i = getIntent();
        id = i.getIntExtra("ID", 0);
        Project p = MainActivity.projects.get(id-1);
        fixProjectNo = findViewById(R.id.fixProjectNo);
        fixCourseName = findViewById(R.id.fixCourseName);
        fixInstructorName = findViewById(R.id.fixInstructorName);
        fixDueDate = findViewById(R.id.fixDueDate);
        fixStatus = findViewById(R.id.fixStatus);
        fixDescription = findViewById(R.id.fixDescription);
        projectNo = findViewById(R.id.projectNo);
        courseName = findViewById(R.id.courseName);
        instructorName = findViewById(R.id.instructorName);
        dueDate = findViewById(R.id.dueDate);
        status = findViewById(R.id.status);
        description = findViewById(R.id.descrption);
        btnModify = findViewById(R.id.btnModify);
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
