package net.thebookofcode.www.statuscloud.ui.upload;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.thebookofcode.www.statuscloud.Global;
import net.thebookofcode.www.statuscloud.ImageSaver;
import net.thebookofcode.www.statuscloud.LoadingDialog;
import net.thebookofcode.www.statuscloud.R;
import net.thebookofcode.www.statuscloud.UploadActivity;
import net.thebookofcode.www.statuscloud.ui.active.ActiveRecyclerViewAdapter;
import net.thebookofcode.www.statuscloud.ui.login.LoginActivity;
import net.thebookofcode.www.statuscloud.ui.saved.SavedRecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;

public class UploadFragment extends Fragment {

    private UploadViewModel uploadViewModel;
    RecyclerView uploadList;
    SwipeRefreshLayout swipeRefreshLayout;
    LoadingDialog loadingDialog;
    ImageSaver imageSaver;
    FloatingActionButton fab;
    ArrayList<File> image;
    ArrayList<String> imageNames;
    UploadRecyclerViewAdapter adapter;
    private FirebaseAuth mAuth;

    Handler mediaHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            image = new ArrayList<>();
            image = (ArrayList<File>) bundle.getSerializable("media_key");
            imageNames = bundle.getStringArrayList("media_names");
            adapter.setmMediaFiles(image);
            adapter.setmMediaName(imageNames);
            loadingDialog.dismissDialog();
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // uploadViewModel =
        //       new ViewModelProvider(this).get(UploadViewModel.class);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        View root = inflater.inflate(R.layout.fragment_upload, container, false);
        uploadList = root.findViewById(R.id.uploadList);
        fab = root.findViewById(R.id.fab);
        fab.bringToFront();
        imageSaver = new ImageSaver(getContext());
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh);
        loadingDialog = new LoadingDialog(requireActivity());
        adapter = new UploadRecyclerViewAdapter(getActivity(), getContext(), imageSaver);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false);
        uploadList.setLayoutManager(gridLayoutManager);
        adapter.StatusText = true;
        uploadList.setAdapter(adapter);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getFiles();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser == null){
                    //reload();
                    new AlertDialog.Builder(getContext()).setTitle("Login Required")
                            .setMessage("Do you wish to sign-up and login")
                            .setPositiveButton("YES", (dialogInterface, i) -> {
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                startActivity(intent);
                            }).setNegativeButton("NO", null).show();
                }else{
                    Intent intent = new Intent(getContext(), UploadActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
    @Override
    public void onPause() {
        super.onPause();
        if(Global.sActionMode != null){
            Global.sActionMode.finish();
        }
    }

    private void getFiles() {
        loadingDialog.startAlertDialog();
        Runnable mediaRunnable = new Runnable() {
            Message mediaMessage = mediaHandler.obtainMessage();
            Bundle mediaBundle = new Bundle();
            @Override
            public void run() {
                ArrayList<File> images = new ArrayList<>();
                ArrayList<String> imageName = new ArrayList<>();
                File file = new File(Environment.getExternalStorageDirectory() + "/StatusCloud/.Uploads");
                File[] dirFile = file.listFiles();
                if (dirFile.length != 0) {
                    images = new ArrayList<>();
                    for (File file1 : dirFile) {
                        if (file1.getName().endsWith("jpg") || file1.getName().endsWith("mp4") || file1.getName().endsWith("png")) {
                            images.add(file1);
                            imageName.add(file1.getName());

                        } else {
                            continue;
                        }
                    }
                }
                mediaBundle.putSerializable("media_key",images);
                mediaBundle.putStringArrayList("media_names",imageName);
                mediaMessage.setData(mediaBundle);
                mediaHandler.sendMessage(mediaMessage);
            }

        };
        Thread mediaThread = new Thread(mediaRunnable);
        mediaThread.start();
    }
}