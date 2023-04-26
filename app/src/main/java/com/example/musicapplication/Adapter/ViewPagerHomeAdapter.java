package com.example.musicapplication.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.musicapplication.Fragment.HomeFragment;
import com.example.musicapplication.Fragment.PersonalMusicFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerHomeAdapter extends FragmentStatePagerAdapter {
    public ViewPagerHomeAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    public Fragment getItem(int position) {
        switch (position)
        {
            case 0: return new HomeFragment();
            case 1: return new PersonalMusicFragment();
            default: return new HomeFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        String Title="";
        switch (position){
            case 0:
                Title="Home";
                break;
            case 1:
                Title="Personal Music";
                break;
        }
        return Title;
    }
}