package net.thebookofcode.www.statuscloud.ui.saved;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.thebookofcode.www.statuscloud.Global;
import net.thebookofcode.www.statuscloud.ImageSaver;
import net.thebookofcode.www.statuscloud.LoadingDialog;
import net.thebookofcode.www.statuscloud.R;
import net.thebookofcode.www.statuscloud.ui.active.ActiveRecyclerViewAdapter;
import net.thebookofcode.www.statuscloud.RecyclerviewOnClickListener;
import net.thebookofcode.www.statuscloud.SharedViewModel;

import java.io.File;
import java.util.ArrayList;

public class SavedVideoFragment extends Fragment {
    RecyclerView savedVideoList;
    SwipeRefreshLayout swipeRefreshLayout;
    LoadingDialog loadingDialog;
    ImageSaver imageSaver;
    ArrayList<File> image;
    ArrayList<String> imageNames;
    public SavedRecyclerViewAdapter adapter;
    SharedViewModel viewModel;

    public SavedVideoFragment() {
        // Required empty public constructor
    }

    Handler mediaHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            image = new ArrayList<>();
            image = (ArrayList<File>) bundle.getSerializable("media_images");
            imageNames = bundle.getStringArrayList("media_names");
            adapter.setmMediaFiles(image);
            adapter.setmMediaName(imageNames);
            loadingDialog.dismissDialog();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_saved_video, container, false);
        savedVideoList = root.findViewById(R.id.savedVideoList);
        imageSaver = new ImageSaver(getContext());
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh);
        loadingDialog = new LoadingDialog(requireActivity());
        adapter = new SavedRecyclerViewAdapter(getActivity(), getContext(), imageSaver,"video/*");
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false);
        savedVideoList.setLayoutManager(gridLayoutManager);
        adapter.StatusText = true;
        savedVideoList.setAdapter(adapter);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getFiles();
        adapter.setOnItemClick(new RecyclerviewOnClickListener() {
            @Override
            public void onItemClick(int position) {
                viewModel.setPosition(position);
                viewModel.setPath(imageSaver.getSavedVideoPath());
                Navigation.findNavController(requireView()).navigate(R.id.action_navigation_saved_to_savedVideoViewFragment);
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
    public void filter(String text) {
        adapter.getFilter().filter(text);
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
                File file = new File(Environment.getExternalStorageDirectory() + "/StatusCloud/Videos");
                File[] dirFile = file.listFiles();
                if (dirFile.length != 0) {
                    images = new ArrayList<>();
                    for (File file1 : dirFile) {
                        images.add(file1);
                        imageName.add(file1.getName());
                    }
                }
                mediaBundle.putSerializable("media_images",images);
                mediaBundle.putStringArrayList("media_names",imageName);
                mediaMessage.setData(mediaBundle);
                mediaHandler.sendMessage(mediaMessage);
            }

        };
        Thread mediaThread = new Thread(mediaRunnable);
        mediaThread.start();
    }
}