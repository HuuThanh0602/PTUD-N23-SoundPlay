package vn.edu.tlu.cse.hyn.soundplay.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.tlu.cse.hyn.soundplay.R;
import vn.edu.tlu.cse.hyn.soundplay.adapter.MixAdapter;
import vn.edu.tlu.cse.hyn.soundplay.adapter.MusicAdapter;
import vn.edu.tlu.cse.hyn.soundplay.adapter.RecentAdapter;
import vn.edu.tlu.cse.hyn.soundplay.data.model.MusicItem;

public class MusicHomeActivity extends AppCompatActivity {

    RecyclerView recyclerRecent, recyclerForYou, recyclerRecentPlays;
    LinearLayout navProfile;
    CircleImageView avatarHome;  // ✅ Đúng kiểu bo tròn
    TextView txtUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Ánh xạ view
        recyclerRecent = findViewById(R.id.recyclerRecent);
        recyclerForYou = findViewById(R.id.recyclerForYou);
        recyclerRecentPlays = findViewById(R.id.recyclerRecentPlays);
        navProfile = findViewById(R.id.navProfile);
        avatarHome = findViewById(R.id.avatarHome);  // ✅ Sử dụng CircleImageView
        txtUserName = findViewById(R.id.txtUserName);

        // Load dữ liệu SharedPreferences lần đầu
        loadUserProfile();

        // Danh sách mẫu
        List<MusicItem> ngheLaiList = new ArrayList<>();
        ngheLaiList.add(new MusicItem("Nhạc buồn", R.drawable.nhacbuon));
        ngheLaiList.add(new MusicItem("SAY HI", R.drawable.sayhi));
        ngheLaiList.add(new MusicItem("Tuyển tập của HIEUTHUHAI", R.drawable.hieuthuhai));
        ngheLaiList.add(new MusicItem("V- POP không thể thiếu", R.drawable.vpop));
        ngheLaiList.add(new MusicItem("Âm hưởng dân gian", R.drawable.dangian));
        ngheLaiList.add(new MusicItem("LOFI", R.drawable.lofi));

        List<MusicItem> danhChoBanList = new ArrayList<>();
        danhChoBanList.add(new MusicItem("Pop Mix", R.drawable.popmix));
        danhChoBanList.add(new MusicItem("Chill Mix", R.drawable.chillmix));
        danhChoBanList.add(new MusicItem("K- Pop Mix", R.drawable.kpopmix));

        List<MusicItem> ganDayList = new ArrayList<>();
        ganDayList.add(new MusicItem("", R.drawable.img_recent1));
        ganDayList.add(new MusicItem("", R.drawable.img_recent2));
        ganDayList.add(new MusicItem("", R.drawable.img_recent3));

        recyclerRecent.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerRecent.setAdapter(new MusicAdapter(ngheLaiList));

        recyclerForYou.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerForYou.setAdapter(new MixAdapter(danhChoBanList));

        recyclerRecentPlays.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerRecentPlays.setAdapter(new RecentAdapter(ganDayList));

        // Chuyển sang Hồ sơ
        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MusicHomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Mỗi khi quay lại, cập nhật lại avatar và tên
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
            avatarHome.setImageResource(R.drawable.ic_avatar); // ảnh mặc định nếu chưa có
        }
    }
}
