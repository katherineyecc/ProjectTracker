package com.example.projecttracker;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.client.results.SignInResult;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.example.projecttracker.MainActivity.projects;

public class Util {
    private static String AWS_ACCESS_KEY = "AKIAURRNFPA7DRINWVVA";
    private static String AWS_SECRET_KEY = "adnGPSPhBbFRPF7oMKHdvAezstBHjkRHy8V5f5tV";
    private static final String bucketName = "myprojecttrackerbucket";

    public static final String TAG = Util.class.getSimpleName();

    private static AmazonS3Client sS3Client;
    private AWSCredentialsProvider sMobileClient;
    private TransferUtility sTransferUtility;
    private List<S3ObjectSummary> s3ObjList;
    private Context currentContext;
    List<Project> projects = new ArrayList<>();

    public Util(Context context){
        this.currentContext = context;
    }

    private AWSCredentialsProvider getCredProvider(Context context) {
        if (sMobileClient == null) {
            final CountDownLatch latch = new CountDownLatch(1);
            AWSMobileClient.getInstance().initialize(context, new Callback<UserStateDetails>() {
                @Override
                public void onResult(UserStateDetails result) {
                    latch.countDown();
                    System.out.println("Current Status: " + result.getUserState());
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "onError: ", e);
                    latch.countDown();
                }
            });
            try {
                latch.await();
                sMobileClient = AWSMobileClient.getInstance();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("End of getCredProvider!");
        return sMobileClient;
    }

    private CognitoCachingCredentialsProvider getCognitoCredProvider(Context context){
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                "us-east-1:07ead51f-783d-4d1f-bad6-b99c05e7b0b8", // Identity pool ID
                Regions.US_EAST_1 // Region
        );
        return credentialsProvider;
    }

    public AmazonS3Client getS3Client(Context context) {
        if (sS3Client == null) {
            try {
                String regionString = new AWSConfiguration(context)
                        .optJsonObject("S3TransferUtility")
                        .getString("Region");
                //sS3Client.setRegion(Region.getRegion(regionString));
                sS3Client = new AmazonS3Client(getCredProvider(context), Region.getRegion(regionString));
                //sS3Client = new AmazonS3Client(getCognitoCredProvider(context), Region.getRegion(regionString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        System.out.println("End of getS3Client!");
        return sS3Client;
    }

    private TransferUtility getTransferUtility(Context context) {
        if (sTransferUtility == null) {
            sTransferUtility = TransferUtility.builder()
                    .context(context)
                    .s3Client(getS3Client(context))
                    .awsConfiguration(new AWSConfiguration(context))
                    .build();
        }
        return sTransferUtility;
    }

    public void downloadWithTransferUtility(Context context, String key){
        //String key = "public/project" + projectId + ".txt";
        //final String downloadChlidPath = "downloadFile" + projectId + ".txt";
        //final String filename = context.getFilesDir() + key;
        //System.out.println("filename = key : " + filename);
        getTransferUtility(context);
        final File file = new File(context.getFilesDir(), key);
        System.out.println("file path: " + file.getPath());
        TransferObserver downloadObserver =
                sTransferUtility.download(
                        key,
                        file);
        // Attach a listener to the observer to get state update and progress notification
        downloadObserver.setTransferListener(new TransferListener(){
            @Override
            public void onStateChanged(int id, TransferState state){
                if(TransferState.COMPLETED == state){
                    // handle a completed download
                    // extract project information from the downloaded file
                    Project p = new Project();
                    try{
                        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
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

    public void uploadWithTransferUtility(Context context, Project project){
        Constants.PROJECT_NUMBER++;
        int projectId = Constants.PROJECT_NUMBER;
        String key = "public/projectFile" + projectId + ".txt";
        String downloadChlidPath = "downloadFile" + projectId + ".txt";
        /*
        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(context)
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance()))
                        .build();
        */
        TransferUtility transferUtility = getTransferUtility(context);
        File file = new File(context.getFilesDir(), downloadChlidPath);

        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(project);
        } catch (IOException e){
            e.printStackTrace();
        }

        TransferObserver uploadObserver =
                transferUtility.upload(
                        key,
                        file);

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
        file.deleteOnExit();
    }
/*
    private List<S3ObjectSummary> getFileList(){
        // Queries files in the bucket from S3
        String bucketLocatoin = sS3Client.getBucketLocation("myprojecttrackerbucket");
        System.out.println("Bucket Location is: " + bucketLocatoin);
        s3ObjList = sS3Client.listObjects(bucketName).getObjectSummaries();
        return s3ObjList;
    }

    public List<Project> getAllProjectsFile(Context context){
        for(S3ObjectSummary summary : getFileList()){
            downloadWithTransferUtility(context, summary.getKey());
        }
        return projects;
    }
 */
    private class GetFileListTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute(){
            // ...
        }

        @Override
        protected Void doInBackground(Void... inputs){
            s3ObjList = sS3Client.listObjects(bucketName).getObjectSummaries();
            for(S3ObjectSummary summary : s3ObjList){
                downloadWithTransferUtility(currentContext, summary.getKey());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            // ...
        }
    }

    public List<Project> getAllProjectsFile(Context context){
        new GetFileListTask().execute();
        return projects;
    }

}
