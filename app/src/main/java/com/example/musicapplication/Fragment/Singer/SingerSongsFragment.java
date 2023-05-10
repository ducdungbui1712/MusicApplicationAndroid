package com.example.musicapplication.Fragment.Singer;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.musicapplication.Activity.MainActivity;
import com.example.musicapplication.Adapter.NewSongAdapter;
import com.example.musicapplication.Adapter.PersonalMusicAdapter;
import com.example.musicapplication.Model.MediaPlayerSingleton;
import com.example.musicapplication.Model.Singer;
import com.example.musicapplication.Model.Song;
import com.example.musicapplication.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.IOException;
import java.util.ArrayList;

public class SingerSongsFragment extends Fragment {
    CoordinatorLayout coordinatorLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    ArrayList<Song> singerSongs;
    ArrayList<Song> songs;
    FirebaseFirestore firebaseFirestore;
    NewSongAdapter adapter;
    ImageView imageView;
    TextView textViewArtistName;
    Singer singer;
    RelativeLayout playerView;
    MediaPlayer mediaPlayer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_singer_songs, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            singer = bundle.getParcelable("singer");
            // Sử dụng biến singer để hiển thị thông tin trong Fragment
            init(view);
            firebaseFirestore = FirebaseFirestore.getInstance();
            songs = new ArrayList<>();
            singerSongs = new ArrayList<>();
            mediaPlayer = MediaPlayerSingleton.getInstance().getMediaPlayer();
            floatingActionButton.setBackgroundColor(Color.BLACK);
            collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
            collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
            collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
            GetCasi();
            GetData();
            adapter = new NewSongAdapter(getContext(),null, singerSongs, playerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    private void GetCasi() {
        DocumentReference documentReference = firebaseFirestore.collection("Singer").document(singer.getId().trim());
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String id = document.getId().trim();
                    String name = document.getString("name").trim();
                    String image = document.getString("image").trim();
                    collapsingToolbarLayout.setTitle(name);
                    textViewArtistName.setText(name);
                    Glide.with(getContext())
                            .load(image)
                            .circleCrop()
                            .into(imageView);
                }
                else {
                    Log.d("TAG", "No such document");
                }
            } else
            {
                Log.d("TAG", "get failed with ", task.getException());
            }
        });
    }

    private void init(View view) {
        collapsingToolbarLayout = view.findViewById(R.id.collapsingToolbarLayout);
        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        floatingActionButton = view.findViewById(R.id.floatingButtonPlay);
        imageView = view.findViewById(R.id.imageView);
        textViewArtistName = view.findViewById(R.id.textViewArtistName);
        playerView = getActivity().findViewById(R.id.playerView);
    }

    private void GetData() {
        singerSongs.clear();
        CollectionReference songsRef = firebaseFirestore.collection("Songs");
        Query query = songsRef.whereEqualTo("idSinger", singer.getId().trim());
        query
                .get()
                .addOnSuccessListener(documentSnapshots -> {
                    if (documentSnapshots.isEmpty()) {
                        Log.d("TAG", "onSuccess: LIST EMPTY");
                    } else {
                        for (DocumentSnapshot document : documentSnapshots) {
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
                            singerSongs.add(song);
                        }

                        adapter.notifyDataSetChanged();
                        eventClick();
                    }
                }).addOnFailureListener(e -> Log.d("TAG","Error"));
    }

    private void eventClick() {
        floatingActionButton.setEnabled(true);
        floatingActionButton.setOnClickListener(v -> {
            Log.d("floatingActionButton" , "Clicked");
            if (singerSongs.size() >= 2){
                playSong(singerSongs.get(0));
                Intent intent = new Intent("sendSong");
                intent.putExtra("song", singerSongs.get(0));
                intent.putExtra("songs", singerSongs);
                getContext().sendBroadcast(intent);
                Log.d("singerSong", String.valueOf(singerSongs.size()));
                Log.d("If1", "If1");
            } else {
                Log.d("If2", "If2");
                // Query Firestore for all songs
                firebaseFirestore.collection("Songs")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
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
                                playSong(singerSongs.get(0));
                                Intent intent = new Intent("sendSong");
                                intent.putExtra("song", singerSongs.get(0));
                                intent.putExtra("songs", songs);
                                getContext().sendBroadcast(intent);
                            } else {
                                Log.d("No song found", "Empty Firestore collection");
                            }
                        })
                        .addOnFailureListener(e -> Log.d("Error getting songs", e.getMessage()));
            }
        });
    }

    private void playSong(Song song) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(song.getLink());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}