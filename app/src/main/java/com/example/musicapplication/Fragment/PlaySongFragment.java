package com.example.musicapplication.Fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicapplication.Adapter.NewSongAdapter;
import com.example.musicapplication.Model.Song;
import com.example.musicapplication.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;


public class PlaySongFragment extends Fragment {
    View view;
    CircleImageView imageView;
    ArrayList<Song> arrayList;
    public ObjectAnimator objectAnimator;
    ImageView backArrow;
    TextView songTitle, albumTitle, artistName, timeDuration, timeLeft;
    ImageButton shuffle, previous, play, next, repeat;
    MediaPlayer mediaPlayer;
    SeekBar seekBar;
    Handler handler;
    ArrayList<Song> songs;
    Song song;
    FirebaseFirestore firebaseFirestore;
    boolean isRepeat = false;
    boolean isShuffle = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        song = getArguments().getParcelable("song");
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_play_song, container, false);
        init(view);
        loadData(song);
        clickEvent(song);
        return view;
    }

    private void clickEvent(Song song) {
        backArrow.setOnClickListener(view1 -> {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = (int) getResources().getDimension(R.dimen.minimized_fragment_height);
            view.setLayoutParams(params);
        });

        shuffle.setOnClickListener(view1 -> {
            isShuffle = !isShuffle;
            if (isShuffle) {
                // Set shuffle image to on
                shuffle.setImageResource(R.mipmap.shuffle_on);
            } else {
                // Set shuffle image to off
                shuffle.setImageResource(R.mipmap.shuffle);
            }
        });


        previous.setOnClickListener(view1 -> {

        });

        play.setOnClickListener(view1 -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                play.setImageResource(R.mipmap.play);
                objectAnimator.pause();
            } else {
                mediaPlayer.start();
                play.setImageResource(R.mipmap.pause);
                objectAnimator.start();
            }
        });


        next.setOnClickListener(view1 -> {

        });

         // Khởi tạo biến isRepeat ban đầu là false

        repeat.setOnClickListener(view1 -> {
            isRepeat = !isRepeat;
            if (isRepeat) {
                mediaPlayer.setLooping(true);
                repeat.setImageResource(R.mipmap.repeat_on);
            } else {
                mediaPlayer.setLooping(false);
                repeat.setImageResource(R.mipmap.repeat);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
                objectAnimator.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.start();
                objectAnimator.resume();
            }
        });
    }

    private void loadData(Song song) {
        songTitle.setText(song.getTitle());
        getAlbumAndArtistTitle(song);
        timeDuration.setText(song.getDuration());
        playSong(song);

        Glide.with(getContext())
                .load(song.getImage())
                .into(imageView);
    }

    private void getAlbumAndArtistTitle(Song song) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference albumRef = firebaseFirestore.collection("Albums").document(song.getIdAlbum().trim());
        DocumentReference artistRef = firebaseFirestore.collection("Singer").document(song.getIdSinger().trim());

        albumRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String albumName = documentSnapshot.getString("title");
                albumTitle.setText(albumName);
            }
        });

        artistRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String singerName = documentSnapshot.getString("name");
                artistName.setText(singerName);
            }
        });
    }


    private void playSong(Song song) {
        String link = song.getLink();
        if (mediaPlayer == null) {
            objectAnimator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
            objectAnimator.setDuration(10000);
            objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
            objectAnimator.setRepeatMode(ValueAnimator.RESTART);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.start();

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(mp -> {
                mediaPlayer.start();
                play.setImageResource(R.mipmap.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                updateSeekBar();
            });

            try {
                mediaPlayer.setDataSource(link);
                mediaPlayer.prepareAsync();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    private void updateSeekBar() {
        handler = new Handler();
        handler.postDelayed(() -> {
            int currentPosition = mediaPlayer.getCurrentPosition();
            seekBar.setProgress(currentPosition);
            int timeLeftInMillis = mediaPlayer.getDuration() - currentPosition;
            timeLeft.setText(String.format(Locale.getDefault(), "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(timeLeftInMillis),
                    TimeUnit.MILLISECONDS.toSeconds(timeLeftInMillis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeLeftInMillis))));
            updateSeekBar();
        }, 1000);
        onFinish();
    }
    public void onFinish() {
        mediaPlayer.setOnCompletionListener(mp -> {
            play.setImageResource(R.mipmap.play);
            objectAnimator.end();
        });
    }


    private void init(View view) {
        shuffle = view.findViewById(R.id.shuffle);
        previous = view.findViewById(R.id.previous);
        play = view.findViewById(R.id.play);
        next = view.findViewById(R.id.next);
        repeat = view.findViewById(R.id.repeat);
        songTitle = view.findViewById(R.id.songTitle);
        albumTitle = view.findViewById(R.id.albumTitle);
        artistName = view.findViewById(R.id.artistName);
        timeDuration = view.findViewById(R.id.timeDuration);
        timeLeft = view.findViewById(R.id.timeLeft);
        seekBar = view.findViewById(R.id.seekBar);
        imageView = view.findViewById(R.id.albumArt);
        backArrow =view.findViewById(R.id.backArrow);
    }
}