package com.example.projecttracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    //private static Bundle bundle;
    //private AWSAppSyncClient mAWSAppSyncClient;
    private static final String TAG = MainActivity.class.getSimpleName();

    RecyclerView recyclerView;
    Adapter adapter;
    static Util util;
    static Map<Integer, Project> projects = new HashMap<>();
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
    MediaPlayer mediaPlayer;


    FloatingActionMenu floatingActionMenu;
    FloatingActionButton btnCreate,btnTrack, btnQuery, btnAbout, btnSound;
    boolean soundFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playBgm();
        readConstant();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        util = new Util(getApplicationContext());
        Constants.projects = util.getAllProjectsFile();
        projects = Constants.projects;

        showWarningMsg();

        recyclerView = findViewById(R.id.allProjectsList);
        displayList(Constants.projects);
        btnCreate = (FloatingActionButton) findViewById(R.id.btnCreate);
        btnTrack = (FloatingActionButton) findViewById(R.id.btnTrack);
        btnQuery = (FloatingActionButton) findViewById(R.id.btnQuery);
        btnAbout = (FloatingActionButton) findViewById(R.id.btnAbout);
        btnSound = (FloatingActionButton) findViewById(R.id.btnSound);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CreateProject.class);
                intent.putExtra("ProjectNumber", projects.size()+1);
                view.getContext().startActivity(intent);
            }
        });
        btnTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Track the completed projects
                // Display all the completed projects in a list
                System.out.println("PROJECT LIST LENGTH: "+projects.size());
                System.out.println("Project totoal number: "+Constants.PROJECT_NUMBER);
                String completedProjectNo = "";
                Project p;
                boolean projectStatus;
                for(int i=0; i<Constants.PROJECT_NUMBER; i++){
                    p = projects.get(i);
                    if (p != null) {
                        System.out.println("p is not null, name: "+p.getCourseTitle());
                        projectStatus = p.getIsCompleted();
                        if(projectStatus == true){
                            //use ',' to separate project number
                            completedProjectNo += p.getProjectNumber() + ",";
                        }
                    }
                }
                System.out.println("completeProjectNo is: "+completedProjectNo);
                Intent intent = new Intent(view.getContext(), TrackProject.class);
                intent.putExtra("completeProjects", completedProjectNo);
                view.getContext().startActivity(intent);
            }
        });
        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // another interface to allow queries on the projects
                // show a report on all uncompleted projects
                /*Project p;
                boolean projectStatus;
                String uncompletedProjectNo = "";
                int count = 0;
                for(int i=0; i<Constants.PROJECT_NUMBER; i++){
                    p = projects.get(i);
                    if(p != null){
                        projectStatus = p.getIsCompleted();
                        if(projectStatus == false){
                            uncompletedProjectNo += p.getProjectNumber() + ",";
                        }
                    }
                }
                Intent intent = new Intent(view.getContext(), TrackProject.class);
                intent.putExtra("completeProjects", uncompletedProjectNo);
                view.getContext().startActivity(intent);*/
                Intent intent = new Intent(view.getContext(), Query.class);
                view.getContext().startActivity(intent);

            }
        });
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // pop up name and email
                alert = null;
                builder = null;
                builder = new AlertDialog.Builder(MainActivity.this);
                alert = builder.setTitle("About Us")
                        .setMessage("Developers: Sonal, Chuchu\nEmails: sdwiv045@uottawaa.ca,\n \tcye041@uottawa.ca")
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // do not need operations
                            }
                        }).create();
                alert.show();
            }
        });
        btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!soundFlag){
                    soundFlag = true;
                    musicStatus(soundFlag);
                }else{
                    soundFlag = false;
                    musicStatus(soundFlag);
                }
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        Constants.projects.clear();
        readConstant();
        mediaPlayer.start();
        Constants.projects = util.getAllProjectsFile();
        displayList(Constants.projects);
    }

    private void displayList(Map<Integer, Project> allProjects){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, allProjects);
        System.out.println("After init Adapter");
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy(){
        save(Constants.PROJECT_NUMBER);
        mediaPlayer.release();
        super.onDestroy();
    }

    @Override
    protected void onPause(){
        save(Constants.PROJECT_NUMBER);
        //save(6);
        mediaPlayer.pause();
        super.onPause();
    }

    public void save(int i){
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try{
            out = openFileOutput("data_store", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(i);
            writer.close();
            //System.out.println("Data stored in file.");
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void readConstant(){
        int projectNumber;
        FileInputStream fis = null;
        BufferedReader reader = null;
        try{
            fis = openFileInput("data_store");
            reader = new BufferedReader(new InputStreamReader(fis));
            projectNumber = reader.read();
            reader.close();
            Constants.PROJECT_NUMBER = projectNumber;
            System.out.println("Data retrieved from file. The projectNumber is: " + Constants.PROJECT_NUMBER);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void showWarningMsg(){
        alert = null;
        builder = null;
        Project p;
        int count = 0;
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 2);
        Date within2Date = c.getTime();
        Date projectDueDate;
        String dueMsg = "The following projects will be due in 2 days:\n";
        System.out.println("CONSTANTS: "+projects.size());
        for(int i=0; i<projects.size(); i++){

            p = projects.get(i);
            if (p != null) {
                projectDueDate = p.getDueDate();
                if(projectDueDate.before(within2Date) && !p.getIsCompleted()){
                    dueMsg += "ID: "+p.getProjectNumber()+
                            " Course: "+p.getCourseTitle()+
                            "\n";
                    count++;
                }
            }
        }
        builder = new AlertDialog.Builder(MainActivity.this);
        if(count != 0) {
            alert = builder.setTitle("Warning List")
                    .setMessage(dueMsg)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // do not need operations
                        }
                    }).create();
            alert.show();
        } else{
            alert = builder.setTitle("Warning List")
                    .setMessage("No project are due in 2 days!")
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // do not need operations
                        }
                    }).create();
            alert.show();
        }
    }

    public void playBgm() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bgm);
                mediaPlayer.start();
            }
        }).start();
    }

    public void musicStatus(boolean status){
        if(!soundFlag){
            mediaPlayer.start();
        }else{
            mediaPlayer.pause();
        }
    }

}
