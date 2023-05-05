package com.example.musicapplication.Fragment.Singer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.musicapplication.Adapter.AlbumAdapter;
import com.example.musicapplication.Model.Album;
import com.example.musicapplication.Model.Singer;
import com.example.musicapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SingerAlbumsFragment extends Fragment {
    private Singer singer;
    RecyclerView recyclerView;
    AlbumAdapter adapter;
    ArrayList<Album> albums;
    FirebaseFirestore firebaseFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_singer_albums, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            singer = bundle.getParcelable("singer");
            init(view);
            getAlbum();
        }

        return view;
    }


    private void getAlbum() {
        List<String> albumIds = singer.getIdAlbum();
        firebaseFirestore.collection("Albums")
                .whereIn("id", albumIds)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            String id = documentSnapshot.getId().trim();
                            String image = documentSnapshot.getString("image").trim();
                            String singer = documentSnapshot.getString("singer").trim();
                            String title = documentSnapshot.getString("title").trim();
                            Album album = new Album(id, image, singer, title);
                            albums.add(album);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("No albums found for singer with id: ", singer.getId().trim());
                    }
                })
                .addOnFailureListener(e -> Log.d("Error getting albums for singer with id: "+ singer.getId().trim(), e.getMessage()));
    }


    private void init(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        firebaseFirestore = FirebaseFirestore.getInstance();
        albums = new ArrayList<>();
        adapter = new AlbumAdapter(albums,singer,getContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
    }
}