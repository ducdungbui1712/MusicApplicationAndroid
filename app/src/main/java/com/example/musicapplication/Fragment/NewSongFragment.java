package com.example.musicapplication.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapplication.Adapter.NewSongAdapter;
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

import java.util.ArrayList;


public class NewSongFragment extends Fragment {
    FirebaseFirestore firebaseFirestore;
    View view;
    RecyclerView recyclerViewNewSongs;
    ArrayList<Song> songs;
    NewSongAdapter newSongAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_song, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerViewNewSongs = view.findViewById(R.id.recyclerViewNewSongs);
        songs = new ArrayList<>();
        newSongAdapter = new NewSongAdapter(getContext(), songs);
        getSongs();
        recyclerViewNewSongs.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerViewNewSongs.setAdapter(newSongAdapter);

        return view;
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
                    String id = document.getId();
                    String duration = document.getString("duration");
                    String image = document.getString("image");
                    String link = document.getString("link");
                    String title = document.getString("title");
                    String lyric = document.getString("lyric");
                    int like = document.getLong("likes").intValue();
                    Timestamp release = document.getTimestamp("release");
                    String idGenre =document.getString("idGenre");
                    String idAlbum = document.getString("idAlbum");
                    String idSinger = document.getString("idSinger");
                    String idTopic = document.getString("idTopic");

                    Song song = new Song(id, duration, image, link, title, lyric, like, release, idGenre, idAlbum,idSinger, idTopic);
                    songs.add(song);
                }
                newSongAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> Log.d("TAG","Error"));

    }

}