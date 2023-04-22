package com.example.musicapplication.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class loginActivity extends AppCompatActivity {
    EditText edtEmail,edtPassword;
    Button btnLogin;
    TextView txtCreateAccount,txtForgotPassword;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        txtCreateAccount.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(),registerActivity.class)));
        txtForgotPassword.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(),forgotPasswordActivity.class)));
        btnLogin.setOnClickListener(view -> Login());
    }

    private void Login(){
        String email=edtEmail.getText().toString().trim();
        String password=edtPassword.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            edtEmail.setError("Email chưa điền");
            return;
        }
        if(!email.matches(emailPattern)){
            edtEmail.setError("Sai định dạng Email");
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
        }
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(getApplicationContext(),"Đăng nhập thành công",Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
            else {
                Toast.makeText(getApplicationContext().getApplicationContext(),"Lỗi ! ",Toast.LENGTH_LONG).show();
            }
        });
    }


    private void init()
    {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtCreateAccount = findViewById(R.id.txtCreateAccount);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        progressBar = findViewById(R.id.progress_circular);
        firebaseAuth = FirebaseAuth.getInstance();
    }
}