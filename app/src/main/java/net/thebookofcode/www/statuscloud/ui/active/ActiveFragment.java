package net.thebookofcode.www.statuscloud.ui.active;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import net.thebookofcode.www.statuscloud.ImageSaver;
import net.thebookofcode.www.statuscloud.R;
import net.thebookofcode.www.statuscloud.ui.UIAdapter;

import java.util.ArrayList;

public class ActiveFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    private ImageSaver imageSaver;
    //private SharedViewModel viewModel;
    UIAdapter uiAdapter;
    private ActiveImageFragment activeImageFragment;
    private ActiveVideoFragment activeVideoFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //activeViewModel =
          //      new ViewModelProvider(this).get(ActiveViewModel.class);
        View root = inflater.inflate(R.layout.fragment_active, container, false);
        imageSaver = new ImageSaver(getContext());
        FragmentManager fm = getChildFragmentManager();
        uiAdapter = new UIAdapter(fm,getLifecycle());
        tabLayout = root.findViewById(R.id.tabsLayout);
        viewPager2 = root.findViewById(R.id.viewPager2);
        activeVideoFragment = new ActiveVideoFragment();
        activeImageFragment = new ActiveImageFragment();
        uiAdapter.AddFragment(activeImageFragment);
        uiAdapter.AddFragment(activeVideoFragment);
        viewPager2.setAdapter(uiAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {

                tabLayout.selectTab(tabLayout.getTabAt(position));

            }
        });
        return root;
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main,menu);
        menu.findItem(R.id.action_search).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}