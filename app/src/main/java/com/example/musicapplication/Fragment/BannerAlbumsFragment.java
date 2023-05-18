package com.example.musicapplication.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.musicapplication.Adapter.AlbumAdapter;
import com.example.musicapplication.Adapter.NewSongAdapter;
import com.example.musicapplication.Model.Album;
import com.example.musicapplication.Model.Song;
import com.example.musicapplication.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class BannerAlbumsFragment extends Fragment {
    View view;
    FirebaseFirestore firebaseFirestore;
    RecyclerView recyclerViewBannerAlbums;
    ArrayList<Album> albums;
    AlbumAdapter albumAdapter;
    ImageView backArrow;
    String idBannerItem;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_banner_albums, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerViewBannerAlbums = view.findViewById(R.id.recyclerViewBannerAlbums);
        backArrow = view.findViewById(R.id.backArrow);
        Bundle bundle = getArguments();
        if (bundle != null) {
            idBannerItem = bundle.getString("idBanner");
        }
        albums = new ArrayList<>();
        albumAdapter = new AlbumAdapter(albums,null,getContext());
        recyclerViewBannerAlbums.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewBannerAlbums.setAdapter(albumAdapter);
        getAlbum(idBannerItem);
        backArrow.setOnClickListener(view1 -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });
        return view;
    }

    private void getAlbum(String idBannerItem) {
        albums.clear();
        CollectionReference productRefs = firebaseFirestore.collection("Albums");
        Query query = productRefs.whereEqualTo("idBanner", idBannerItem);
        query.get().addOnSuccessListener(documentSnapshots -> {
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

                albumAdapter.notifyDataSetChanged();

            }
        }).addOnFailureListener(e -> Log.d("TAG","Error"));
    }
}