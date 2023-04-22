 package com.example.musicapplication.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

 public class forgotPasswordActivity extends AppCompatActivity {
    Button btnBack;
    EditText edtEmail;
    Button btnSubmit;
    TextView txtCreateAccount,txtHavePassword;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        init();
        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(),loginActivity.class));
            finish();
        });
        txtCreateAccount.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(),registerActivity.class)));
        txtHavePassword.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(),loginActivity.class)));
        btnSubmit.setOnClickListener(view -> forgotPassword());
    }

     private void forgotPassword(){
         String email=edtEmail.getText().toString().trim();
         if(email.isEmpty())
         {
             edtEmail.setError("Nhập Email");
             return;
         }
         firebaseAuth.sendPasswordResetEmail(email)
                 .addOnSuccessListener(aVoid -> Toast.makeText(getApplicationContext().getApplicationContext(),"Đã gửi link cho Email",Toast.LENGTH_LONG).show())
                 .addOnFailureListener(e -> Toast.makeText(getApplicationContext().getApplicationContext(),"Lỗi! ",Toast.LENGTH_LONG).show());
     }
     private void init()
     {
         firebaseAuth = FirebaseAuth.getInstance();
         btnBack = findViewById(R.id.btnBack);
         edtEmail = findViewById(R.id.edtEmail);
         btnSubmit = findViewById(R.id.btnSubmit);
         txtCreateAccount = findViewById(R.id.txtCreateAccount);
         txtHavePassword = findViewById(R.id.txtHavePassword);
     }
}