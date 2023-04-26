package com.example.musicapplication.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicapplication.Activity.loginActivity;
import com.example.musicapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    AppCompatButton btnEditProfile, btnChangePassword, btnLogout;
    EditText edtUsername, edtEmail, edtAddress, edtPhone;
    Button btnCancel, btnReset;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);

        btnEditProfile.setOnClickListener(view1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View dialogView = getLayoutInflater().inflate(R.layout.fragment_profile_edit_info_dialog,null);
            edtUsername = dialogView.findViewById(R.id.edtUsername);
            edtEmail = dialogView.findViewById(R.id.edtEmail);
            edtAddress = dialogView.findViewById(R.id.edtAddress);
            edtPhone = dialogView.findViewById(R.id.edtPhone);
            btnCancel = dialogView.findViewById(R.id.btnCancel);
            btnReset = dialogView.findViewById(R.id.btnReset);
            getUser();
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            btnReset.setOnClickListener(view2 -> {
                updateUserInfo();
            });

            btnCancel.setOnClickListener(view2 -> {
                dialog.dismiss();
            });
            dialog.show();
        });

        btnLogout.setOnClickListener(view1 -> {
            firebaseAuth.signOut();
            startActivity(new Intent(getContext(), loginActivity.class));
            getActivity().finish();
        });
        return view;
    }

    private void getUser() {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseUser.getUid());
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                String Username = document.getString("Username");
                String Email = document.getString("Email");
                String Address = document.getString("Address");
                String Phone = document.getString("Phone");
                edtUsername.setText(Username);
                edtEmail.setText(Email);
                edtAddress.setText(Address);
                edtPhone.setText(Phone);

            } else
            {
                Log.d("TAG", "get failed with ", task.getException());
            }
        });
    }

    private void updateUserInfo() {

    }
}