package com.example.dangnhap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private TextView loginTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ view
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        loginTextView = findViewById(R.id.loginText);

        // Xử lý sự kiện đăng ký
        registerButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (validateInput(name, email, password, confirmPassword)) {
                // Giả lập đăng ký thành công
                Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        // Chuyển sang màn hình đăng nhập
        loginTextView.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });
    }

    private boolean validateInput(String name, String email, String password, String confirmPassword) {
        if (name.isEmpty()) {
            nameEditText.setError("Vui lòng nhập họ tên");
            return false;
        }

        if (email.isEmpty()) {
            emailEditText.setError("Vui lòng nhập email");
            return false;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Vui lòng nhập mật khẩu");
            return false;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Mật khẩu không khớp");
            return false;
        }

        return true;
    }
}