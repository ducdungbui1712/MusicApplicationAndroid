package com.example.musicapplication.Fragment.Home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.musicapplication.R;

import java.util.ArrayList;
import java.util.List;

public class HomeBannerFragment extends Fragment {
    ImageSlider imageSlider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_banner, container, false);
        imageSlider = view.findViewById(R.id.imageSlider);
        List<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel("https://photo-zmp3.zmdcdn.me/banner/d/1/8/7/d187f7c347f245df7a694cae5d1477d2.jpg", ScaleTypes.FIT));
        imageList.add(new SlideModel("https://photo-zmp3.zmdcdn.me/banner/1/0/6/2/1062d83af2ff5c5fbbba88331219ebce.jpg", ScaleTypes.FIT));
        imageSlider.setImageList(imageList,ScaleTypes.FIT);
        return view;
    }
}