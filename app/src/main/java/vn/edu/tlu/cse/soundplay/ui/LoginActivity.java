package vn.edu.tlu.cse.soundplay.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.tlu.cse.soundplay.R;
import vn.edu.tlu.cse.soundplay.data.repository.AuthRepository;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private AuthRepository authRepo;
    private EditText edtEmail, edtPassword;
    private Button btnLogin, btnBack;
    private TextView tvMessage, tvRegister, tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authRepo = new AuthRepository();
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnBack = findViewById(R.id.btnback);
        tvMessage = findViewById(R.id.tvMessage);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                tvMessage.setText("Vui lòng nhập email và mật khẩu");
                return;
            }

            authRepo.login(email, password).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    Log.d(TAG, "Response code: " + response.code());
                    Log.d(TAG, "Response message: " + response.message());
                    if (response.body() != null) {
                        Log.d(TAG, "Response body: " + response.body().toString());
                    } else {
                        Log.d(TAG, "Response body is null");
                    }

                    if (response.isSuccessful() && response.body() != null) {
                        Map<String, Object> body = response.body();
                        String token = body.containsKey("token") ? (String) body.get("token") : null;
                        Map<String, Object> user = body.containsKey("user") ? (Map<String, Object>) body.get("user") : null;

                        if (token != null && user != null) {
                            // Lưu token và email vào SharedPreferences ("auth")
                            getSharedPreferences("auth", MODE_PRIVATE)
                                    .edit()
                                    .putString("token", token)
                                    .putString("email", user.containsKey("email") ? (String) user.get("email") : "")
                                    .apply();

                            // Lưu name và avatarBase64 vào SharedPreferences ("user_profile")
                            String name = user.containsKey("name") ? (String) user.get("name") : "Hello!";
                            String avatarBase64 = ""; // Mặc định vì server không trả về avatar
                            getSharedPreferences("user_profile", MODE_PRIVATE)
                                    .edit()
                                    .putString("KEY_NAME", name)
                                    .putString("KEY_AVATAR", avatarBase64)
                                    .apply();

                            // Lấy name và avatarBase64 để sử dụng (nếu cần)
                            String savedName = getSharedPreferences("user_profile", MODE_PRIVATE)
                                    .getString("KEY_NAME", "Hello!");
                            String savedAvatarBase64 = getSharedPreferences("user_profile", MODE_PRIVATE)
                                    .getString("KEY_AVATAR", "");
                            Log.d(TAG, "Saved name: " + savedName);
                            Log.d(TAG, "Saved avatarBase64: " + savedAvatarBase64);

                            tvMessage.setText("Đăng nhập thành công");
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            tvMessage.setText("Lỗi: Không nhận được token hoặc thông tin người dùng");
                            Log.e(TAG, "Token or user is null");
                        }
                    } else {
                        String errorMsg = "Thông tin đăng nhập không hợp lệ";
                        if (response.code() == 401) {
                            errorMsg = "Email hoặc mật khẩu không đúng";
                        } else if (response.code() == 403) {
                            errorMsg = "Vui lòng xác thực OTP trước khi đăng nhập";
                        } else if (response.errorBody() != null) {
                            try {
                                errorMsg = response.errorBody().string();
                                Log.d(TAG, "Error body: " + errorMsg);
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing errorBody: " + e.getMessage());
                            }
                        }
                        tvMessage.setText(errorMsg);
                        Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Log.e(TAG, "Network error: " + t.getMessage());
                    tvMessage.setText("Lỗi mạng: " + t.getMessage());
                    Toast.makeText(LoginActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}