package com.example.musicapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapplication.Fragment.PlaySongFragment;
import com.example.musicapplication.Model.Song;
import com.example.musicapplication.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class NewSongAdapter extends RecyclerView.Adapter<NewSongAdapter.ViewHolder>{
    Context context;
    ArrayList<Song> arrayList;

    public NewSongAdapter(Context context, ArrayList<Song> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public NewSongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View itemview= inflater.inflate(R.layout.fragment_new_song_item_recycler_view,parent,false);
        return new ViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull NewSongAdapter.ViewHolder holder, int position) {
        Song song = arrayList.get(position);
        Glide.with(context)
                .load(song.getImage())
                .into(holder.imageViewAlbumArt);

        // Khai báo đối tượng callback
        OnAlbumTitleCallback albumTitleCallback = albumTitle -> {
            // Hiển thị albumTitle lên TextView
            holder.album.setText(albumTitle);
        };

        OnSingerNameCallback singerNameCallback = singerName -> {
            // Hiển thị singerName lên TextView
            holder.artist.setText(singerName);
        };

        // Gọi hàm getAlbumTitle()
        getAlbumTitle(song, albumTitleCallback);
        getSingerName(song, singerNameCallback);

        holder.rankSong.setText(String.valueOf(position + 1));
        holder.songName.setText(song.getTitle());
        holder.time.setText(song.getDuration());
        holder.itemView.setOnClickListener(v -> {
            PlaySongFragment playerFragment = new PlaySongFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("song", song);
            playerFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentLayout,playerFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

    }

    private void getAlbumTitle(Song song, OnAlbumTitleCallback albumTitleCallback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lấy idAlbum tương ứng với vị trí hiện tại của item trong RecyclerView
        String idAlbum = song.getIdAlbum();

        // Truy vấn tới document tương ứng với idAlbum
        db.collection("Albums").document(idAlbum)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Nếu document tồn tại, lấy giá trị của AlbumTitle
                        String albumTitle = documentSnapshot.getString("title");

                        // Gọi hàm callback để trả về giá trị của AlbumTitle
                        albumTitleCallback.onCallback(albumTitle);
                    }
                });
    }

    // Khai báo interface OnAlbumTitleCallback
    interface OnAlbumTitleCallback {
        void onCallback(String albumTitle);
    }

    private void getSingerName(Song song, OnSingerNameCallback singerNameCallback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lấy idSinger tương ứng với vị trí hiện tại của item trong RecyclerView
        String idSinger = song.getIdSinger();

        // Truy vấn tới document tương ứng với idSinger
        db.collection("Singer").document(idSinger)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Nếu document tồn tại, lấy giá trị của singerName
                        String singerName = documentSnapshot.getString("name");

                        // Gọi hàm callback để trả về giá trị của singerName
                        singerNameCallback.onCallback(singerName);
                    }
                });
    }

    // Khai báo interface OnAlbumTitleCallback
    public interface OnSingerNameCallback {
        void onCallback(String singerName);
    }



    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankSong, songName, album, artist, time;
        ImageView imageViewAlbumArt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewAlbumArt = itemView.findViewById(R.id.imageViewAlbumArt);
            rankSong = itemView.findViewById(R.id.rankSong);
            songName = itemView.findViewById(R.id.songName);
            album = itemView.findViewById(R.id.album);
            artist = itemView.findViewById(R.id.artist);
            time = itemView.findViewById(R.id.time);
        }
    }
}
