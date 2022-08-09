package net.thebookofcode.www.statuscloud.ui.saved;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import net.thebookofcode.www.statuscloud.R;
import net.thebookofcode.www.statuscloud.ui.UIAdapter;

public class SavedFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    UIAdapter uiAdapter;
    private SavedImageFragment savedImageFragment;
    private SavedVideoFragment savedVideoFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //savedViewModel =
        //      new ViewModelProvider(this).get(SavedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_saved, container, false);
        tabLayout = root.findViewById(R.id.tabsLayout);
        viewPager2 = root.findViewById(R.id.viewPager2);
        FragmentManager fm = getChildFragmentManager();
        uiAdapter = new UIAdapter(fm, getLifecycle());
        savedImageFragment = new SavedImageFragment();
        savedVideoFragment = new SavedVideoFragment();
        uiAdapter.AddFragment(savedImageFragment);
        uiAdapter.AddFragment(savedVideoFragment);
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
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (!newText.equals("")) {
                        if (viewPager2.getCurrentItem() == 0) {
                            savedImageFragment.filter(newText);
                            savedImageFragment.adapter.notifyDataSetChanged();
                        } else if (viewPager2.getCurrentItem() == 1) {
                            savedVideoFragment.filter(newText);
                            savedVideoFragment.adapter.notifyDataSetChanged();
                        }
                    }

                    return false;
                }
            });
        }
        return true;
    }
}