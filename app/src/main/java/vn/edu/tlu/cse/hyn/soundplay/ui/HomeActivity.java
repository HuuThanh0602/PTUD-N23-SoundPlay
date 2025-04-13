package vn.edu.tlu.cse.hyn.soundplay.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.tlu.cse.hyn.soundplay.R;
import vn.edu.tlu.cse.hyn.soundplay.adapter.MixAdapter;
import vn.edu.tlu.cse.hyn.soundplay.adapter.MusicAdapter;
import vn.edu.tlu.cse.hyn.soundplay.adapter.RecentAdapter;
import vn.edu.tlu.cse.hyn.soundplay.data.model.PlayList;
import vn.edu.tlu.cse.hyn.soundplay.data.repository.MusicRepository;

public class HomeActivity extends AppCompatActivity {

    RecyclerView recyclerRecent, recyclerForYou, recyclerRecentPlays;
    LinearLayout navProfile;
    CircleImageView avatarHome;
    TextView txtUserName;
    private MusicRepository musicRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        musicRepository = new MusicRepository();

        // Ánh xạ view
        recyclerRecent = findViewById(R.id.recyclerRecent);
        recyclerForYou = findViewById(R.id.recyclerForYou);
        recyclerRecentPlays = findViewById(R.id.recyclerRecentPlays);
        navProfile = findViewById(R.id.navProfile);
        avatarHome = findViewById(R.id.avatarHome);
        txtUserName = findViewById(R.id.txtUserName);

        loadUserProfile();

        // Thiết lập layout cho từng RecyclerView
        recyclerRecent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerForYou.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerRecentPlays.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        getTop100Music();
        getForYouMusic();
        getRecentMusic();
        // Gọi API để lấy dữ liệu top 100

        // Chuyển sang Hồ sơ
        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        LinearLayout libraryTab = findViewById(R.id.library);

        libraryTab.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, FavouriteActivity.class);
            startActivity(intent);
        });

    }

    private void getTop100Music() {
        musicRepository.getTop100(new MusicRepository.Top100Callback() {
            @Override
            public void onSuccess(List<PlayList> top100List) {
                // In log kiểm tra dữ liệu
                for (PlayList playList : top100List) {
                    Log.d("Top100", "ID: " + playList.getId() + ", Tên: " + playList.getTitle() + ", Ảnh: " + playList.getThumbnail());
                }
                // Đổ dữ liệu vào RecyclerView
                MusicAdapter musicAdapter = new MusicAdapter(top100List);
                recyclerForYou.setAdapter(musicAdapter);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("Top100", "Lỗi: " + errorMessage);
            }
        });
    }
    private void getForYouMusic() {
        musicRepository.getTop100(new MusicRepository.Top100Callback() {
            @Override
            public void onSuccess(List<PlayList> top100List) {
                // Chọn ngẫu nhiên 5 bài
                List<PlayList> randomList = top100List.subList(0, Math.min(5, top100List.size()));
                MixAdapter mixAdapter = new MixAdapter(randomList);
                recyclerForYou.setAdapter(mixAdapter);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("ForYou", "Lỗi: " + errorMessage);
            }
        });
    }
    private void getRecentMusic() {
        musicRepository.getTop100(new MusicRepository.Top100Callback() {
            @Override
            public void onSuccess(List<PlayList> top100List) {
                // Lấy 3 bài mới nhất (hoặc do người dùng nghe gần đây nếu có lưu local)
                List<PlayList> recentList = top100List.subList(0, Math.min(3, top100List.size()));
                RecentAdapter recentAdapter = new RecentAdapter(recentList);
                recyclerRecentPlays.setAdapter(recentAdapter);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("Recent", "Lỗi: " + errorMessage);
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
    }

    private void loadUserProfile() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_profile", MODE_PRIVATE);
        String name = sharedPreferences.getString("KEY_NAME", "Hello!");
        String avatarBase64 = sharedPreferences.getString("KEY_AVATAR", "");

        txtUserName.setText(name);

        if (!avatarBase64.isEmpty()) {
            try {
                byte[] imageBytes = Base64.decode(avatarBase64, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                avatarHome.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            avatarHome.setImageResource(R.drawable.ic_avatar);
        }
    }
}
