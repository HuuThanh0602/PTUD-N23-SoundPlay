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

public class RegisterActivity extends AppCompatActivity {
    private AuthRepository authRepo;
    private EditText edtName, edtEmail, edtPassword, edtConfirmPassword;
    private Button btnRegister, btnBack;
    private TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authRepo = new AuthRepository();
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnback);
        tvMessage = findViewById(R.id.tvMessage);

        btnRegister.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            // Kiểm tra input
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                tvMessage.setText("Vui lòng nhập đầy đủ thông tin");
                return;
            }

            if (!password.equals(confirmPassword)) {
                tvMessage.setText("Mật khẩu xác nhận không khớp");
                return;
            }

            authRepo.register(name, email, password).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Map<String, Object> body = response.body();
                        String message = (String) body.get("message");
                        tvMessage.setText(message);
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RegisterActivity.this, VerifyOtpActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    } else {
                        tvMessage.setText("Đăng ký thất bại: " + response.message());
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