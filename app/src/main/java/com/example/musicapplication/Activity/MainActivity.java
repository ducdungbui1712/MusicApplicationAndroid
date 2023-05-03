package com.example.musicapplication.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;



import com.bumptech.glide.Glide;
import com.example.musicapplication.Adapter.NewSongAdapter;
import com.example.musicapplication.Fragment.AlbumsFragment;
import com.example.musicapplication.Fragment.SingerFragment;
import com.example.musicapplication.Fragment.Home.HomeTabFragment;
import com.example.musicapplication.Fragment.NewSongFragment;
import com.example.musicapplication.Fragment.PersonalMusicFragment;
import com.example.musicapplication.Fragment.ProfileFragment;
import com.example.musicapplication.Fragment.TopicFragment;
import com.example.musicapplication.Model.Song;
import com.example.musicapplication.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    Toolbar toolbar;
    ImageView toolbarLogo, imageViewEdit;
    CircleImageView imageViewUserAva;
    TextView txtUserName, txtUserMail;
    ObjectAnimator objectAnimator;

    //player_view
    RelativeLayout playerView;
    CircleImageView songImage;
    ImageView backArrow;
    TextView songTitle, albumTitle, artistName, timeDuration, timeLeft;
    ImageButton shuffle, previous, play, next, repeat;
    SeekBar seekBar;
    Handler handler;

    //miniPlayer
    RelativeLayout miniPlayer;
    CircleImageView songImageMiniPlayer;
    TextView songTitleMiniPlayer;
    SeekBar seekBarMiniPlayer;
    ImageButton previousMiniPlayer, playMiniPlayer, nextMiniPlayer;
    Animation slide_up, slide_down, song_name_slide;
    private ArrayList<Song> songs = new ArrayList<>();
    private ArrayList<Song> originalSongs = new ArrayList<>();
    Song song;
    boolean isRepeat = false;
    boolean isShuffle = false;
    private int currentSongIndex = 0;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("sendSong")) {
                if(intent != null){
                    song = intent.getParcelableExtra("song");
                    loadData(song);
                }
            }

            if (intent.getAction().equals("sendListSongs")) {
                if(intent != null){
//                    songs.clear();
//                    originalSongs.clear();
//                    ArrayList<Song> ListSongs = intent.getParcelableArrayListExtra("sendListSongs");
//                    songs.addAll(ListSongs);
//                    originalSongs.addAll(songs);
                }
            }else {
                songs.clear();
                originalSongs.clear();
                CollectionReference productRefs = firebaseFirestore.collection("Songs");
                productRefs.orderBy("likes", Query.Direction.DESCENDING)
                        .orderBy("release", Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener(documentSnapshots -> {
                            if (documentSnapshots.isEmpty()) {
                                Log.d("TAG", "onSuccess: LIST EMPTY");
                            } else {
                                for (DocumentSnapshot document : documentSnapshots) {
                                    // Lấy dữ liệu từ document
                                    String id = document.getId();
                                    String duration = document.getString("duration");
                                    String image = document.getString("image");
                                    String link = document.getString("link");
                                    String title = document.getString("title");
                                    String lyric = document.getString("lyric");
                                    int like = document.getLong("likes").intValue();
                                    Timestamp release = document.getTimestamp("release");
                                    String idGenre =document.getString("idGenre");
                                    String idAlbum = document.getString("idAlbum");
                                    String idSinger = document.getString("idSinger");
                                    String idTopic = document.getString("idTopic");

                                    Song song = new Song(id, duration, image, link, title, lyric, like, release, idGenre, idAlbum,idSinger, idTopic);
                                    songs.add(song);
                                }
                            }
                            originalSongs.addAll(songs);
                        }).addOnFailureListener(e -> Log.d("TAG","Error"));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        setToolbar();
        setNavMenu();

        //Edit profile click event
        imageViewEdit.setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new ProfileFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new HomeTabFragment()).commit();
            navigationView.setCheckedItem(R.id.navHome);
        }
        // Register the broadcast receiver
        IntentFilter filter = new IntentFilter("sendSong");
        registerReceiver(broadcastReceiver, filter);
        playerViewControl();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the broadcast receiver to avoid memory leaks
        unregisterReceiver(broadcastReceiver);

        //release the player
        if(NewSongAdapter.mediaPlayer.isPlaying()){
            NewSongAdapter.mediaPlayer.stop();
        }
        NewSongAdapter.mediaPlayer.release();

    }

    public void loadData(Song song) {
        //playerView
        songTitle.setText(song.getTitle());
        getAlbumAndArtistTitle(song);
        timeDuration.setText(song.getDuration());
        Glide.with(getApplicationContext())
                .load(song.getImage())
                .into(songImage);

        setAnimationSongImage(songImage);

        //miniPlayer
        songTitleMiniPlayer.setText(song.getTitle());
        Glide.with(getApplicationContext())
                .load(song.getImage())
                .into(songImageMiniPlayer);
        setAnimationSongImage(songImageMiniPlayer);
        seekBar.setMax(NewSongAdapter.mediaPlayer.getDuration());
        seekBarMiniPlayer.setMax(NewSongAdapter.mediaPlayer.getDuration());
        updateSeekBar();
    }

    private void updateSeekBar() {
        handler = new Handler();
        handler.postDelayed(() -> {
            if (NewSongAdapter.mediaPlayer != null) { // Check if mediaPlayer is not null
                int currentPosition = NewSongAdapter.mediaPlayer.getCurrentPosition();
                seekBar.setProgress(currentPosition);
                seekBarMiniPlayer.setProgress(currentPosition);
                int timeLeftInMillis = NewSongAdapter.mediaPlayer.getDuration() - currentPosition;
                timeLeft.setText(String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(timeLeftInMillis),
                        TimeUnit.MILLISECONDS.toSeconds(timeLeftInMillis) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeLeftInMillis))));
            }
            updateSeekBar();
        }, 1000);
        onFinish();
    }
    public void onFinish() {
        if (NewSongAdapter.mediaPlayer != null) { // Check if mediaPlayer is not null
            NewSongAdapter.mediaPlayer.setOnCompletionListener(mp -> {
                play.setImageResource(R.mipmap.play);
                objectAnimator.end();
                song = getNextSong(song);
                currentSongIndex = songs.indexOf(song);
                playSong(song);
            });
        }
    }


    private void playSong(Song song) {
        if (song != null) {
            try {
                NewSongAdapter.mediaPlayer.reset();
                NewSongAdapter.mediaPlayer.setDataSource(song.getLink());
                NewSongAdapter.mediaPlayer.prepare();
                NewSongAdapter.mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        loadData(song);
    }

    private void setAnimationSongImage(CircleImageView songImage) {

        objectAnimator = ObjectAnimator.ofFloat(songImage, "rotation", 0f, 360f);
        objectAnimator.setDuration(10000);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);

        if (NewSongAdapter.mediaPlayer.isPlaying()) {
            play.setImageResource(R.mipmap.pause);
            playMiniPlayer.setImageResource(R.mipmap.pause);
            objectAnimator.start();
        } else {
            play.setImageResource(R.mipmap.play);
            playMiniPlayer.setImageResource(R.mipmap.play);
            objectAnimator.end();
        }
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


    private void init(){
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbarLogo = findViewById(R.id.toolbarLogo);


        //player_view
        playerView = findViewById(R.id.playerView);
        songImage = findViewById(R.id.songImage);
        backArrow = findViewById(R.id.backArrow);
        songTitle = findViewById(R.id.songTitle);
        albumTitle = findViewById(R.id.albumTitle);
        artistName = findViewById(R.id.artistName);
        timeDuration = findViewById(R.id.timeDuration);
        timeLeft = findViewById(R.id.timeLeft);
        shuffle = findViewById(R.id.shuffle);
        previous = findViewById(R.id.previous);
        play = findViewById(R.id.play);
        next = findViewById(R.id.next);
        repeat = findViewById(R.id.repeat);
        seekBar = findViewById(R.id.seekBar);

        //miniPlayer
        miniPlayer = findViewById(R.id.miniPlayer);
        songImageMiniPlayer = findViewById(R.id.songImageMiniPlayer);
        songTitleMiniPlayer = findViewById(R.id.songTitleMiniPlayer);
        seekBarMiniPlayer = findViewById(R.id.seekBarMiniPlayer);
        previousMiniPlayer = findViewById(R.id.previousMiniPlayer);
        playMiniPlayer = findViewById(R.id.playMiniPlayer);
        nextMiniPlayer = findViewById(R.id.nextMiniPlayer);

    }


    private void setToolbar() {
        setSupportActionBar(toolbar);
        setTitle("");
        toolbarLogo.setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new HomeTabFragment()).commit();
            navigationView.setCheckedItem(R.id.navHome);
        });
    }
    private void setNavMenu() {
        View headerView = navigationView.getHeaderView(0);
        imageViewEdit = headerView.findViewById(R.id.imageViewEdit);
        imageViewUserAva = headerView.findViewById(R.id.imageViewUserAva);
        txtUserName = headerView.findViewById(R.id.txtUserName);
        txtUserMail = headerView.findViewById(R.id.txtUserMail);
        getUser();
    }

    private void playerViewControl() {
        backArrow.setOnClickListener(view -> exitPlayerView());
        miniPlayer.setOnClickListener(view -> showPlayerView());
        miniPlayer.setOnTouchListener(new View.OnTouchListener() {
            private float startY;
            private int dragThreshold = 10;
            private int miniPlayerHeight;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startY = event.getRawY();
                        miniPlayerHeight = miniPlayer.getHeight(); // Initialize miniPlayerHeight here
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float dy = event.getRawY() - startY;
                        if (dy > dragThreshold) {
                            // User is dragging down
                            miniPlayer.setTranslationY(dy);
                            return true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // User stopped dragging
                        float currentY = miniPlayer.getTranslationY();
                        if (currentY > miniPlayerHeight / 2) {
                            // User dragged down more than half of the Mini player height, so close it
                            miniPlayer.animate().translationY(miniPlayerHeight).start();
                            miniPlayer.setVisibility(View.GONE);
                            if (NewSongAdapter.mediaPlayer != null) {
                                NewSongAdapter.mediaPlayer.stop();
                                NewSongAdapter.mediaPlayer.release();
                                NewSongAdapter.mediaPlayer = null;
                            }
                        } else {
                            // User did not drag down enough, so restore the Mini player to its original position
                            miniPlayer.animate().translationY(0).start();
                        }
                        return true;
                }
                return false;
            }
        });

        shuffle.setOnClickListener(view1 -> {
            isShuffle = !isShuffle;
            if (isShuffle) {
                // Shuffle mode is on
                shuffle.setImageResource(R.mipmap.shuffle_on);
                // Shuffle the songs list
                Collections.shuffle(songs);
            } else {
                // Shuffle mode is off
                shuffle.setImageResource(R.mipmap.shuffle);
                // Reset the songs list to the original order
                songs.clear();
                songs.addAll(originalSongs);
            }
        });

        previous.setOnClickListener(view1 -> {
            song = getPreviousSong(song);
            currentSongIndex = songs.indexOf(song);
            playSong(song);
        });

        previousMiniPlayer.setOnClickListener(view1 -> {
            song = getPreviousSong(song);
            currentSongIndex = songs.indexOf(song);
            playSong(song);
        });

        play.setOnClickListener(view1 -> {
            if (NewSongAdapter.mediaPlayer.isPlaying()) {
                NewSongAdapter.mediaPlayer.pause();
                setAnimationSongImage(songImage); // dừng animation
            } else {
                NewSongAdapter.mediaPlayer.start();
                setAnimationSongImage(songImage); // bắt đầu animation
            }
        });

        playMiniPlayer.setOnClickListener(view1 -> {
            if (NewSongAdapter.mediaPlayer.isPlaying()) {
                NewSongAdapter.mediaPlayer.pause();
                setAnimationSongImage(songImage); // dừng animation
            } else {
                NewSongAdapter.mediaPlayer.start();
                setAnimationSongImage(songImage); // bắt đầu animation
            }
        });

        next.setOnClickListener(view1 -> {
            song = getNextSong(song);
            currentSongIndex = songs.indexOf(song);
            playSong(song);
        });

        nextMiniPlayer.setOnClickListener(view1 -> {
            song = getNextSong(song);
            currentSongIndex = songs.indexOf(song);
            playSong(song);
        });

        repeat.setOnClickListener(view1 -> {
            isRepeat = !isRepeat;
            if (isRepeat) {
                NewSongAdapter.mediaPlayer.setLooping(true);
                repeat.setImageResource(R.mipmap.repeat_on);
            } else {
                NewSongAdapter.mediaPlayer.setLooping(false);
                repeat.setImageResource(R.mipmap.repeat);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    NewSongAdapter.mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                NewSongAdapter.mediaPlayer.pause();
                play.setImageResource(R.mipmap.play);
                objectAnimator.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                NewSongAdapter.mediaPlayer.start();
                play.setImageResource(R.mipmap.pause);
                objectAnimator.resume();
            }
        });

        seekBarMiniPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    NewSongAdapter.mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                NewSongAdapter.mediaPlayer.pause();
                playMiniPlayer.setImageResource(R.mipmap.play);
                objectAnimator.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                NewSongAdapter.mediaPlayer.start();
                playMiniPlayer.setImageResource(R.mipmap.pause);
                objectAnimator.resume();
            }
        });
    }

    private Song getPreviousSong(Song currentSong) {
        Song previousSong;
        if (isShuffle) {
            int index = new Random().nextInt(songs.size());
            previousSong = songs.get(index);
        } else {
            Song songToFind = null;
            for (Song s : originalSongs) {
                if (s.getId().equals(currentSong.getId())) {
                    songToFind = s;
                    break;
                }
            }
            int currentIndex = originalSongs.indexOf(songToFind);
            int previousIndex = (currentIndex - 1 + originalSongs.size()) % originalSongs.size();
            previousSong = originalSongs.get(previousIndex);
        }
        return previousSong;
    }

    private Song getNextSong(Song currentSong) {
        Song nextSong;
        if (isShuffle) {
            int index = new Random().nextInt(songs.size());
            nextSong = songs.get(index);
        } else {
            Song songToFind = null;
            for (Song s : originalSongs) {
                if (s.getId().equals(currentSong.getId())) {
                    songToFind = s;
                    break;
                }
            }

            int currentIndex = originalSongs.indexOf(songToFind);
            int nextIndex = (currentIndex + 1) % originalSongs.size();
            nextSong = originalSongs.get(nextIndex);
        }
        return nextSong;
    }

    private void showPlayerView() {
        // Set up the animation for showing playerView
        slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);

        miniPlayer.setVisibility(View.GONE);
        playerView.setVisibility(View.VISIBLE);
        playerView.startAnimation(slide_up);
    }

    private void exitPlayerView() {
        // Set up the animation for hiding playerView
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        miniPlayer.setVisibility(View.VISIBLE);
        miniPlayer.animate().translationY(0).start();
        playerView.setVisibility(View.GONE);
        playerView.startAnimation(slide_down);
    }


    private void getUser() {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseUser.getUid());
        documentReference.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.d("TAG", "Listen failed.", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                String Username = documentSnapshot.getString("Username");
                String Ava = documentSnapshot.getString("Avatar");
                String Email = documentSnapshot.getString("Email");
                Glide.with(getApplicationContext())
                        .load(Ava).circleCrop()
                        .into(imageViewUserAva);
                txtUserName.setText(Username);
                txtUserMail.setText(Email);
            } else {
                Log.d("TAG", "Current data: null");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.ic_search);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navHome:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new HomeTabFragment()).commit();
                break;
            case R.id.navTopic:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new TopicFragment()).commit();
                break;
            case R.id.navNewSong:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new NewSongFragment()).commit();
                break;
            case R.id.navAlbums:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new AlbumsFragment()).commit();
                break;
            case R.id.navArtist:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new SingerFragment()).commit();
                break;
            case R.id.navPersonalMusic:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new PersonalMusicFragment()).commit();
                break;
            case R.id.navLogout:
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), loginActivity.class));
                finish();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}