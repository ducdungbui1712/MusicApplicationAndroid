package com.example.musicapplication.Model;

import android.media.MediaPlayer;

public class MediaPlayerSingleton {

    private static MediaPlayerSingleton instance;
    private MediaPlayer mediaPlayer;

    private MediaPlayerSingleton() {
        mediaPlayer = new MediaPlayer();
    }

    public static MediaPlayerSingleton getInstance() {
        if (instance == null) {
            instance = new MediaPlayerSingleton();
        }
        return instance;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}
