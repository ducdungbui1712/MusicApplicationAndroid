package com.example.musicapplication.Fragment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
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

import com.example.musicapplication.Adapter.NewSongAdapter;
import com.example.musicapplication.Model.Song;
import com.example.musicapplication.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class RankingSongFragment extends Fragment {
    FirebaseFirestore firebaseFirestore;
    View view;
    RecyclerView recyclerViewRankingSongs;
    ArrayList<Song> songs;
    NewSongAdapter newSongAdapter;

    RelativeLayout playerView;
    ImageView backArrow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_ranking_song, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerViewRankingSongs = view.findViewById(R.id.recyclerViewRankingSongs);
        backArrow = view.findViewById(R.id.backArrow);
        playerView = getActivity().findViewById(R.id.playerView);
        songs = new ArrayList<>();
        getSongs();
        newSongAdapter = new NewSongAdapter(getContext(),null, songs, playerView);
        recyclerViewRankingSongs.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerViewRankingSongs.setAdapter(newSongAdapter);
        backArrow.setOnClickListener(view1 -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });
        return view;
    }

    private void getSongs() {
        songs.clear();
        CollectionReference productRefs = firebaseFirestore.collection("Songs");
        productRefs.orderBy("likes", Query.Direction.DESCENDING)
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
                            String lyric = document.getString("lyric").trim();
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