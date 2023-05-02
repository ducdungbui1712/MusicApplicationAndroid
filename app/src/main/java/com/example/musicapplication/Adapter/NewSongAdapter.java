package com.example.musicapplication.Adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapplication.Fragment.PlaySongFragment;
import com.example.musicapplication.Model.Song;
import com.example.musicapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;

public class NewSongAdapter extends RecyclerView.Adapter<NewSongAdapter.ViewHolder>{
    Context context;
    ArrayList<Song> arrayList;
    MediaPlayer mediaPlayer;

    public ArrayList<Song> getArrayList() {
        return arrayList;
    }

    public NewSongAdapter(Context context, ArrayList<Song> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        this.mediaPlayer = new MediaPlayer();
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
        final Song song = arrayList.get(position);
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
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }

            // Start playing the new song
            try {
                mediaPlayer.setDataSource(song.getLink());
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Bundle bundle = new Bundle();
            bundle.putParcelable("song", song);

            PlaySongFragment playSongFragment = new PlaySongFragment();
            playSongFragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = ((AppCompatActivity)context)
                    .getSupportFragmentManager()
                    .beginTransaction();

            String fragmentTag = "PlaySongFragmentTag"; // tag for the fragment

            Fragment currentFragment = ((AppCompatActivity)context)
                    .getSupportFragmentManager()
                    .findFragmentByTag(fragmentTag); // find the current fragment by tag

            if (currentFragment != null) {
                ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
            }

            fragmentTransaction.replace(R.id.fragmentLayout, playSongFragment, fragmentTag); // add the new fragment with the tag
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
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
