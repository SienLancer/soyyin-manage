package com.example.soyyinmanage.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soyyinmanage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEt, passwordEt;
    private Button loginBtn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        loginBtn = findViewById(R.id.loginBtn);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        loginBtn.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEt.setError("Nhập email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEt.setError("Nhập mật khẩu");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) checkUserRole(user.getUid());
                    } else {
                        Exception e = task.getException();
                        String errorMessage = "Đăng nhập thất bại";

                        if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidUserException) {
                            // Tài khoản không tồn tại hoặc bị xoá
                            errorMessage = "Tài khoản không tồn tại hoặc đã bị xoá";
                        } else if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                            // Sai mật khẩu hoặc email không hợp lệ
                            errorMessage = "Sai mật khẩu hoặc email không hợp lệ";
                        } else if (e instanceof com.google.firebase.FirebaseNetworkException) {
                            // Lỗi mạng
                            errorMessage = "Không có kết nối Internet";
                        } else if (e != null) {
                            // Bắt fallback tất cả lỗi khác
                            String msg = e.getLocalizedMessage();
                            if (msg != null && msg.toLowerCase().contains("password")) {
                                errorMessage = "Sai mật khẩu";
                            } else if (msg != null && msg.toLowerCase().contains("user")) {
                                errorMessage = "Tài khoản không tồn tại";
                            }
                        }

                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void checkUserRole(String uid) {
        usersRef.child(uid).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role = snapshot.getValue(String.class);
                if (role != null) {
                    if (role.equals("admin")) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Bạn không có quyền truy cập", Toast.LENGTH_SHORT).show();
                        mAuth.signOut(); // log out user không phải admin
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Không tìm thấy role của user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Lỗi Database", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
