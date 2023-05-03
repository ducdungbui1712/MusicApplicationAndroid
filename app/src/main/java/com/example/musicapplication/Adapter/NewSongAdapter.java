package com.example.musicapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapplication.Model.Song;
import com.example.musicapplication.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;

public class NewSongAdapter extends RecyclerView.Adapter<NewSongAdapter.ViewHolder>{
    Context context;
    ArrayList<Song> songs;
    static public MediaPlayer mediaPlayer;
    RelativeLayout playerView;


    public ArrayList<Song> getArrayList() {
        return songs;
    }

    public NewSongAdapter(Context context, ArrayList<Song> songs, RelativeLayout playerView) {
        this.context = context;
        this.songs = songs;
        this.playerView = playerView;
    }

    @NonNull
    @Override
    public NewSongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View itemView= inflater.inflate(R.layout.fragment_new_song_item_recycler_view,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NewSongAdapter.ViewHolder holder, int position) {
        final Song song = songs.get(position);
        holder.rankSong.setText(String.valueOf(position + 1));
        holder.songName.setText(song.getTitle());
        holder.time.setText(song.getDuration());

        Glide.with(context)
                .load(song.getImage())
                .into(holder.imageViewAlbumArt);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Albums").document(song.getIdAlbum()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String albumName = document.getString("title");
                    holder.album.setText(albumName);
                }
            }
        });

        db.collection("Singer").document(song.getIdSinger()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String singerName = document.getString("name");
                    holder.artist.setText(singerName);
                }
            }
        });

        holder.itemView.setOnClickListener(v -> {
            playSong(song);
            // Tạo một Intent với dữ liệu cần truyền đi
            Intent intent = new Intent("sendSong");
            intent.putExtra("song", song);
            context.sendBroadcast(intent);
        });
    }

    public void playSong(Song song) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        // Start playing the new song
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(song.getLink());
            mediaPlayer.prepare();
            mediaPlayer.start();
            Animation slide_up = AnimationUtils.loadAnimation(context,
                    R.anim.slide_up);
            playerView.setVisibility(View.VISIBLE);
            playerView.startAnimation(slide_up);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return songs.size();
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
