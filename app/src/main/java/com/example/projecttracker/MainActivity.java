package com.example.projecttracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AWSAppSyncClient mAWSAppSyncClient;
    private static final String TAG = MainActivity.class.getSimpleName();

    RecyclerView recyclerView;
    Adapter adapter;
    Button btnCreate;
    Button btnTrack;
    Button btnQuery;
    Button btnAbout;
    Util util;
    public static List<Project> projects = new ArrayList<>();
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    private AmazonS3Client sS3Client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.allProjectsList);
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
        util = new Util(this);
        sS3Client = util.getS3Client(this);
        projects = util.getAllProjectsFile(this);
        displayList(projects);
    }

    private void displayList(List<Project> allProjects){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, allProjects);
        recyclerView.setAdapter(adapter);
    }
}
