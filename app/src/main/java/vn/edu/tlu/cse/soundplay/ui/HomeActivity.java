package vn.edu.tlu.cse.soundplay.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.tlu.cse.soundplay.R;
import vn.edu.tlu.cse.soundplay.adapter.LastestAdapter;
import vn.edu.tlu.cse.soundplay.adapter.MusicAdapter;
import vn.edu.tlu.cse.soundplay.adapter.YouAdapter;
import vn.edu.tlu.cse.soundplay.data.model.Music;
import vn.edu.tlu.cse.soundplay.data.model.Playlist;
import vn.edu.tlu.cse.soundplay.data.repository.MusicRepository;

public class HomeActivity extends BaseActivity {

    private RecyclerView recyclerLastest, recyclerForYou, recyclerRecentPlays;
    private CircleImageView avatarHome;
    private TextView txtUserName;
    private MusicRepository musicRepository;
    private MusicAdapter musicAdapter;
    private ImageView searchIcon, libraryIcon, profileIcon;


    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate called");

        // Khởi tạo MusicRepository với Context
        musicRepository = new MusicRepository(this);

        // Khởi tạo các view
        initializeViews();

        // Khởi tạo mini player từ BaseActivity
        initMiniPlayer();

        // Load hồ sơ người dùng
        loadUserProfile();

        // Thiết lập RecyclerViews
        setupRecyclerViews();

        // Thiết lập sự kiện click
        setupClickListeners();
    }

    private void initializeViews() {
        searchIcon = findViewById(R.id.search);
        libraryIcon = findViewById(R.id.library);
        profileIcon = findViewById(R.id.profile);
        recyclerLastest = findViewById(R.id.recyclerLastest);
        recyclerForYou = findViewById(R.id.recyclerForYou);
        recyclerRecentPlays = findViewById(R.id.recyclerRecentPlays);
        avatarHome = findViewById(R.id.avatarHome);
        txtUserName = findViewById(R.id.txtUserName);
    }

    private void setupRecyclerViews() {

        recyclerRecentPlays.setLayoutManager(new GridLayoutManager(this, 1));
        getRecentPlays();

        recyclerLastest.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.HORIZONTAL, false));
        getNewReleaseMusic();

        recyclerForYou.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        getTop100Music();


    }

    private void setupClickListeners() {
        searchIcon.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, SearchActivity.class));
            finish();
        });

        libraryIcon.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, LibraryActivity.class));
            finish();
        });

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void getNewReleaseMusic() {
        Log.d(TAG, "Fetching new release music");
        musicRepository.getNewReleaseChart(new MusicRepository.NewReleaseCallback() {
            @Override
            public void onSuccess(List<Music> newReleases) {
                LastestAdapter lastestAdapter = new LastestAdapter(newReleases,musicRepository);
                recyclerLastest.setAdapter(lastestAdapter);
                Log.d(TAG, "New releases loaded: " + newReleases.size() + " items");
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "New releases error: " + errorMessage);
            }
        });
    }

    private void getTop100Music() {
        Log.d(TAG, "Fetching top 100 music");
        musicRepository.getTop100(new MusicRepository.Top100Callback() {
            @Override
            public void onSuccess(List<Playlist> top100List) {
                YouAdapter youAdapter = new YouAdapter(top100List);
                recyclerForYou.setAdapter(youAdapter);
                Log.d(TAG, "Top 100 loaded: " + top100List.size() + " items");
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Top 100 error: " + errorMessage);
            }
        });
    }

    private void getRecentPlays() {
        musicRepository.getRecentPlays(new MusicRepository.RecentCallback() {
            @Override
            public void onSuccess(List<Music> musicList) {
                MusicAdapter adapter = new MusicAdapter(musicList,musicRepository);
                recyclerRecentPlays.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("RecentPlays", "Lỗi: " + errorMessage);
            }
        });
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
                Log.e(TAG, "Error loading avatar: " + e.getMessage());
                avatarHome.setImageResource(R.drawable.ic_avatar);
            }
        } else {
            avatarHome.setImageResource(R.drawable.ic_avatar);
        }
        Log.d(TAG, "User profile loaded: name=" + name);
    }
}