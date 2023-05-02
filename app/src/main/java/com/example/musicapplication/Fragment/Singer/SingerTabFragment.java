package com.example.musicapplication.Fragment.Singer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapplication.Adapter.ViewPagerHomeAdapter;
import com.example.musicapplication.Adapter.ViewPagerSingerAdapter;
import com.example.musicapplication.Model.Singer;
import com.example.musicapplication.R;
import com.google.android.material.tabs.TabLayout;

public class SingerTabFragment extends Fragment {
    TabLayout tablayout;
    ViewPager viewPager;
    ViewPagerSingerAdapter viewPagerSingerAdapter;
    Singer singer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_singer_tab, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            singer = bundle.getParcelable("singer");
        }

        tablayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        viewPagerSingerAdapter= new ViewPagerSingerAdapter(getFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPagerSingerAdapter.setSinger(singer);
        viewPager.setAdapter(viewPagerSingerAdapter);
        tablayout.setupWithViewPager(viewPager);
        return view;
    }
}