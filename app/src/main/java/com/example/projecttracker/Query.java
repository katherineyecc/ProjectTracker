package com.example.projecttracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.projecttracker.MainActivity.util;

public class Query extends AppCompatActivity {

    CheckBox inProg, comp;
    Button beforedate;
    static Map<Integer, Project> projects = new HashMap<>();
    EditText cdueDate;
    SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        inProg = findViewById(R.id.inprog);
        comp = findViewById(R.id.comp);
        beforedate = findViewById(R.id.beforedate);

        projects = util.getAllProjectsFile();

        inProg.setChecked(false);
        comp.setChecked(false);

        inProg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Project p;
                boolean projectStatus;
                String uncompletedProjectNo = "";
                for(int i=0; i<projects.size(); i++){
                    p = projects.get(i);
                    if(p != null){
                        projectStatus = p.getIsCompleted();
                        if(!projectStatus){
                            uncompletedProjectNo += p.getProjectNumber() + ",";
                        }
                    }
                }
                Intent intent = new Intent(view.getContext(), TrackProject.class);
                intent.putExtra("completeProjects", uncompletedProjectNo);
                view.getContext().startActivity(intent);
            }
        });

        comp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Project p;
                boolean projectStatus;
                String uncompletedProjectNo = "";
                int count = 0;
                for(int i=0; i<projects.size(); i++){
                    p = projects.get(i);
                    if(p != null){
                        projectStatus = p.getIsCompleted();
                        if(projectStatus){
                            uncompletedProjectNo += p.getProjectNumber() + ",";
                        }
                    }
                }
                Intent intent = new Intent(view.getContext(), TrackProject.class);
                intent.putExtra("completeProjects", uncompletedProjectNo);
                view.getContext().startActivity(intent);
            }
        });

        beforedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Project p;
                String uncompletedProjectNo = "";
                Date dueDate = new Date();
                Date projectDueDate;
                cdueDate = findViewById(R.id.mdueDate);
                try {
                    dueDate = ft.parse(cdueDate.getText().toString());
                }catch(ParseException e) {
                    e.printStackTrace();
                }

                for(int i=0; i<projects.size(); i++){

                    p = projects.get(i);
                    if (p != null) {
                        projectDueDate = p.getDueDate();
                        if(projectDueDate.before(dueDate)){
                            uncompletedProjectNo += p.getProjectNumber() + ",";
                        }
                    }
                }

                Intent intent = new Intent(view.getContext(), TrackProject.class);
                intent.putExtra("completeProjects", uncompletedProjectNo);
                view.getContext().startActivity(intent);
            }
        });



    }
}
