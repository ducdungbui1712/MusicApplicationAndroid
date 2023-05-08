package com.example.musicapplication.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOverlay;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import android.widget.SeekBar;
import android.widget.TextView;



import com.bumptech.glide.Glide;
import com.example.musicapplication.Adapter.NewSongAdapter;
import com.example.musicapplication.Adapter.PersonalMusicAdapter;
import com.example.musicapplication.Fragment.AlbumsFragment;
import com.example.musicapplication.Fragment.HomeFragment;
import com.example.musicapplication.Fragment.SearchFragment;
import com.example.musicapplication.Fragment.SingerFragment;
import com.example.musicapplication.Fragment.NewSongFragment;
import com.example.musicapplication.Fragment.PersonalMusicFragment;
import com.example.musicapplication.Fragment.ProfileFragment;
import com.example.musicapplication.Model.Song;
import com.example.musicapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;

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
    ImageView toolbarLogo, imageViewEdit, searchIcon;
    CircleImageView imageViewUserAva;
    TextView txtUserName, txtUserMail;
    ObjectAnimator objectAnimator;
    MediaPlayer mediaPlayer = new MediaPlayer();
    public static boolean isPersonalAdapter = false;
    FrameLayout frameLayout;
    boolean searchClicked = false;

    //player_view
    RelativeLayout playerView;
    CircleImageView songImage;
    ImageView backArrow, like;
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
    private BroadcastReceiver sendSongReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("sendSong")) {
                if(intent != null){
                    song = intent.getParcelableExtra("song");
                    ArrayList<Song> ListSongs = intent.getParcelableArrayListExtra("songs");
                    isPersonalAdapter = intent.getBooleanExtra("isPersonalAdapter", false);
                    mediaPlayer = isPersonalAdapter ? PersonalMusicAdapter.personalSongPlayer : NewSongAdapter.newSongPlayer;
                    loadData(song);
                    songs.clear();
                    originalSongs.clear();
                    songs.addAll(ListSongs);
                    originalSongs.addAll(songs);
                }
            }
        }
    };

    private BroadcastReceiver personalSongReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("personalSong")) {
                if(intent != null){
                    song = intent.getParcelableExtra("song");
                    ArrayList<Song> ListSongs = intent.getParcelableArrayListExtra("songs");
                    isPersonalAdapter = intent.getBooleanExtra("isPersonalAdapter", false);
                    mediaPlayer = isPersonalAdapter ? PersonalMusicAdapter.personalSongPlayer : NewSongAdapter.newSongPlayer;
                    loadData(song);
                    songs.clear();
                    originalSongs.clear();
                    songs.addAll(ListSongs);
                    originalSongs.addAll(songs);
                }
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
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.navHome);
        }

        searchIcon.setOnClickListener(view -> {
            Log.d("Search Icon Clicked", "Error");
            if (searchClicked) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_bottom, R.anim.slide_in_top, R.anim.slide_out_bottom)
                        .replace(R.id.fragmentLayout, new SearchFragment())
                        .addToBackStack(null)
                        .commit();
                searchIcon.setImageResource(R.drawable.nav_menu_search_close);
                searchClicked = false;
            } else {
                getSupportFragmentManager().popBackStack();
                searchIcon.setImageResource(R.drawable.nav_menu_search);
                searchClicked = true;
            }
        });

        // Register the broadcast receiver
        IntentFilter sendSongFilter = new IntentFilter();
        sendSongFilter.addAction("sendSong");
        IntentFilter personalSongFilter = new IntentFilter();
        personalSongFilter.addAction("personalSong");
        registerReceiver(sendSongReceiver, sendSongFilter);
        registerReceiver(personalSongReceiver, personalSongFilter);
        playerViewControl();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the broadcast receiver to avoid memory leaks
        unregisterReceiver(sendSongReceiver);
        unregisterReceiver(personalSongReceiver);

        //release the player
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        mediaPlayer.release();

    }

    public void loadData(Song song) {

        Log.d("songID", song.getId());
        Log.d("songs.size()", String.valueOf(songs.size()));
        Log.d("originalSongs.size()", String.valueOf(originalSongs.size()));
        Log.d("isPersonalAdapter", String.valueOf(isPersonalAdapter));
        Log.d("isPersonalAdapter", String.valueOf(mediaPlayer));
        //playerView
        songTitle.setText(song.getTitle().trim());
        getAlbumAndArtistTitle(song);
        timeDuration.setText(song.getDuration().trim());
        Glide.with(getApplicationContext())
                .load(song.getImage().trim())
                .into(songImage);

        setAnimationSongImage(songImage);

        //miniPlayer
        songTitleMiniPlayer.setText(song.getTitle().trim());
        Glide.with(getApplicationContext())
                .load(song.getImage().trim())
                .into(songImageMiniPlayer);
        setAnimationSongImage(songImageMiniPlayer);

        // Set image resource for like button
        FirebaseFirestore.getInstance().collection("Users").document(firebaseUser.getUid().trim())
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("TAG", "Listen failed.", error);
                        return;
                    }
                    if (value != null && value.exists()) {
                        ArrayList<String> likedSongIds = (ArrayList<String>) value.get("songLiked");
                        if (likedSongIds != null && likedSongIds.contains(song.getId().trim())) {
                            like.setImageResource(R.mipmap.heart_on);
                        }else {
                            like.setImageResource(R.mipmap.heart);
                        }
                    } else {
                        Log.d("TAG", "Current data: null");
                    }
                });

        seekBar.setMax(mediaPlayer.getDuration());
        seekBarMiniPlayer.setMax(mediaPlayer.getDuration());
        updateSeekBar();
    }

    private void updateSeekBar() {
        handler = new Handler();
        handler.postDelayed(() -> {
            if (mediaPlayer != null) { // Check if mediaPlayer is not null
                int currentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(currentPosition);
                seekBarMiniPlayer.setProgress(currentPosition);
                int timeLeftInMillis = mediaPlayer.getDuration() - currentPosition;
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
        if (mediaPlayer != null) { // Check if mediaPlayer is not null
            mediaPlayer.setOnCompletionListener(mp -> {
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
                mediaPlayer.reset();
                mediaPlayer.setDataSource(song.getLink().trim());
                mediaPlayer.prepare();
                mediaPlayer.start();
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

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
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
        frameLayout = findViewById(R.id.fragmentLayout);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbarLogo = findViewById(R.id.toolbarLogo);
        searchIcon = findViewById(R.id.searchIcon);


        //player_view
        playerView = findViewById(R.id.playerView);
        songImage = findViewById(R.id.songImage);
        backArrow = findViewById(R.id.backArrow);
        like = findViewById(R.id.like);
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
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new HomeFragment()).commit();
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
        like.setOnClickListener(view -> {
            firebaseUser = firebaseAuth.getCurrentUser();
            String userID = firebaseUser.getUid().trim();
            DocumentReference docRef = firebaseFirestore.collection("Users").document(userID);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<String> songLiked = (ArrayList<String>) document.get("songLiked");
                        if (songLiked.contains(song.getId())) {
                            // Nếu đã có bài hát trong songLiked thì xóa nó đi
                            docRef.update("songLiked", FieldValue.arrayRemove(song.getId()))
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("TAG", "onSuccess: Song removed from liked list");
                                        like.setImageResource(R.mipmap.heart);
                                        // Giảm số likes của bài hát đi 1
                                        firebaseFirestore.collection("Songs").document(song.getId())
                                                .update("likes", FieldValue.increment(-1))
                                                .addOnSuccessListener(aVoid1 -> Log.d("TAG", "onSuccess: Likes updated"))
                                                .addOnFailureListener(e -> Log.w("TAG", "Error updating likes", e));
                                    })
                                    .addOnFailureListener(e -> Log.w("TAG", "Error removing song from liked list", e));
                        } else {
                            // Nếu chưa có bài hát trong songLiked thì thêm nó vào
                            docRef.update("songLiked", FieldValue.arrayUnion(song.getId()))
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("TAG", "onSuccess: Song added to liked list");
                                        like.setImageResource(R.mipmap.heart_on);
                                        // Tăng số likes của bài hát lên 1
                                        firebaseFirestore.collection("Songs").document(song.getId())
                                                .update("likes", FieldValue.increment(1))
                                                .addOnSuccessListener(aVoid1 -> Log.d("TAG", "onSuccess: Likes updated"))
                                                .addOnFailureListener(e -> Log.w("TAG", "Error updating likes", e));
                                    })
                                    .addOnFailureListener(e -> Log.w("TAG", "Error adding song to liked list", e));
                        }
                    }
                } else {
                    Log.d("TAG", "Error getting document: ", task.getException());
                }
            });
        });
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
                            if (mediaPlayer != null) {
                                mediaPlayer.stop();
                                mediaPlayer.release();
                                mediaPlayer = null; // Set mediaPlayer to null after releasing it
                                if(isPersonalAdapter){
                                    PersonalMusicAdapter.personalSongPlayer = null;
                                }else {
                                    NewSongAdapter.newSongPlayer = null;
                                }
                            }
                        } else {
                            // User did not drag down enough, so restore the Mini player to its original position
                            miniPlayer.animate().translationY(0).start();
                            showPlayerView();
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
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                setAnimationSongImage(songImage); // dừng animation
            } else {
                mediaPlayer.start();
                setAnimationSongImage(songImage); // bắt đầu animation
            }
        });

        playMiniPlayer.setOnClickListener(view1 -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                setAnimationSongImage(songImage); // dừng animation
            } else {
                mediaPlayer.start();
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
                play.setImageResource(R.mipmap.play);
                objectAnimator.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.start();
                play.setImageResource(R.mipmap.pause);
                objectAnimator.resume();
            }
        });

        seekBarMiniPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
                playMiniPlayer.setImageResource(R.mipmap.play);
                objectAnimator.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.start();
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
                if (s.getId().trim().equals(currentSong.getId().trim())) {
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
                if (s.getId().trim().equals(currentSong.getId().trim())) {
                    songToFind = s;
                    break;
                }
            }

            int currentIndex = originalSongs.indexOf(songToFind);
            int nextIndex = (currentIndex + 1) % originalSongs.size();
//            int nextIndex = ((currentIndex + 1) % originalSongs.size())  == 0 ? 0 : (currentIndex + 1) % originalSongs.size();
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
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseUser.getUid().trim());
        documentReference.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.d("TAG", "Listen failed.", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                String Username = documentSnapshot.getString("Username").trim();
                String Ava = documentSnapshot.getString("Avatar").trim();
                String Email = documentSnapshot.getString("Email").trim();
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navHome:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new HomeFragment()).commit();
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