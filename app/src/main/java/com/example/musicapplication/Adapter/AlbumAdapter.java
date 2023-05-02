package com.example.musicapplication.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapplication.Model.Album;
import com.example.musicapplication.R;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder>{
    ArrayList<Album> albums;
    Context context;

    public AlbumAdapter(ArrayList<Album> albums, Context context) {
        this.albums = albums;
        this.context = context;
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
        holder.txtAlbumTitle.setText(album.getTitle());
        holder.txtSingerName.setText(album.getSinger());
        Glide.with(context)
                .load(album.getImage())
                .into(holder.imageViewAlbum);

//        holder.itemView.setOnClickListener(v -> {
//            Intent i = new Intent(context, danhsachbaihatAlbum.class);
//            i.putExtra("idAlbum",dongPlaylist.get(position).getId());
//            context.startActivity(i);
//        });

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