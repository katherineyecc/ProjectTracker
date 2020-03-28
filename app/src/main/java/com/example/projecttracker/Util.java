package com.example.projecttracker;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
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


public class Util {

    private static final String bucketName = "myprojecttrackerbucket";

    public static final String TAG = Util.class.getSimpleName();

    private static AmazonS3Client sS3Client;
    private AWSCredentialsProvider sMobileClient;
    private TransferUtility sTransferUtility;
    private List<S3ObjectSummary> s3ObjList;
    private Context context;
    List<Project> projects = new ArrayList<>();

    public Util(Context context){
        this.context = context;
        getTransferUtility();
    }

    private AWSCredentialsProvider getCredProvider() {
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
/*
    private CognitoCachingCredentialsProvider getCognitoCredProvider(Context context){
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                "us-east-1:07ead51f-783d-4d1f-bad6-b99c05e7b0b8", // Identity pool ID
                Regions.US_EAST_1 // Region
        );
        return credentialsProvider;
    }
*/
    public AmazonS3Client getS3Client() {
        if (sS3Client == null) {
            try {
                String regionString = new AWSConfiguration(context)
                        .optJsonObject("S3TransferUtility")
                        .getString("Region");
                //sS3Client.setRegion(Region.getRegion(regionString));
                sS3Client = new AmazonS3Client(getCredProvider(), Region.getRegion(regionString));
                //sS3Client = new AmazonS3Client(getCognitoCredProvider(context), Region.getRegion(regionString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        System.out.println("End of getS3Client!");
        return sS3Client;
    }

    private void getTransferUtility() {
        if (sTransferUtility == null) {
            sTransferUtility = TransferUtility.builder()
                    .context(context)
                    .s3Client(getS3Client())
                    .awsConfiguration(new AWSConfiguration(context))
                    .build();
            System.out.println("Building TransferUtility!");
        }
        //return sTransferUtility;
    }

    public void downloadWithTransferUtility(String key){
        //String key = "public/project" + projectId + ".txt";
        //final String downloadChlidPath = "downloadFile" + projectId + ".txt";
        //final String filename = context.getFilesDir() + key;
        //System.out.println("filename = key : " + filename);
        System.out.println("Start downloading...");
        //getTransferUtility();
        //final CountDownLatch latch = new CountDownLatch(1);
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
                    System.out.println("TransferState is COMPLETED!");
                    Project p = new Project();
                    try{
                        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                        p = (Project) ois.readObject();
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                    MainActivity.projects.add(p);
                    System.out.println("Util projects arraylist size: "+MainActivity.projects.size());
                    //latch.countDown();
                }
                else if(TransferState.WAITING_FOR_NETWORK == state){
                    System.out.println("Download is waiting for the network...");
                } else {
                    System.out.println("Download is in other status...");
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
                System.out.println("DownloadError happens!");
            }
        });
        /*try{
            System.out.println("Latch waiting...");
            latch.await();
        }catch(InterruptedException e){
            e.printStackTrace();
        }*/
        System.out.println("Downloading Ends!");
    }

    public void uploadWithTransferUtility(Project project){
        int projectId = Constants.PROJECT_NUMBER;
        String key = "projectFile" + projectId + ".txt";
        String downloadChlidPath = "downloadFile" + projectId + ".txt";
        /*
        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(context)
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance()))
                        .build();
        */
        //TransferUtility transferUtility = getTransferUtility();
        File file = new File(context.getFilesDir(), downloadChlidPath);

        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(project);
        } catch (IOException e){
            e.printStackTrace();
        }

        TransferObserver uploadObserver =
                sTransferUtility.upload(
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
        Constants.PROJECT_NUMBER++;
        file.deleteOnExit();
    }
/*
    private class GetFileListTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(Void... inputs){
            s3ObjList = sS3Client.listObjects(bucketName).getObjectSummaries();
            for(S3ObjectSummary summary : s3ObjList){
                downloadWithTransferUtility(summary.getKey());
            }
            System.out.println("DOWNLOAD Finished");
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            downloadCompleted = true;
            System.out.println("IN ASYNCTASK, TASK FINISHED.");
            clatch.countDown();

        }
    }
*/

/*
    public void getAllProjectsFile(){
        final CountDownLatch clatch = new CountDownLatch(1);
        //new GetFileListTask().execute();
        new Thread(new Runnable(){
            @Override
            public void run(){
                System.out.println("Getting buckets and summaries...");
                s3ObjList = sS3Client.listObjects(bucketName).getObjectSummaries();
                for(S3ObjectSummary summary : s3ObjList){
                    downloadWithTransferUtility(summary.getKey());

                }
                System.out.println("countDownLatch -1");
                clatch.countDown();
            }
        }).start();

        try{
            System.out.println("STILL WAITING...");
            clatch.await();
            System.out.println("WAITING ENDS!");
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
*/

    public List<Project> getAllProjectsFile(){
        final CountDownLatch clatch = new CountDownLatch(1);
        //new GetFileListTask().execute();
        new Thread(new Runnable(){
            @Override
            public void run(){
                System.out.println("Getting buckets and summaries...");
                s3ObjList = sS3Client.listObjects(bucketName).getObjectSummaries();
                for(S3ObjectSummary summary : s3ObjList){
                    //downloadWithTransferUtility(summary.getKey());
                    S3Object s3Object = sS3Client.getObject(bucketName, summary.getKey());
                    Project p = new Project();
                    try{
                        ObjectInputStream ois = new ObjectInputStream(
                                new S3ObjectInputStream(s3Object.getObjectContent()));
                        p = (Project) ois.readObject();
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                    projects.add(p);
                    //System.out.println("Project File course name: "+projects.get(0).getCourseTitle());
                }
                System.out.println("countDownLatch -1");
                clatch.countDown();
            }
        }).start();

        try{
            System.out.println("STILL WAITING...");
            clatch.await();
            System.out.println("WAITING ENDS!");
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        return projects;
    }

    public void deleteProject(String key){
        sS3Client.deleteObject(bucketName, key);
        System.out.println("Deleting the project...");
    }

}
