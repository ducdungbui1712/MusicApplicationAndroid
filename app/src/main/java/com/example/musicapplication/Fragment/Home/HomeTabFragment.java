package com.example.musicapplication.Fragment.Home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapplication.Adapter.ViewPagerHomeAdapter;
import com.example.musicapplication.R;
import com.google.android.material.tabs.TabLayout;

public class HomeTabFragment extends Fragment {
    TabLayout tablayout;
    ViewPager viewPager;
    ViewPagerHomeAdapter viewPagerHomeAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_tab, container, false);
        tablayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        viewPagerHomeAdapter= new ViewPagerHomeAdapter(getFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(viewPagerHomeAdapter);
        tablayout.setupWithViewPager(viewPager);
        return view;
    }
}