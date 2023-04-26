package com.example.musicapplication.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
            showDialogEditProfile();
        });

        btnChangePassword.setOnClickListener(view1 -> {
            showDialogChangePassword();
        });

        btnLogout.setOnClickListener(view1 -> {
            firebaseAuth.signOut();
            startActivity(new Intent(getContext(), loginActivity.class));
            getActivity().finish();
        });
        return view;
    }

    private void showDialogEditProfile() {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_profile_edit_info_dialog);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
        dialog.setCanceledOnTouchOutside(true);
        edtUsername = dialog.findViewById(R.id.edtUsername);
        edtEmail = dialog.findViewById(R.id.edtEmail);
        edtAddress = dialog.findViewById(R.id.edtAddress);
        edtPhone = dialog.findViewById(R.id.edtPhone);
        btnCancel = dialog.findViewById(R.id.btnCancel);
        btnReset = dialog.findViewById(R.id.btnReset);
        getUser();

        btnReset.setOnClickListener(view2 -> {
            updateUserInfo();
        });

        btnCancel.setOnClickListener(view2 -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    private void showDialogChangePassword() {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_profile_change_password_dialog);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
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