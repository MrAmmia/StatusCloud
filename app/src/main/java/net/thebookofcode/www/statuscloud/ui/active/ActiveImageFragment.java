package net.thebookofcode.www.statuscloud.ui.active;

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
import net.thebookofcode.www.statuscloud.RecyclerviewOnClickListener;
import net.thebookofcode.www.statuscloud.SharedViewModel;

import java.io.File;
import java.util.ArrayList;

public class ActiveImageFragment extends Fragment {
    RecyclerView activeImageList;
    SwipeRefreshLayout swipeRefreshLayout;
    LoadingDialog loadingDialog;
    ImageSaver imageSaver;
    ArrayList<File> image;
    ActiveRecyclerViewAdapter adapter;
    SharedViewModel viewModel;

    public ActiveImageFragment() {
        // Required empty public constructor
    }
    Handler mediaHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            image = new ArrayList<>();
            image = (ArrayList<File>) bundle.getSerializable("media_key");
            adapter.setmMediaFiles(image);
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
        View root = inflater.inflate(R.layout.fragment_active_image, container, false);
        activeImageList = root.findViewById(R.id.activeImageList);
        imageSaver = new ImageSaver(getContext());
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh);
        loadingDialog = new LoadingDialog(requireActivity());
        adapter = new ActiveRecyclerViewAdapter(getActivity(), getContext(), imageSaver,"image/*");
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false);
        activeImageList.setLayoutManager(gridLayoutManager);
        adapter.StatusText = false;
        activeImageList.setAdapter(adapter);
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
                viewModel.setImagePagerArray(image);
                Navigation.findNavController(requireView()).navigate(R.id.action_navigation_active_to_imagePagerFragment);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if(Global.aActionMode != null){
            Global.aActionMode.finish();
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
                File file = new File(Environment.getExternalStorageDirectory() + "/WhatsApp/Media/.Statuses/");
                File[] dirFile = file.listFiles();
                if (dirFile.length != 0) {
                    images = new ArrayList<>();
                    for (File file1 : dirFile) {
                        if (file1.getName().endsWith("nomedia") || file1.getName().endsWith("mp4")) {
                            continue;

                        } else {
                            images.add(file1);
                        }
                    }
                }
                mediaBundle.putSerializable("media_key",images);
                mediaMessage.setData(mediaBundle);
                mediaHandler.sendMessage(mediaMessage);
            }

        };
        Thread mediaThread = new Thread(mediaRunnable);
        mediaThread.start();
    }
}