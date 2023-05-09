package com.example.musicapplication.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.musicapplication.Adapter.SingerAdapter;
import com.example.musicapplication.Model.Singer;
import com.example.musicapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SingerFragment extends Fragment {
    View view;
    RecyclerView listSinger;
    ArrayList<Singer> singers;
    SingerAdapter adapter;
    FirebaseFirestore firebaseFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_singer, container, false);
        ImageView searchIcon = getActivity().findViewById(R.id.searchIcon);
        Fragment currentFragment = ((AppCompatActivity)getContext()).getSupportFragmentManager().findFragmentById(R.id.fragmentLayout);
        if (currentFragment instanceof SearchFragment) {
            searchIcon.setImageResource(R.drawable.nav_menu_search_close);
        } else {
            searchIcon.setImageResource(R.drawable.nav_menu_search);
        }
        firebaseFirestore = FirebaseFirestore.getInstance();
        listSinger = view.findViewById(R.id.listSinger);
        getSinger();
        adapter = new SingerAdapter(singers,getContext());
        listSinger.setLayoutManager(new GridLayoutManager(getActivity(), 3, GridLayoutManager.HORIZONTAL, false));
        listSinger.setAdapter(adapter);
        return view;
    }

    private void getSinger() {
        singers = new ArrayList<>();
        CollectionReference productRefs = firebaseFirestore.collection("Singer");
        productRefs.get().addOnSuccessListener(documentSnapshots -> {
            if (documentSnapshots.isEmpty()) {
                Log.d("TAG", "onSuccess: LIST EMPTY");
            } else {
                for (DocumentSnapshot document : documentSnapshots) {
                    String id = document.getId().trim();
                    String name = document.getString("name").trim();
                    String image = document.getString("image").trim();
                    List<String> idAlbum = (List<String>) document.get("idAlbum");
                    Singer Singer = new Singer(id,name,image,idAlbum);
                    singers.add(Singer);
                }

                adapter.notifyDataSetChanged();

            }
        }).addOnFailureListener(e -> Log.d("TAG","Error"));
    }
}