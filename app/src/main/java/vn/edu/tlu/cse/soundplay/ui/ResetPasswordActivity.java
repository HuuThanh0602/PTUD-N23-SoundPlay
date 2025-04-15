package vn.edu.tlu.cse.soundplay.ui;

import android.content.Intent;
import android.os.Bundle;
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

public class ResetPasswordActivity extends AppCompatActivity {
    private AuthRepository authRepo;
    private EditText  edtPassword, edtConfirmPassword;
    private Button btnResetPassword, btnBack;
    private TextView tvMessage;
    private String email; // Lưu email từ Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        authRepo = new AuthRepository();
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnBack = findViewById(R.id.btnback);
        tvMessage = findViewById(R.id.tvMessage);

        // Lấy email từ Intent
        email = getIntent().getStringExtra("email");
        if (email == null || email.isEmpty()) {
            tvMessage.setText("Lỗi: Không tìm thấy email");
            btnResetPassword.setEnabled(false);
            return;
        }

        btnResetPassword.setOnClickListener(v -> {

            String password = edtPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            // Kiểm tra input
            if (password.isEmpty() || confirmPassword.isEmpty()) {
                tvMessage.setText("Vui lòng nhập đầy đủ mã OTP và mật khẩu");
                return;
            }

            if (!password.equals(confirmPassword)) {
                tvMessage.setText("Mật khẩu xác nhận không khớp");
                return;
            }

            authRepo.resetPassword(email, password).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Map<String, Object> body = response.body();
                        String message = body.containsKey("message") ? (String) body.get("message") : "Đặt lại mật khẩu thành công";
                        tvMessage.setText(message);
                        Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();

                        // Chuyển về LoginActivity
                        if (response.code() == 200) {
                            startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                            finish();
                        }
                    } else {
                        String errorMsg = "Lỗi: Mã OTP không hợp lệ hoặc đã hết hạn";
                        if (response.errorBody() != null) {
                            try {
                                errorMsg = response.errorBody().string();
                            } catch (Exception e) {
                                errorMsg = "Lỗi khi xử lý phản hồi từ server";
                            }
                        }
                        tvMessage.setText(errorMsg);
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    tvMessage.setText("Lỗi mạng: " + t.getMessage());
                }
            });
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}