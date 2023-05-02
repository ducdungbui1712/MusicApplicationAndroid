package com.example.musicapplication.Fragment.Singer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicapplication.Adapter.NewSongAdapter;
import com.example.musicapplication.Fragment.PlaySongFragment;
import com.example.musicapplication.Model.Singer;
import com.example.musicapplication.Model.Song;
import com.example.musicapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SingerSongsFragment extends Fragment {
    CoordinatorLayout coordinatorLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    ArrayList<Song> songs;
    FirebaseFirestore firebaseFirestore;
    NewSongAdapter adapter;
    ImageView imageView;
    TextView textViewArtistName;
    Singer singer;
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
            floatingActionButton.setBackgroundColor(Color.BLACK);
            collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
            collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
            collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
            GetCasi();
        }
        return view;
    }

    private void GetCasi() {
        DocumentReference documentReference = firebaseFirestore.collection("Singer").document(singer.getId());
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String id = document.getId();
                    String name = document.getString("name");
                    String image = document.getString("image");
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
        firebaseFirestore = FirebaseFirestore.getInstance();
        songs = new ArrayList<>();
        adapter = new NewSongAdapter(getContext(), songs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);
        GetData();
    }

    private void GetData() {
        songs.clear();
        firebaseFirestore
                .collection("Songs")
                .whereEqualTo("idSinger",singer.getId())
                .get()
                .addOnSuccessListener(documentSnapshots -> {
                    if (documentSnapshots.isEmpty()) {
                        Log.d("TAG", "onSuccess: LIST EMPTY");
                    } else {
                        for (DocumentSnapshot document : documentSnapshots) {
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
                        adapter.notifyDataSetChanged();
                        eventClick();
                    }
                }).addOnFailureListener(e -> Log.d("TAG","Error"));
    }

    private void eventClick() {
        floatingActionButton.setEnabled(true);
        floatingActionButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("listSong", songs);
            PlaySongFragment playSongFragment = new PlaySongFragment();
            playSongFragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = ((AppCompatActivity)getContext())
                    .getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction.replace(R.id.fragmentLayout, playSongFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
    }
}