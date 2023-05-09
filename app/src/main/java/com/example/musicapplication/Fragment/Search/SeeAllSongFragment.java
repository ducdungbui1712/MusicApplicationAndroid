package com.example.musicapplication.Fragment.Search;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.musicapplication.Adapter.NewSongAdapter;
import com.example.musicapplication.Fragment.HomeFragment;
import com.example.musicapplication.Fragment.SearchFragment;
import com.example.musicapplication.Model.Song;
import com.example.musicapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SeeAllSongFragment extends Fragment {
    View view;
    RecyclerView recyclerViewSearchSongs;
    ArrayList<Song> songs;
    NewSongAdapter newSongAdapter;

    RelativeLayout playerView;
    ImageView backArrow;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_see_all_song, container, false);
        recyclerViewSearchSongs = view.findViewById(R.id.recyclerViewSearchSongs);
        backArrow = view.findViewById(R.id.backArrow);
        playerView = getActivity().findViewById(R.id.playerView);
        ImageView searchIcon = getActivity().findViewById(R.id.searchIcon);
        Fragment currentFragment = ((AppCompatActivity)getContext()).getSupportFragmentManager().findFragmentById(R.id.fragmentLayout);
        if (currentFragment instanceof SearchFragment) {
            searchIcon.setImageResource(R.drawable.nav_menu_search_close);
        } else {
            searchIcon.setImageResource(R.drawable.nav_menu_search);
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            songs = bundle.getParcelableArrayList("songs");
            newSongAdapter = new NewSongAdapter(getContext(),null, songs, playerView);
            recyclerViewSearchSongs.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
            recyclerViewSearchSongs.setAdapter(newSongAdapter);
        }
        backArrow.setOnClickListener(view1 -> {
            HomeFragment homeFragment = new HomeFragment();
            FragmentTransaction fragmentTransaction = ((AppCompatActivity)getContext()).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentLayout,homeFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        });
        return view;
    }
}