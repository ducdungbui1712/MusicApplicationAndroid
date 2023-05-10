package com.example.musicapplication.Fragment;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

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
import com.example.musicapplication.Model.MediaPlayerSingleton;
import com.example.musicapplication.Model.Song;
import com.example.musicapplication.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;

public class BannerSongsFragment extends Fragment {
    FirebaseFirestore firebaseFirestore;
    View view;
    RecyclerView recyclerViewBannerSongs;
    ArrayList<Song> songs;
    NewSongAdapter newSongAdapter;
    Button btnPlayAll;
    RelativeLayout playerView;
    ImageView backArrow;
    String idBannerItem;
    MediaPlayer mediaPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_banner_songs, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerViewBannerSongs = view.findViewById(R.id.recyclerViewBannerSongs);
        backArrow = view.findViewById(R.id.backArrow);
        btnPlayAll = view.findViewById(R.id.btnPlayAll);
        mediaPlayer = MediaPlayerSingleton.getInstance().getMediaPlayer();
        playerView = getActivity().findViewById(R.id.playerView);
        Bundle bundle = getArguments();
        if (bundle != null) {
            idBannerItem = bundle.getString("idBanner");
        }
        songs = new ArrayList<>();
        getSongs(idBannerItem);
        eventClick();
        newSongAdapter = new NewSongAdapter(getContext(),null, songs, playerView);
        recyclerViewBannerSongs.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerViewBannerSongs.setAdapter(newSongAdapter);

        return view;
    }

    private void eventClick() {
        backArrow.setOnClickListener(view1 -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });
        btnPlayAll.setOnClickListener(view1 -> {
            playSong(songs.get(0));

            Intent intent = new Intent("sendSong");
            intent.putExtra("song", songs.get(0));
            intent.putExtra("songs", songs);
            getContext().sendBroadcast(intent);
        });
    }

    private void playSong(Song firstSong) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(firstSong.getLink());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getSongs(String idBannerItem) {
        firebaseFirestore.collection("Songs")
                .whereEqualTo("idBanner", idBannerItem.trim())
                .orderBy("likes", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Lấy dữ liệu từ các trường của tài liệu hiện tại và add vào danh sách songs
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
                            Song song = new Song(id, duration, image, link, title, lyric, like, release, idAlbum,idSinger, idBanner);
                            songs.add(song);
                        }
                        newSongAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }
}