package com.example.musicapplication.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.musicapplication.Activity.loginActivity;
import com.example.musicapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    AppCompatButton btnEditProfile, btnChangePassword, btnLogout;
    EditText edtUsername, edtEmail, edtAddress, edtPhone;
    Button btnCancel, btnReset, btnSend;
    CircleImageView imageViewUserAva;
    ImageView imageViewEditIcon;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ImageView searchIcon = getActivity().findViewById(R.id.searchIcon);
        Fragment currentFragment = ((AppCompatActivity)getContext()).getSupportFragmentManager().findFragmentById(R.id.fragmentLayout);
        if (currentFragment instanceof SearchFragment) {
            searchIcon.setImageResource(R.drawable.nav_menu_search_close);
        } else {
            searchIcon.setImageResource(R.drawable.nav_menu_search);
        }
        imageViewUserAva = view.findViewById(R.id.imageViewUserAva);
        imageViewEditIcon = view.findViewById(R.id.imageViewEditIcon);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);

        getImageAva();

        imageViewEditIcon.setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 123);
        });

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123 && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            imageViewUserAva.setImageURI(imageUri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                uploadImageToFireStore(base64Image);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void uploadImageToFireStore(String base64Image) {
        if (firebaseUser != null) {
            DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseUser.getUid());
            Map<String, Object> user = new HashMap<>();
            user.put("Avatar", "data:image/png;base64," + base64Image);
            documentReference.set(user, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d("TAG", "Avatar uploaded successfully"))
                    .addOnFailureListener(e -> Log.d("TAG", "Avatar upload failed: " + e.getMessage()));
        }


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
        edtEmail = dialog.findViewById(R.id.edtEmail);
        btnCancel = dialog.findViewById(R.id.btnCancel);
        btnSend = dialog.findViewById(R.id.btnSend);

        btnSend.setOnClickListener(view2 -> {
            forgotPassword();
        });

        btnCancel.setOnClickListener(view2 -> {
            dialog.dismiss();
        });
        dialog.show();
    }
    private void getImageAva() {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseUser.getUid());
        documentReference.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.d("TAG", "Listen failed.", error);
                return;
            }

            if (value != null && value.exists()) {
                String ava = value.getString("Avatar").trim();
                if (ava != null) {
                    Glide.with(getContext())
                            .load(ava)
                            .circleCrop()
                            .into(imageViewUserAva);
                }
            } else {
                Log.d("TAG", "Current data: null");
            }
        });
    }


    private void getUser() {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseUser.getUid().trim());
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                String Username = document.getString("Username").trim();
                String Email = document.getString("Email").trim();
                String Address = document.getString("Address").trim();
                String Phone = document.getString("Phone").trim();
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
        Map<String, Object> map = new HashMap<>();
        map.put("Phone", edtPhone.getText().toString());
        map.put("Username", edtUsername.getText().toString());
        map.put("Address", edtAddress.getText().toString());
        map.put("Email", edtEmail.getText().toString());
        firebaseFirestore.collection("Users").document(firebaseUser.getUid()).update(map)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Cập nhật thông tin thành công",Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "Successfully updated!");
                })
                .addOnFailureListener(e -> Log.d("TAG", "Unsuccessfully updated!"));
    }
    private void forgotPassword(){
        String email=edtEmail.getText().toString().trim();
        if(email.isEmpty())
        {
            edtEmail.setError("Nhập Email");
            return;
        }
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext().getApplicationContext(),"Đã gửi link cho Email",Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> Toast.makeText(getContext().getApplicationContext(),"Lỗi! ",Toast.LENGTH_LONG).show());
    }
}