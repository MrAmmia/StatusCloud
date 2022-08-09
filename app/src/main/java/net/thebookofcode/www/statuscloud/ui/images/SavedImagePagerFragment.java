package net.thebookofcode.www.statuscloud.ui.images;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import net.thebookofcode.www.statuscloud.ImageSaver;
import net.thebookofcode.www.statuscloud.R;
import net.thebookofcode.www.statuscloud.SharedViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class SavedImagePagerFragment extends Fragment {

    ImagePagerAdapter imagePagerAdapter;
    ImageSaver imageSaver;
    SharedViewModel viewModel;
    ViewPager2 viewPager2;
    int position;
    FloatingActionsMenu fab;
    FloatingActionButton fabDelete;
    FloatingActionButton fabShare;
    FloatingActionButton fabRename;
    String mediaName;
    ArrayList<File> mediaFiles;

    public SavedImagePagerFragment() {
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
        View view = inflater.inflate(R.layout.fragment_saved_image_pager, container, false);
        viewPager2 = view.findViewById(R.id.viewPager2);
        fab = view.findViewById(R.id.fab);
        fabDelete = view.findViewById(R.id.actionDelete);
        fabShare = view.findViewById(R.id.actionShare);
        fabRename = view.findViewById(R.id.actionRename);
        FragmentManager fm = getChildFragmentManager();
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getImagePagerArray().observe(getViewLifecycleOwner(), new Observer<ArrayList<File>>() {
            @Override
            public void onChanged(ArrayList<File> files) {
                mediaFiles = files;
                for(File file : files){
                    imagePagerAdapter.AddFragment(ImageFragment.getNewInstance(file));
                }
            }
        });
        imagePagerAdapter = new ImagePagerAdapter(fm,getLifecycle());
        viewPager2.setAdapter(imagePagerAdapter);
        viewModel.getPosition().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        viewPager2.setCurrentItem(integer);
                    }
                });
            }
        });


        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.collapse();
                viewModel.getImagePagerArray().observe(getViewLifecycleOwner(), new Observer<ArrayList<File>>() {
                    @Override
                    public void onChanged(ArrayList<File> files) {
                        imagePagerAdapter.removeFragment(viewPager2.getCurrentItem());
                        files.get(viewPager2.getCurrentItem()).delete();
                        Toast.makeText(getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        if(viewPager2.getCurrentItem() < imagePagerAdapter.getItemCount()){
                            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
                        }else{
                            viewPager2.setCurrentItem(0);
                        }
                        imagePagerAdapter.notifyDataSetChanged();

                    }
                });

            }
        });
        fabRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final View layout_view = getLayoutInflater().inflate(R.layout.custom_save_name_layout,null);
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
                                viewModel.getImagePagerArray().observe(getViewLifecycleOwner(), new Observer<ArrayList<File>>() {
                                    @Override
                                    public void onChanged(ArrayList<File> files) {
                                        EditText editText = layout_view.findViewById(R.id.mediaName);
                                        mediaName = editText.getText().toString();
                                        files.get(viewPager2.getCurrentItem()).renameTo(new File(files.get(viewPager2.getCurrentItem()).getAbsolutePath(),mediaName));
                                        //imageSaver.saveImageWithName(files.get(viewPager2.getCurrentItem()),mediaName);
                                        Toast.makeText(getContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                                        imagePagerAdapter.removeFragment(viewPager2.getCurrentItem());

                                    }
                                });
                            }
                        }).create().show();


            }
        });
        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = mediaFiles.get(viewPager2.getCurrentItem()).getAbsolutePath();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uri));
                //shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,toSend);
                shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                fab.collapse();
                getContext().startActivity(Intent.createChooser(shareIntent,"Share via..."));
            }
        });
    }
}