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

public class VerifyOtpActivity extends AppCompatActivity {
    private AuthRepository authRepo;
    private EditText edtOtp;
    private Button btnVerify, btnBack;
    private TextView tvMessage;
    private String email; // Lưu email từ Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        authRepo = new AuthRepository();
        edtOtp = findViewById(R.id.edtOtp);
        btnVerify = findViewById(R.id.btnVerify);
        btnBack = findViewById(R.id.btnback);
        tvMessage = findViewById(R.id.tvMessage);

        // Lấy email từ Intent
        email = getIntent().getStringExtra("email");
        boolean fromForgotPassword = getIntent().getBooleanExtra("from_forgot_password", false);
        if (email == null || email.isEmpty()) {
            tvMessage.setText("Lỗi: Không tìm thấy email");
            btnVerify.setEnabled(false);
            return;
        }

        btnVerify.setOnClickListener(v -> {
            String otp = edtOtp.getText().toString().trim();

            if (otp.isEmpty()) {
                tvMessage.setText("Vui lòng nhập mã OTP");
                return;
            }

            authRepo.verifyOtp(email, otp).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Map<String, Object> body = response.body();
                        String message = body.containsKey("message") ? (String) body.get("message") : "Xác thực OTP thành công";
                        tvMessage.setText(message);
                        Toast.makeText(VerifyOtpActivity.this, message, Toast.LENGTH_SHORT).show();

                        // Nếu xác nhận thành công
                        if (response.code() == 200) {
                            if (fromForgotPassword) {
                                // Chuyển sang ResetPasswordActivity
                                Intent intent = new Intent(VerifyOtpActivity.this, ResetPasswordActivity.class);
                                intent.putExtra("email", email);
                                startActivity(intent);
                            } else {
                                // Chuyển sang LoginActivity
                                startActivity(new Intent(VerifyOtpActivity.this, LoginActivity.class));
                            }
                            finish();
                        }
                    } else {
                        String errorMsg = "Mã OTP không hợp lệ hoặc đã hết hạn";
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