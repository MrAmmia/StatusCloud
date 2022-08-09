package net.thebookofcode.www.statuscloud.ui.images;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.thebookofcode.www.statuscloud.ImageSaver;
import net.thebookofcode.www.statuscloud.R;

import java.io.File;

public class ImageFragment extends Fragment {
    File file;
    ImageSaver imageSaver;
    ImageView image;
    public ImageFragment() {
        // Required empty public constructor
    }

    public static ImageFragment getNewInstance(File file) {
        ImageFragment fragment = new ImageFragment();
        fragment.file = file;
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        image = view.findViewById(R.id.image);
        Bitmap b = imageSaver.load(file);
        image.setImageBitmap(b);
        return view;
    }
}