package net.thebookofcode.www.statuscloud.ui.videos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import net.thebookofcode.www.statuscloud.ImageSaver;
import net.thebookofcode.www.statuscloud.R;
import net.thebookofcode.www.statuscloud.SharedViewModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SavedVideoViewFragment extends Fragment {
    String mediaName;
    String path;
    VideoView videoView;
    SharedViewModel viewModel;
    ImageSaver imageSaver;
    FloatingActionsMenu fab;
    FloatingActionButton fabDelete;
    FloatingActionButton fabShare;
    FloatingActionButton fabRename;

    public SavedVideoViewFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageSaver = new ImageSaver(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saved_video_view, container, false);
        fab = view.findViewById(R.id.fab);
        fabDelete = view.findViewById(R.id.actionDelete);
        fabShare = view.findViewById(R.id.actionShare);
        fabRename = view.findViewById(R.id.actionRename);
        videoView = view.findViewById(R.id.videoView);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getPath().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {

                viewModel.getPosition().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        path = strings.get(integer);
                    }
                });
                videoView.setVideoPath(path);
            }
        });

        //videoView.setVideoPath();
        MediaController mediaController = new MediaController(getContext());
        mediaController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //next listener
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //prev listener
            }
        });
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.start();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.collapse();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete")
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                viewModel.getPath().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
                                    @Override
                                    public void onChanged(ArrayList<String> strings) {

                                        viewModel.getPosition().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                                            @Override
                                            public void onChanged(Integer integer) {
                                                path = strings.get(integer);
                                            }
                                        });

                                        File fdelete = new File(path);
                                        if(fdelete.exists()){
                                            try {
                                                fdelete.getCanonicalFile().delete();
                                                Navigation.findNavController(requireView()).navigate(R.id.action_savedVideoViewFragment_to_navigation_saved);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        Toast.makeText(getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).create().show();
                viewModel.getPath().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
                    @Override
                    public void onChanged(ArrayList<String> strings) {

                        viewModel.getPosition().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                            @Override
                            public void onChanged(Integer integer) {
                                path = strings.get(integer);
                            }
                        });
                        imageSaver.saveVideo(new File(path));
                        Toast.makeText(getContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        fabRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final View layout_view = getLayoutInflater().inflate(R.layout.custom_save_name_layout, null);
                builder.setView(layout_view)
                        .setTitle("Rename")
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                fab.collapse();
                            }
                        })
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                fab.collapse();
                                viewModel.getPath().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
                                    @Override
                                    public void onChanged(ArrayList<String> strings) {

                                        viewModel.getPosition().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                                            @Override
                                            public void onChanged(Integer integer) {
                                                path = strings.get(integer);
                                            }
                                        });
                                        EditText editText = layout_view.findViewById(R.id.mediaName);
                                        mediaName = editText.getText().toString();
                                        new File(path).renameTo(new File(path,mediaName));
                                        Toast.makeText(getContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).create().show();


            }
        });
        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.getPath().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
                    @Override
                    public void onChanged(ArrayList<String> strings) {

                        viewModel.getPosition().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                            @Override
                            public void onChanged(Integer integer) {
                                path = strings.get(integer);
                            }
                        });
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("video/*");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        fab.collapse();
                        getContext().startActivity(Intent.createChooser(shareIntent, "Share via..."));
                    }
                });
                //String uri = mediaFiles.get(viewPager2.getCurrentItem()).getAbsolutePath();

            }
        });
    }
}