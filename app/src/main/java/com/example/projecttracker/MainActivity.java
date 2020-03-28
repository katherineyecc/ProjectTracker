package com.example.projecttracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    //private static Bundle bundle;
    private AWSAppSyncClient mAWSAppSyncClient;
    private static final String TAG = MainActivity.class.getSimpleName();

    RecyclerView recyclerView;
    Adapter adapter;
    Button btnCreate;
    Button btnTrack;
    Button btnQuery;
    Button btnAbout;
    static Util util;
    //static Map<Integer, Project> projects = new HashMap<>();
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    //private AmazonS3Client sS3Client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readConstant();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        util = new Util(getApplicationContext());
        Constants.projects = util.getAllProjectsFile();
        System.out.println("The projects arraylist size is: "+Constants.projects.size());
        //System.out.println("Project File course name: "+projects.get(1).getCourseTitle());

        recyclerView = findViewById(R.id.allProjectsList);
        displayList(Constants.projects);
        btnCreate = findViewById(R.id.btnCreate);
        btnTrack = findViewById(R.id.btnTrack);
        btnQuery = findViewById(R.id.btnQuery);
        btnAbout = findViewById(R.id.btnAbout);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CreateProject.class);
                intent.putExtra("ProjectNumber", Constants.PROJECT_NUMBER);
                view.getContext().startActivity(intent);
            }
        });
        btnTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Track the completed projects
            }
        });
        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // another interface to allow queries on the projects
            }
        });
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // pop up name and email
                alert = null;
                builder = new AlertDialog.Builder(MainActivity.this);
                alert = builder.setTitle("About Us")
                        .setMessage("Developers: Sonal, Chuchu\nEmails: cye041@uottawa.ca")
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // do not need operations
                            }
                        }).create();
                alert.show();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        Constants.projects.clear();
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
        //save(Constants.PROJECT_NUMBER);
        super.onDestroy();
    }

    @Override
    protected void onPause(){
        save(Constants.PROJECT_NUMBER);
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
            //System.out.println("Data retrieved from file. The projectNumber is: " + projectNumber);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
