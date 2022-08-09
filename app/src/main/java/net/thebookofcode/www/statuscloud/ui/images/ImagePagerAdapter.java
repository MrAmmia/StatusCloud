package net.thebookofcode.www.statuscloud.ui.images;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class ImagePagerAdapter extends FragmentStateAdapter {
    private final List<Fragment> lstFragment = new ArrayList<>();
    public ImagePagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return lstFragment.get(position);
    }

    @Override
    public int getItemCount() {
        return lstFragment.size();
    }
    public void AddFragment(Fragment fragment){
        lstFragment.add(fragment);

    }
    public void removeFragment(int position){
        lstFragment.remove(position);
    }
}

