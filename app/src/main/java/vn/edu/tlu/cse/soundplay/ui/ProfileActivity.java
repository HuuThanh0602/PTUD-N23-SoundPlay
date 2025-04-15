package vn.edu.tlu.cse.soundplay.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.tlu.cse.soundplay.R;
import vn.edu.tlu.cse.soundplay.data.repository.AuthRepository;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String PREFS_NAME = "user_profile";

    private ImageView btnBack, btnEditAvatar, btnSave;
    private CircleImageView imgAvatar;
    private EditText edtName, edtEmail;
    private Button btnLogout;
    private SharedPreferences profilePrefs; // Khai báo ở đây để dùng chung
    private SharedPreferences authPrefs;    // Khai báo ở đây để dùng chung

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hoso);

        // Ánh xạ view
        btnBack = findViewById(R.id.btnBack);
        btnEditAvatar = findViewById(R.id.btnEditAvatar);
        btnSave = findViewById(R.id.btnSave);
        imgAvatar = findViewById(R.id.imgAvatar);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        btnLogout = findViewById(R.id.btnLogout);

        // Load dữ liệu SharedPreferences
        profilePrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        authPrefs = getSharedPreferences("auth", MODE_PRIVATE);
        edtName.setText(profilePrefs.getString("KEY_NAME", ""));
        edtEmail.setText(authPrefs.getString("email", ""));
        String avatarBase64 = profilePrefs.getString("KEY_AVATAR", "");
        if (!avatarBase64.isEmpty()) {
            byte[] decodedBytes = Base64.decode(avatarBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            imgAvatar.setImageBitmap(bitmap);
        }

        // Xử lý nút quay lại
        btnBack.setOnClickListener(v -> onBackPressed());

        // Nút chọn ảnh
        btnEditAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Chọn ảnh đại diện"), PICK_IMAGE_REQUEST);
        });

        // Nút lưu tên
        btnSave.setOnClickListener(v -> {
            String newName = edtName.getText().toString().trim();

            if (newName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy email từ SharedPreferences (sử dụng authPrefs đã khai báo)
            String email = authPrefs.getString("email", "");

            if (email.isEmpty()) {
                Toast.makeText(this, "Không tìm thấy email, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return;
            }

            // Gọi API updateName từ AuthRepository
            AuthRepository authRepo = new AuthRepository();
            authRepo.updateName(newName, email).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Lưu tên vào SharedPreferences (sử dụng profilePrefs đã khai báo)
                        profilePrefs.edit().putString("KEY_NAME", newName).apply();

                        Toast.makeText(ProfileActivity.this, "Tên đã được cập nhật", Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMsg = "Lỗi khi cập nhật tên";
                        if (response.errorBody() != null) {
                            try {
                                errorMsg = response.errorBody().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(ProfileActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(ProfileActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Xử lý nút đăng xuất
        btnLogout.setOnClickListener(v -> {
            // Xóa dữ liệu trong SharedPreferences (sử dụng authPrefs và profilePrefs đã khai báo)
            authPrefs.edit().clear().apply();
            profilePrefs.edit().clear().apply();

            // Hiển thị thông báo đăng xuất
            Toast.makeText(ProfileActivity.this, "Tài khoản đã đăng xuất", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            try {
                Uri uri = data.getData();
                Bitmap selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imgAvatar.setImageBitmap(selectedBitmap);

                // Lưu ảnh vào SharedPreferences
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

                profilePrefs.edit().putString("KEY_AVATAR", encodedImage).apply();

                Toast.makeText(this, "Đã cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Không thể chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }
}