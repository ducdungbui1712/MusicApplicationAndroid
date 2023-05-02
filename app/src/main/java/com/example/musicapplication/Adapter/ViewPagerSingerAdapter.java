package com.example.musicapplication.Adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.musicapplication.Fragment.HomeFragment;
import com.example.musicapplication.Fragment.PersonalMusicFragment;
import com.example.musicapplication.Fragment.Singer.SingerAlbumsFragment;
import com.example.musicapplication.Fragment.Singer.SingerSongsFragment;
import com.example.musicapplication.Model.Singer;

public class ViewPagerSingerAdapter extends FragmentStatePagerAdapter {
    private Singer singer;
    public ViewPagerSingerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    public void setSinger(Singer singer) {
        this.singer = singer;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                SingerAlbumsFragment singerAlbumsFragment = new SingerAlbumsFragment();
                Bundle albumBundle = new Bundle();
                albumBundle.putParcelable("singer", singer);
                singerAlbumsFragment.setArguments(albumBundle);
                return singerAlbumsFragment;
            case 1:
                SingerSongsFragment singerSongsFragment = new SingerSongsFragment();
                Bundle songBundle = new Bundle();
                songBundle.putParcelable("singer", singer);
                singerSongsFragment.setArguments(songBundle);
                return singerSongsFragment;
            default:
                return null;
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
                Title="Albums";
                break;
            case 1:
                Title="Songs";
                break;
        }
        return Title;
    }
}
