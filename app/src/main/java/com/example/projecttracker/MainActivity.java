package com.example.projecttracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

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
    private AmazonS3Client sS3Client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.allProjectsList);
        util = new Util(this);
        sS3Client = util.getS3Client(this);
        projects = util.getAllProjectsFile(this);
        displayList(projects);

        //getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));

        // Initiate the AWSMobileClient if not initialized
        /*
        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>(){
            @Override
            public void onResult(UserStateDetails userStateDetails){
                Log.i(TAG, "AWSMobileClient initialized. User State is " + userStateDetails.getUserState());
                if(Constants.PROJECT_NUMBER > 0){
                    for(int i=1; i<=Constants.PROJECT_NUMBER; i++){
                        // Download all existing project files
                        util.downloadWithTransferUtility(getApplicationContext(), i);
                    }
                    displayList(projects);
                }
            }

            @Override
            public void onError(Exception e){
                Log.e(TAG, "Initialization error.", e);
            }
        });
        */
    }

/*
    public void uploadWithTransferUtility(Project project){
        Constants.PROJECT_NUMBER++;
        int projectId = Constants.PROJECT_NUMBER;
        String key = "public/projectFile" + projectId + ".txt";
        String downloadChlidPath = "downloadFile" + projectId + ".txt";

        TransferUtility transferUtility =
                TransferUtility.builder()
                .context(getApplicationContext())
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .s3Client(new AmazonS3Client(AWSMobileClient.getInstance()))
                .build();
        File file = new File(getApplicationContext().getFilesDir(), downloadChlidPath);

        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(project);
        } catch (IOException e){
            e.printStackTrace();
        }

        TransferObserver uploadObserver =
                transferUtility.upload(
                        key,
                        new File(getApplicationContext().getFilesDir(), downloadChlidPath));

        // Attach a listener to the observer to get state update and progress notification
        uploadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state){
                if(TransferState.COMPLETED == state){
                    // handle a completed upload
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float)bytesCurrent / (float)bytesTotal) * 100;
                int percentDone = (int)percentDonef;

                Log.d(TAG, "ID:" + id + "bytesCurrent: " + bytesCurrent
                    + " bytesTotal: " + bytesTotal + " " +
                        percentDone + "%");
            }

            @Override
            public void onError(int id, Exception e){
                // handle errors
            }
        });
    }


    public void downloadWithTransferUtility(int projectId){
        String key = "public/project" + projectId + ".txt";
        final String downloadChlidPath = "downloadFile" + projectId + ".txt";
        TransferUtility transferUtility =
                TransferUtility.builder()
                .context(getApplicationContext())
                .awsConfiguration((AWSMobileClient.getInstance().getConfiguration()))
                .s3Client(new AmazonS3Client(AWSMobileClient.getInstance()))
                .build();
        TransferObserver downloadObserver =
                transferUtility.download(
                        key,
                        new File(getApplicationContext().getFilesDir(), downloadChlidPath));
        // Attach a listener to the observer to get state update and progress notification
        downloadObserver.setTransferListener(new TransferListener(){
            @Override
            public void onStateChanged(int id, TransferState state){
                if(TransferState.COMPLETED == state){
                    // handle a completed download
                    // extract project information from the downloaded file
                    Project p = new Project();
                    try{
                        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(downloadChlidPath));
                        p = (Project) ois.readObject();
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                    projects.add(p);
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal){
                float percentDonef = ((float)bytesCurrent/(float)bytesTotal)*100;
                int percentDone = (int)percentDonef;

                Log.d("Your Activity", " ID:" + id + " bytesCurrent: "
                    + bytesCurrent + " bytesTotal: " + bytesTotal + " " + percentDone
                    + "%");
            }

            @Override
            public void onError(int id, Exception e){
                // handle errors
            }
        });
    }
 */

    private void displayList(List<Project> allProjects){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, allProjects);
        recyclerView.setAdapter(adapter);
    }
}
