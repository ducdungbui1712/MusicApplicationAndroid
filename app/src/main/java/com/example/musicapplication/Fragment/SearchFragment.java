package com.example.musicapplication.Fragment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.musicapplication.Activity.MainActivity;
import com.example.musicapplication.Adapter.AlbumAdapter;
import com.example.musicapplication.Adapter.NewSongAdapter;
import com.example.musicapplication.Fragment.Search.SeeAllAlbumFragment;
import com.example.musicapplication.Fragment.Search.SeeAllSongFragment;
import com.example.musicapplication.Model.Album;
import com.example.musicapplication.Model.Song;
import com.example.musicapplication.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {
    View view;
    EditText searchEditText;
    ImageView clearSearchImageView;
    FirebaseFirestore firebaseFirestore;
    RecyclerView listSearchSongs, listSearchAlbums;
    NewSongAdapter newSongAdapter;
    AlbumAdapter albumAdapter;
    RelativeLayout playerView, songSearch, albumSearch;
    ArrayList<Album> albums;
    ArrayList<Song> songs;
    TextView txtSeeAllSongs, txtSeeAllAlbums;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);
        ImageView searchIcon = getActivity().findViewById(R.id.searchIcon);
        Fragment currentFragment = ((AppCompatActivity)getContext()).getSupportFragmentManager().findFragmentById(R.id.fragmentLayout);
        if (currentFragment instanceof SearchFragment) {
            searchIcon.setImageResource(R.drawable.nav_menu_search_close);
        } else {
            searchIcon.setImageResource(R.drawable.nav_menu_search);
        }
        firebaseFirestore = FirebaseFirestore.getInstance();
        searchEditText = view.findViewById(R.id.searchEditText);
        clearSearchImageView = view.findViewById(R.id.clearSearchImageView);
        listSearchSongs = view.findViewById(R.id.listSearchSongs);
        listSearchAlbums = view.findViewById(R.id.listSearchAlbums);
        playerView = getActivity().findViewById(R.id.playerView);
        songSearch = view.findViewById(R.id.songSearch);
        albumSearch = view.findViewById(R.id.albumSearch);
        txtSeeAllSongs = view.findViewById(R.id.txtSeeAllSongs);
        txtSeeAllAlbums = view.findViewById(R.id.txtSeeAllAlbums);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    searchForSongs(charSequence.toString());
                    clearSearchImageView.setVisibility(View.VISIBLE);
                } else {
                    clearSearchImageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        clearSearchImageView.setOnClickListener(view -> searchEditText.setText(""));
        return view;
    }

    private void searchForSongs(String newText) {
        // Xử lý khi người dùng nhập từ khóa tìm kiếm vào đây
        if (newText.length() >= 3) {
            Log.d("newText", newText);
            // Tìm kiếm bài hát trên Firestore database
            firebaseFirestore.collection("Songs")
                    .whereGreaterThanOrEqualTo("title", newText)
                    .whereLessThanOrEqualTo("title", newText + "\uf8ff")
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            songs = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
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
                            Log.d( "onQueryTextChange: ", String.valueOf(songs));

                            if(songs.size() > 0){
                                songSearch.setVisibility(View.VISIBLE);
                                // Hiển thị danh sách các bài hát tìm kiếm được trong một ListView hoặc RecyclerView
                                if(songs.size() >= 3){
                                    List<Song> top3Songs = songs.subList(0, 3);
                                    ArrayList<Song> top3SongsList = new ArrayList<>(top3Songs);
                                    newSongAdapter = new NewSongAdapter(getContext(), top3SongsList, songs, playerView);
                                    txtSeeAllSongs.setVisibility(View.VISIBLE);
                                }else {
                                    newSongAdapter = new NewSongAdapter(getContext(),null, songs, playerView);
                                    txtSeeAllSongs.setVisibility(View.GONE);
                                }

                                listSearchSongs.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
                                listSearchSongs.setAdapter(newSongAdapter);
                            }else {
                                songs.clear();
                                if(newSongAdapter != null){
                                    newSongAdapter.notifyDataSetChanged();
                                }
                                songSearch.setVisibility(View.GONE);
                            }
                        } else {
                            Log.d( "Error getting documents: ", "Error");
                        }
                    });

            firebaseFirestore.collection("Albums")
                    .whereGreaterThanOrEqualTo("title", newText)
                    .whereLessThanOrEqualTo("title", newText + "\uf8ff")
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            albums = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                String id = document.getId().trim();
                                String image = document.getString("image").trim();
                                String singer = document.getString("singer").trim();
                                String title = document.getString("title").trim();

                                Album album = new Album(id, image, singer, title);
                                albums.add(album);

                            }
                            Log.d( "onQueryTextChange: ", String.valueOf(albums));

                            if(albums.size() > 0){
                                albumSearch.setVisibility(View.VISIBLE);
                                // Hiển thị danh sách các bài hát tìm kiếm được trong một ListView hoặc RecyclerView
                                if (albums.size() >= 3){
                                    List<Album> top3Albums = albums.subList(0, 3);
                                    ArrayList<Album> top3AlbumsList = new ArrayList<>(top3Albums);
                                    albumAdapter = new AlbumAdapter(top3AlbumsList,null,getContext());
                                    txtSeeAllAlbums.setVisibility(View.VISIBLE);
                                }else {
                                    albumAdapter = new AlbumAdapter(albums,null,getContext());
                                    txtSeeAllAlbums.setVisibility(View.GONE);
                                }
                                listSearchAlbums.setLayoutManager(new GridLayoutManager(getContext(), 2));
                                listSearchAlbums.setAdapter(albumAdapter);
                            }else {
                                albums.clear();
                                if(albumAdapter != null) {
                                    albumAdapter.notifyDataSetChanged();
                                }
                                albumSearch.setVisibility(View.GONE);
                            }
                        } else {
                            Log.d( "Error getting documents: ", "Error");
                        }
                    });
        }

        txtSeeAllSongs.setOnClickListener(view1 -> {
            SeeAllSongFragment seeAllSongFragment = new SeeAllSongFragment();
            Bundle songsBundle = new Bundle();
            songsBundle.putParcelableArrayList("songs", songs);
            seeAllSongFragment.setArguments(songsBundle);
            FragmentTransaction fragmentTransaction = ((AppCompatActivity)getContext()).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentLayout,seeAllSongFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        });

        txtSeeAllAlbums.setOnClickListener(view1 -> {
            SeeAllAlbumFragment seeAllAlbumFragment = new SeeAllAlbumFragment();
            Bundle albumsBundle = new Bundle();
            albumsBundle.putParcelableArrayList("albums", albums);
            seeAllAlbumFragment.setArguments(albumsBundle);
            FragmentTransaction fragmentTransaction = ((AppCompatActivity)getContext()).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentLayout,seeAllAlbumFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        });
    }
}