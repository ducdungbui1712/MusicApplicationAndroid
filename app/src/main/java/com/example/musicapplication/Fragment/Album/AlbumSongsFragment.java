package com.example.musicapplication.Fragment.Album;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicapplication.Activity.MainActivity;
import com.example.musicapplication.Adapter.NewSongAdapter;
import com.example.musicapplication.Fragment.SearchFragment;
import com.example.musicapplication.Fragment.Singer.SingerAlbumsFragment;
import com.example.musicapplication.Fragment.Singer.SingerTabFragment;
import com.example.musicapplication.Model.Album;
import com.example.musicapplication.Model.Singer;
import com.example.musicapplication.Model.Song;
import com.example.musicapplication.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class AlbumSongsFragment extends Fragment {
    View view;
    CoordinatorLayout coordinatorLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    ArrayList<Song> songs;
    FirebaseFirestore firebaseFirestore;
    NewSongAdapter adapter;
    ImageView imageView, backArrow;
    TextView txtAlbumTitle;
    Album album;
    RelativeLayout playerView;
    Singer singer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_album_songs, container, false);
        ImageView searchIcon = getActivity().findViewById(R.id.searchIcon);
        Fragment currentFragment = ((AppCompatActivity)getContext()).getSupportFragmentManager().findFragmentById(R.id.fragmentLayout);
        if (currentFragment instanceof SearchFragment) {
            searchIcon.setImageResource(R.drawable.nav_menu_search_close);
        } else {
            searchIcon.setImageResource(R.drawable.nav_menu_search);
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            album = bundle.getParcelable("album");
            singer = bundle.getParcelable("singer");

            // Sử dụng biến album để hiển thị thông tin trong Fragment
            init(view);
            floatingActionButton.setBackgroundColor(Color.BLACK);
            collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
            collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
            collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
            GetData();
        }
        return view;
    }

    private void GetData() {
        collapsingToolbarLayout.setTitle(album.getTitle().trim());
        txtAlbumTitle.setText(album.getTitle().trim());
        Glide.with(getContext())
                .load(album.getImage().trim())
                .into(imageView);

        songs.clear();
        CollectionReference songsRef = firebaseFirestore.collection("Songs");
        Query query = songsRef.whereEqualTo("idAlbum", album.getId().trim());
        query
                .get()
                .addOnSuccessListener(documentSnapshots -> {
                    if (documentSnapshots.isEmpty()) {
                        Log.d("TAG", "onSuccess: LIST EMPTY");
                    } else {
                        for (DocumentSnapshot document : documentSnapshots) {
                            String id = document.getId();
                            String duration = document.getString("duration").trim();
                            String image = document.getString("image").trim();
                            String link = document.getString("link").trim();
                            String title = document.getString("title").trim();
                            String lyric = document.getString("lyric");
                            int like = document.getLong("likes").intValue();
                            Timestamp release = document.getTimestamp("release");
                            String idAlbum = document.getString("idAlbum").trim();
                            String idSinger = document.getString("idSinger").trim();
                            String idBanner = document.getString("idBanner");
                            Song song = new Song(id, duration, image, link, title, lyric, like, release, idAlbum,idSinger, idBanner);
                            songs.add(song);
                        }
                        adapter.notifyDataSetChanged();
                        eventClick();
                    }
                }).addOnFailureListener(e -> Log.d("TAG","Error"));
    }

    private void eventClick() {
        backArrow.setOnClickListener(view1 -> {
            if(singer != null){
                SingerTabFragment singerTabFragment = new SingerTabFragment();
                Bundle albumBundle = new Bundle();
                albumBundle.putParcelable("singer", singer);
                singerTabFragment.setArguments(albumBundle);
                FragmentTransaction fragmentTransaction = ((AppCompatActivity)getContext())
                        .getSupportFragmentManager()
                        .beginTransaction();
                fragmentTransaction.replace(R.id.fragmentLayout, singerTabFragment);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }else {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


    }

    private void init(View view) {
        collapsingToolbarLayout = view.findViewById(R.id.collapsingToolbarLayout);
        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        floatingActionButton = view.findViewById(R.id.floatingButtonPlay);
        imageView = view.findViewById(R.id.imageView);
        backArrow = view.findViewById(R.id.backArrow);
        txtAlbumTitle = view.findViewById(R.id.txtAlbumTitle);
        firebaseFirestore = FirebaseFirestore.getInstance();
        songs = new ArrayList<>();
        playerView = getActivity().findViewById(R.id.playerView);
        adapter = new NewSongAdapter(getContext(),null, songs, playerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);

    }
}