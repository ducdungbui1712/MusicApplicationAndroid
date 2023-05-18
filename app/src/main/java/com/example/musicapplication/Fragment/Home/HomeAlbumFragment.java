package com.example.musicapplication.Fragment.Home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapplication.Adapter.AlbumAdapter;
import com.example.musicapplication.Model.Album;
import com.example.musicapplication.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeAlbumFragment extends Fragment {
    View view;
    RecyclerView listAlbum;
    AlbumAdapter adapter;
    ArrayList<Album> albums;
    FirebaseFirestore firebaseFirestore;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_album, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        listAlbum = view.findViewById(R.id.listAlbum);
        albums = new ArrayList<>();
        getAlbum();
        return view;
    }

    private void getAlbum() {
        albums.clear();
        CollectionReference productRefs = firebaseFirestore.collection("Albums");
        productRefs.get().addOnSuccessListener(documentSnapshots -> {
            if (documentSnapshots.isEmpty()) {
                Log.d("TAG", "onSuccess: LIST EMPTY");
            } else {
                for (DocumentSnapshot document : documentSnapshots) {
                    String id = document.getId().trim();
                    String idBanner = document.getString("idBanner");
                    String image = document.getString("image").trim();
                    String singer = document.getString("singer").trim();
                    String title = document.getString("title").trim();

                    Album album = new Album(id, idBanner, image, singer, title);
                    albums.add(album);
                }

                if(albums.size() >= 4) {
                    Collections.shuffle(albums);
                    List<Album> randomAlbums = albums.subList(0, 4);
                    ArrayList<Album> randomAlbumsList = new ArrayList<>(randomAlbums);
                    adapter = new AlbumAdapter(randomAlbumsList,null,getContext());
                }else {
                    adapter = new AlbumAdapter(albums,null,getContext());
                }
                listAlbum.setLayoutManager(new GridLayoutManager(getContext(), 2));
                listAlbum.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> Log.d("TAG","Error"));
    }
}