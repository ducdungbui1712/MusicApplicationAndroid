package com.example.musicapplication.Adapter;

import android.annotation.SuppressLint;
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
import com.example.musicapplication.Fragment.Album.AlbumSongsFragment;
import com.example.musicapplication.Fragment.Singer.SingerTabFragment;
import com.example.musicapplication.Model.Album;
import com.example.musicapplication.Model.Singer;
import com.example.musicapplication.R;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder>{
    ArrayList<Album> albums;
    Context context;

    Singer singer;

    public AlbumAdapter(ArrayList<Album> albums, Singer singer, Context context) {
        this.albums = albums;
        this.context = context;
        this.singer = singer;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.album_item,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final Album album = albums.get(position);
        holder.txtAlbumTitle.setText(album.getTitle().trim());
        holder.txtSingerName.setText(album.getSinger().trim());
        Glide.with(context)
                .load(album.getImage().trim())
                .into(holder.imageViewAlbum);

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("album", album);
            if(singer != null){
                bundle.putParcelable("singer", singer);
            }

            AlbumSongsFragment albumSongsFragment = new AlbumSongsFragment();
            albumSongsFragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = ((AppCompatActivity)context)
                    .getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction.replace(R.id.fragmentLayout, albumSongsFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageViewAlbum;
        TextView txtAlbumTitle,txtSingerName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewAlbum = itemView.findViewById(R.id.imageViewAlbum);
            txtAlbumTitle = itemView.findViewById(R.id.txtAlbumTitle);
            txtSingerName = itemView.findViewById(R.id.txtSingerName);
        }
    }

}