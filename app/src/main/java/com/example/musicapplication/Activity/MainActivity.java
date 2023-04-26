package com.example.musicapplication.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;



import com.bumptech.glide.Glide;
import com.example.musicapplication.Fragment.AlbumsFragment;
import com.example.musicapplication.Fragment.Home.HomeTabFragment;
import com.example.musicapplication.Fragment.PersonalMusicFragment;
import com.example.musicapplication.Fragment.ProfileFragment;
import com.example.musicapplication.Fragment.TopicFragment;
import com.example.musicapplication.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        //Set toolbar
        setSupportActionBar(toolbar);
        setTitle("");
        toolbarLogo.setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new HomeTabFragment()).commit();
            navigationView.setCheckedItem(R.id.navHome);
        });


        // Set Nav Menu
        firebaseUser = firebaseAuth.getCurrentUser();
        View headerView = navigationView.getHeaderView(0);
        imageViewEdit = headerView.findViewById(R.id.imageViewEdit);
        imageViewUserAva = headerView.findViewById(R.id.imageViewUserAva);
        txtUserName = headerView.findViewById(R.id.txtUserName);
        txtUserMail = headerView.findViewById(R.id.txtUserMail);
        getUser();

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


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.ic_search);
        return true;
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navHome:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new HomeTabFragment()).commit();
                break;
            case R.id.navTopic:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new TopicFragment()).commit();
                break;
            case R.id.navAlbums:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new AlbumsFragment()).commit();
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

    private void init(){
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        toolbarLogo = findViewById(R.id.toolbarLogo);
    }
}