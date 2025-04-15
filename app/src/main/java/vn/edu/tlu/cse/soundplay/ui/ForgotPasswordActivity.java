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

public class ForgotPasswordActivity extends AppCompatActivity {
    private AuthRepository authRepo;
    private EditText edtEmail;
    private Button btnSendOtp, btnBack;
    private TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        authRepo = new AuthRepository();
        edtEmail = findViewById(R.id.edtEmail);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        btnBack = findViewById(R.id.btnback);
        tvMessage = findViewById(R.id.tvMessage);

        btnSendOtp.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();

            if (email.isEmpty()) {
                tvMessage.setText("Vui lòng nhập email");
                return;
            }

            authRepo.forgotPassword(email).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Map<String, Object> body = response.body();
                        String message = (String) body.get("message");
                        tvMessage.setText(message);
                        Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();

                        // Chuyển sang VerifyOtpActivity để nhập OTP
                        Intent intent = new Intent(ForgotPasswordActivity.this, VerifyOtpActivity.class);
                        intent.putExtra("email", email);
                        intent.putExtra("from_forgot_password", true); // Đánh dấu để biết OTP từ quên mật khẩu
                        startActivity(intent);
                    } else {
                        tvMessage.setText("Email không tồn tại hoặc lỗi: " + response.message());
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