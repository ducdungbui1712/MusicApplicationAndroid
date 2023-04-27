package com.example.musicapplication.Fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;

import com.example.musicapplication.Model.Song;
import com.example.musicapplication.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class PlaySongFragment extends Fragment {
    View view;
    CircleImageView imageView;
    public ObjectAnimator objectAnimator;

    ImageButton shuffle, previous, play, next, repeat;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Song song = getArguments().getParcelable("song");

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_play_song, container, false);
        imageView = view.findViewById(R.id.albumArt);
        objectAnimator = ObjectAnimator.ofFloat(imageView,"rotation",0f,360f);
        objectAnimator.setDuration(10000);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();
        return view;
    }

}