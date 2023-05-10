package com.example.musicapplication.Fragment;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.musicapplication.Adapter.NewSongAdapter;
import com.example.musicapplication.Adapter.PersonalMusicAdapter;
import com.example.musicapplication.Model.Song;
import com.example.musicapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;


public class NewSongFragment extends Fragment {
    FirebaseFirestore firebaseFirestore;
    View view;
    RecyclerView recyclerViewNewSongs;
    ArrayList<Song> songs;
    NewSongAdapter newSongAdapter;

    RelativeLayout playerView;
    Button btnPlayAll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_song, container, false);
        ImageView searchIcon = getActivity().findViewById(R.id.searchIcon);
        Fragment currentFragment = ((AppCompatActivity)getContext()).getSupportFragmentManager().findFragmentById(R.id.fragmentLayout);
        if (currentFragment instanceof SearchFragment) {
            searchIcon.setImageResource(R.drawable.nav_menu_search_close);
        } else {
            searchIcon.setImageResource(R.drawable.nav_menu_search);
        }
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerViewNewSongs = view.findViewById(R.id.recyclerViewNewSongs);
        btnPlayAll = view.findViewById(R.id.btnPlayAll);
        playerView = getActivity().findViewById(R.id.playerView);
        songs = new ArrayList<>();
        getSongs();
        eventClick();
        newSongAdapter = new NewSongAdapter(getContext(),null, songs, playerView);
        recyclerViewNewSongs.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerViewNewSongs.setAdapter(newSongAdapter);

        return view;
    }

    private void eventClick() {
        btnPlayAll.setOnClickListener(view1 -> {
            playSong(songs.get(0));

            Intent intent = new Intent("sendSong");
            intent.putExtra("song", songs.get(0));
            intent.putExtra("songs", songs);
            intent.putExtra("isPersonalAdapter", false);
            getContext().sendBroadcast(intent);
        });
    }

    private void playSong(Song firstSong) {
        if (PersonalMusicAdapter.personalSongPlayer != null && PersonalMusicAdapter.personalSongPlayer.isPlaying()) {
            PersonalMusicAdapter.personalSongPlayer.stop();
            PersonalMusicAdapter.personalSongPlayer.release();
            PersonalMusicAdapter.personalSongPlayer = null;
        }

        if (NewSongAdapter.newSongPlayer != null && NewSongAdapter.newSongPlayer.isPlaying()) {
            NewSongAdapter.newSongPlayer.stop();
            NewSongAdapter.newSongPlayer.release();
            NewSongAdapter.newSongPlayer = null;
        }

        // Start playing the new song
        NewSongAdapter.newSongPlayer = new MediaPlayer();
        try {
            NewSongAdapter.newSongPlayer.setDataSource(firstSong.getLink().trim());
            NewSongAdapter.newSongPlayer.prepare();
            NewSongAdapter.newSongPlayer.start();
            Animation slide_up = AnimationUtils.loadAnimation(getContext(),
                    R.anim.slide_up);
            playerView.setVisibility(View.VISIBLE);
            playerView.startAnimation(slide_up);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getSongs() {
        songs.clear();
        CollectionReference productRefs = firebaseFirestore.collection("Songs");
        productRefs.orderBy("likes", Query.Direction.DESCENDING)
                .orderBy("release", Query.Direction.DESCENDING)
                .limit(100)
                .get()
                .addOnSuccessListener(documentSnapshots -> {
            if (documentSnapshots.isEmpty()) {
                Log.d("TAG", "onSuccess: LIST EMPTY");
            } else {
                for (DocumentSnapshot document : documentSnapshots) {
                    // Lấy dữ liệu từ document
                    String id = document.getId().trim();
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
                    Song song = new Song(id, duration, image, link, title, lyric, like, release, idAlbum, idSinger, idBanner);
                    songs.add(song);
                }
                newSongAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> Log.d("TAG","Error"));
    }
}