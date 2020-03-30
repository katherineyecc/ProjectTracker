package com.example.projecttracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackProject extends AppCompatActivity {
    private String projectStr;
    String[] completedProjectNo;
    Map<Integer, Project> cProjects = new HashMap<>();
    Util util;
    RecyclerView recyclerView;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_project);

        Intent intent = getIntent();
        projectStr = intent.getStringExtra("completeProjects");
        System.out.println("ProjectStr is: "+projectStr);
        if(projectStr.length() == 0){
            onBackPressed();
            Toast.makeText(this, "No Completed Project!", Toast.LENGTH_SHORT);
        }
        completedProjectNo = projectStr.split(",");
        util = new Util(getApplicationContext());
        for(int i=0; i<completedProjectNo.length; i++){
            String key = "projectFile" + completedProjectNo[i] + ".txt";
            Project p = util.getProject(key);
            System.out.println("p is: "+p.getCourseTitle());
            cProjects.put(i, p);
        }
        recyclerView = findViewById(R.id.completedProjectsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, cProjects);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
