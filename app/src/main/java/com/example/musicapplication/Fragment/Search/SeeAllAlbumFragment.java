package com.example.musicapplication.Fragment.Search;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.musicapplication.Adapter.AlbumAdapter;
import com.example.musicapplication.Adapter.NewSongAdapter;
import com.example.musicapplication.Fragment.HomeFragment;
import com.example.musicapplication.Fragment.SearchFragment;
import com.example.musicapplication.Model.Album;
import com.example.musicapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SeeAllAlbumFragment extends Fragment {

    View view;
    RecyclerView listSearchAlbums;
    AlbumAdapter albumAdapter;
    ArrayList<Album> albums;
    ImageView backArrow;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_see_all_album, container, false);
        listSearchAlbums = view.findViewById(R.id.listSearchAlbums);
        backArrow = view.findViewById(R.id.backArrow);
        ImageView searchIcon = getActivity().findViewById(R.id.searchIcon);
        Fragment currentFragment = ((AppCompatActivity)getContext()).getSupportFragmentManager().findFragmentById(R.id.fragmentLayout);
        if (currentFragment instanceof SearchFragment) {
            searchIcon.setImageResource(R.drawable.nav_menu_search_close);
        } else {
            searchIcon.setImageResource(R.drawable.nav_menu_search);
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            albums = bundle.getParcelableArrayList("albums");
            albumAdapter = new AlbumAdapter(albums,null,getContext());
            listSearchAlbums.setLayoutManager(new GridLayoutManager(getContext(), 2));
            listSearchAlbums.setAdapter(albumAdapter);
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