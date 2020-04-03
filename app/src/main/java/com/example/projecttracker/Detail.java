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
        fixProjectNo = findViewById(R.id.mfixProjectNo);
        fixCourseName = findViewById(R.id.mfixCourseName);
        fixInstructorName = findViewById(R.id.mfixInstructorName);
        fixDueDate = findViewById(R.id.mfixDueDate);
        fixStatus = findViewById(R.id.mfixStatus);
        fixDescription = findViewById(R.id.mfixDescription);
        projectNo = findViewById(R.id.mprojectNo);
        courseName = findViewById(R.id.mcourseName);
        instructorName = findViewById(R.id.minstructorName);
        dueDate = findViewById(R.id.mdueDate);
        status = findViewById(R.id.mstatus);
        description = findViewById(R.id.mdescription);
        btnModify = findViewById(R.id.btncSave);
        btnDelete = findViewById(R.id.btnDelete);

        projectNo.setText(p.getProjectNumber());
        courseName.setText(p.getCourseTitle());
        instructorName.setText(p.getInstructorName());
        try{
            dueDate.setText(ft.format(p.getDueDate()));
        }catch (Exception e){
            System.out.println("Exception occured");
        }
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
                Intent intent = new Intent(v.getContext(), ModifyProject.class);
                intent.putExtra("ID", id);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
