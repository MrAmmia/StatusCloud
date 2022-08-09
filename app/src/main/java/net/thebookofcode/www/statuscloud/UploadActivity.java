package net.thebookofcode.www.statuscloud;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ImageView imageView;
    private VideoView videoView;
    private LinearLayout container;
    private Button btnUpload;
    String imageName;
    String videoName;
    String PROGRESS_TAG = "Progress";
    ProgressDialog progressDialog;
    Uri videoUri;
    Uri imageUri;
    ImageSaver imageSaver;
    String mediaUrl;
    StorageReference ref;
    private static final String TAG = "Upload Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        imageView = findViewById(R.id.imageView);
        videoView = findViewById(R.id.videoView);
        container = findViewById(R.id.container);
        btnUpload = findViewById(R.id.btnUpload);
        imageSaver = new ImageSaver(this);
        mAuth = FirebaseAuth.getInstance();
        imageName = UUID.randomUUID().toString() + ".jpg";
        videoName = UUID.randomUUID().toString() + ".mp4";
        progressDialog = new ProgressDialog(this);
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if (result != null) {
                btnUpload.setEnabled(true);
                if (result.toString().contains("image")) {
                    imageUri = result;
                    imageView.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.GONE);
                    container.setVisibility(View.INVISIBLE);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageView.setImageBitmap(bitmap);
                    Toast.makeText(UploadActivity.this, "Image Selected", Toast.LENGTH_SHORT).show();
                } else if (result.toString().contains("video")) {
                    videoUri = result;
                    imageView.setVisibility(View.GONE);
                    videoView.setVisibility(View.VISIBLE);
                    container.setVisibility(View.INVISIBLE);
                    videoView.setVideoURI(result);
                    MediaController mediaController = new MediaController(UploadActivity.this);
                    mediaController.setAnchorView(videoView);
                    videoView.setMediaController(mediaController);
                    //videoView.start();
                    Toast.makeText(UploadActivity.this, "Video Selected", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(UploadActivity.this, "Unsupported file type", Toast.LENGTH_SHORT).show();
                }
            }
        }
    });

    public void uploadFile(View view) {
        UploadTask uploadTask;
        if (imageView.getVisibility() == View.VISIBLE) {
            // Get the data from an ImageView as bytes
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            progressDialog.startAlertDialog();
            ref = FirebaseStorage.getInstance().getReference().child("Images").child(mAuth.getCurrentUser().getEmail()).child(imageName);
            uploadTask = FirebaseStorage.getInstance().getReference().child("Images").child(mAuth.getCurrentUser().getEmail()).child(imageName).putBytes(data);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    Log.i("URL",FirebaseStorage.getInstance().getReference().child("Images").child(mAuth.getCurrentUser().getEmail()).child(imageName).getDownloadUrl().toString());
                    return FirebaseStorage.getInstance().getReference().child("Images").child(mAuth.getCurrentUser().getEmail()).child(imageName).getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    progressDialog.dismissDialog();
                    Toast.makeText(UploadActivity.this, "Upload failed!!", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...


                    imageView.setVisibility(View.GONE);
                    container.setVisibility(View.VISIBLE);
                    progressDialog.dismissDialog();
                    Toast.makeText(UploadActivity.this, "Upload successful!!", Toast.LENGTH_SHORT).show();
                    imageSaver.saveUploadImage(getContentResolver(), imageUri);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    int progress = (int) ((100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                    progressDialog.setText(progress);
                    Log.d(PROGRESS_TAG, "Upload is " + progress + "% done");
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(PROGRESS_TAG, "Upload is paused");
                }
            });
            FirebaseStorage.getInstance().getReference().child("Images").child(mAuth.getCurrentUser().getEmail()).child(imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    mediaUrl = uri.toString();
                }
            });

        }
        else if (videoView.getVisibility() == View.VISIBLE) {
            ref = FirebaseStorage.getInstance().getReference().child("Videos").child(mAuth.getCurrentUser().getEmail()).child(videoName);
            uploadTask = FirebaseStorage.getInstance().getReference().child("Videos").child(mAuth.getCurrentUser().getEmail()).child(videoName).putFile(videoUri);
            progressDialog.startAlertDialog();
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    progressDialog.dismissDialog();
                    Toast.makeText(UploadActivity.this, "Upload failed!!", Toast.LENGTH_SHORT).show();
                                    }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...

                    videoView.setVisibility(View.GONE);
                    container.setVisibility(View.VISIBLE);
                    progressDialog.dismissDialog();
                    Toast.makeText(UploadActivity.this, "Upload successful!!", Toast.LENGTH_SHORT).show();
                    imageSaver.saveUploadVideo(getContentResolver(), videoUri);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                    progressDialog.setText(progress);
                    Log.d(PROGRESS_TAG, "Upload is " + progress + "% done");
                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(PROGRESS_TAG, "Upload is paused");
                }
            });
            FirebaseStorage.getInstance().getReference().child("Videos").child(mAuth.getCurrentUser().getEmail()).child(videoName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    mediaUrl = uri.toString();
                }
            });

        }
        Global.addUri(mediaUrl);
        ExampleJobService.ref = ref;
        ComponentName componentName = new ComponentName(this,ExampleJobService.class);
        JobInfo info = new JobInfo.Builder(123,componentName)
            .setMinimumLatency(6*60*60*1000)
            .setRequiresCharging(false)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)
            .build();
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if(resultCode == JobScheduler.RESULT_SUCCESS){
            Log.d(TAG,"Job Scheduling succeeded");
        }else{Log.d(TAG,"Job Scheduling failed");}
    }

    public void getVideo(View view) {
        mGetContent.launch("video/*");
    }

    public void getPhoto(View view) {
        mGetContent.launch("image/*");
    }
}