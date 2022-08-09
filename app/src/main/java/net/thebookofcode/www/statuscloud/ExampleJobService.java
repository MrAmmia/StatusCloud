package net.thebookofcode.www.statuscloud;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ExampleJobService extends JobService {
    private static final String TAG = "ExampleJobService";
    private static final String DELETE_TAG = "DELETE";
    private boolean jobCancelled = false;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    public static StorageReference ref;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        doBackgroundWork(jobParameters);
        return true;
    }

    private void doBackgroundWork(JobParameters jobParameters) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                        Log.d(DELETE_TAG,"It worked fine");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                        Log.d(DELETE_TAG,"It didnt worked fine");
                    }
                });
                jobFinished(jobParameters,true);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        jobCancelled = true;
        return true;
    }
}
