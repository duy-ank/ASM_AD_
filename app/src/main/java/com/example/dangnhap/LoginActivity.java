package com.example.dangnhap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String TEST_EMAIL = "user@example.com";
    private static final String TEST_PASSWORD = "123456";

    private EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ánh xạ view
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        TextView registerTextView = findViewById(R.id.signUpText);

        // Kiểm tra nếu đã đăng nhập thì chuyển thẳng đến MainActivity
        if (isLoggedIn()) {
            navigateToMainActivity(getStoredEmail());
            return;
        }

        // Xử lý sự kiện đăng nhập
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (validateInput(email, password)) {
                authenticateUser(email, password);
            }
        });

        // Xử lý chuyển sang màn hình đăng ký
        registerTextView.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private boolean isLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.contains(KEY_EMAIL);
    }

    private String getStoredEmail() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(KEY_EMAIL, "");
    }

    private boolean validateInput(String email, String password) {
        boolean isValid = true;

        if (email.isEmpty()) {
            emailEditText.setError("Vui lòng nhập email");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Email không hợp lệ");
            isValid = false;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Vui lòng nhập mật khẩu");
            isValid = false;
        } else if (password.length() < 6) {
            passwordEditText.setError("Mật khẩu phải có ít nhất 6 ký tự");
            isValid = false;
        }

        return isValid;
    }

    private void authenticateUser(String email, String password) {
        // Trong thực tế, bạn sẽ kiểm tra với database hoặc API
        if (email.equals(TEST_EMAIL) && password.equals(TEST_PASSWORD)) {
            // Lưu trạng thái đăng nhập
            saveLoginState(email);
            // Chuyển đến màn hình chính
            navigateToMainActivity(email);
        } else {
            Toast.makeText(this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveLoginState(String email) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_EMAIL, email)
                .apply();
    }

    private void navigateToMainActivity(String email) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(KEY_EMAIL, email);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Kết thúc LoginActivity để không quay lại được
    }
}