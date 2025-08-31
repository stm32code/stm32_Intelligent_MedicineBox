package com.example.heartrateandtemperaturemonitor.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;


import com.example.heartrateandtemperaturemonitor.HomeFragment;
import com.example.heartrateandtemperaturemonitor.PopularizeFragment;

import java.util.ArrayList;
import java.util.List;

public class NavigationViewPagerAdapter extends FragmentStateAdapter {
    private List<Fragment> fragments = new ArrayList<>();

    public NavigationViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        fragments.add(new HomeFragment());
        fragments.add(new PopularizeFragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
