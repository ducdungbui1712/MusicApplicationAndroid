package com.example.musicapplication.Fragment.Home;

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
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.musicapplication.Adapter.NewSongAdapter;
import com.example.musicapplication.Fragment.RankingSongFragment;
import com.example.musicapplication.Model.Song;
import com.example.musicapplication.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class HomeRankingFragment extends Fragment {
    View view;
    FirebaseFirestore firebaseFirestore;
    RecyclerView listRankingSongs;
    ArrayList<Song> songs;
    NewSongAdapter newSongAdapter;
    RelativeLayout playerView;
    Button btnSeeAll;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_ranking, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        listRankingSongs = view.findViewById(R.id.listRankingSongs);
        btnSeeAll = view.findViewById(R.id.btnSeeAll);
        playerView = getActivity().findViewById(R.id.playerView);
        songs = new ArrayList<>();
        getSongs();
        btnSeeAll.setOnClickListener(view1 -> {
            RankingSongFragment rankingSongFragment = new RankingSongFragment();
            FragmentTransaction fragmentTransaction = ((AppCompatActivity)getContext()).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentLayout,rankingSongFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        });
        return view;
    }

    private void getSongs() {
        songs.clear();
        CollectionReference productRefs = firebaseFirestore.collection("Songs");
        productRefs.orderBy("likes", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(documentSnapshots -> {
                    if (documentSnapshots.isEmpty()) {
                        Log.d("TAG", "onSuccess: LIST EMPTY");
                    } else {
                        for (DocumentSnapshot document : documentSnapshots) {
                            // Lấy dữ liệu từ document
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

                            Song song = new Song(id, duration, image, link, title, lyric, like, release, idAlbum, idSinger);
                            songs.add(song);
                        }
                        List<Song> top5Songs = songs.subList(0, 5);
                        ArrayList<Song> top5SongsList = new ArrayList<>(top5Songs);
                        newSongAdapter = new NewSongAdapter(getContext(), top5SongsList, songs, playerView);
                        listRankingSongs.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
                        listRankingSongs.setAdapter(newSongAdapter);
                        newSongAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(e -> Log.d("TAG","Error"));
    }
}

