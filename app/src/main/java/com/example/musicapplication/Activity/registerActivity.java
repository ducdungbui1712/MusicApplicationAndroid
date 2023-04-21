package com.example.musicapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.musicapplication.R;

public class registerActivity extends AppCompatActivity {
    Button btnBack, btnRegister;
    EditText edtPassword,edtUsername,edtConfirmPassword,edtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(),loginActivity.class));
            finish();
        });

        btnRegister.setOnClickListener(view -> {
            registerAccount();
        });
    }

    private void registerAccount() {
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();
        String username = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        if(password.compareTo(confirmPassword)!=0){
            edtConfirmPassword.setError("Không giống mật khẩu 1");
            return;
        }
        if(TextUtils.isEmpty(email)){
            edtEmail.setError("Hãy điền Email");
            return;
        }
        if(TextUtils.isEmpty(password))
        {
            edtPassword.setError("Hãy nhập mật khẩu");
            return;
        }
        if(password.length()<=6)
        {
            edtPassword.setError("Mật khẩu cần hơn 6 ký tự");
            return;
        }else {

        }
    }

    private void init(){
        btnBack = findViewById(R.id.btnBack);
        edtPassword = findViewById(R.id.edtPassword);
        edtUsername = findViewById(R.id.edtUsername);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtEmail = findViewById(R.id.edtEmail);
        btnRegister = findViewById(R.id.btnRegister);
    }
}