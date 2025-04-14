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

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.tlu.cse.soundplay.R;
import vn.edu.tlu.cse.soundplay.adapter.LastestAdapter;
import vn.edu.tlu.cse.soundplay.adapter.YouAdapter;
import vn.edu.tlu.cse.soundplay.data.model.Music;
import vn.edu.tlu.cse.soundplay.data.model.Playlist;
import vn.edu.tlu.cse.soundplay.data.repository.MusicRepository;
import vn.edu.tlu.cse.soundplay.service.MusicService;

public class HomeActivity extends BaseActivity {

    RecyclerView recyclerLastest, recyclerForYou, recyclerRecentPlays;
    CircleImageView avatarHome;
    private TextView txtUserName;
    private MusicRepository musicRepository;
    private ImageView searchIcon, libraryIcon, profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d("HomeActivity", "onCreate called");

        musicRepository = new MusicRepository();

        // Khởi tạo các view
        searchIcon = findViewById(R.id.search);
        libraryIcon = findViewById(R.id.library);
        profileIcon = findViewById(R.id.profile);
        recyclerLastest = findViewById(R.id.recyclerLastest);
        recyclerForYou = findViewById(R.id.recyclerForYou);
        recyclerRecentPlays = findViewById(R.id.recyclerRecentPlays);
        avatarHome = findViewById(R.id.avatarHome);
        txtUserName = findViewById(R.id.txtUserName);

        // Load hồ sơ người dùng
        loadUserProfile();

        // Thiết lập RecyclerView
        recyclerForYou.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        getTop100Music();

        recyclerLastest.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.HORIZONTAL, false));
        getNewReleaseMusic();

        // recyclerRecentPlays.setLayoutManager(new LinearLayoutManager(this));
        // getRecentPlays();

        // Sự kiện click
        searchIcon.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, SearchActivity.class));
            finish();
            Log.d("HomeActivity", "Search icon clicked");
        });

        // libraryIcon.setOnClickListener(v -> {
        //     startActivity(new Intent(HomeActivity.this, LibraryActivity.class));
        //     finish();
        // });

        // profileIcon.setOnClickListener(v -> {
        //     startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        // });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupMiniPlayer();
        Log.d("HomeActivity", "onStart: Checking MusicService state");
    }

    private void getTop100Music() {
        musicRepository.getTop100(new MusicRepository.Top100Callback() {
            @Override
            public void onSuccess(List<Playlist> top100List) {
                YouAdapter youAdapter = new YouAdapter(top100List);
                recyclerForYou.setAdapter(youAdapter);
                Log.d("HomeActivity", "Top 100 loaded: " + top100List.size() + " items");
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("HomeActivity", "Top 100 error: " + errorMessage);
            }
        });
    }

    private void getNewReleaseMusic() {
        musicRepository.getNewReleaseChart(new MusicRepository.NewReleaseCallback() {
            @Override
            public void onSuccess(List<Music> newReleases) {
                LastestAdapter lastestAdapter = new LastestAdapter(newReleases);
                recyclerLastest.setAdapter(lastestAdapter);
                Log.d("HomeActivity", "New releases loaded: " + newReleases.size() + " items");
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("HomeActivity", "New releases error: " + errorMessage);
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
                Log.e("HomeActivity", "Error loading avatar: " + e.getMessage());
            }
        } else {
            avatarHome.setImageResource(R.drawable.ic_avatar);
        }
        Log.d("HomeActivity", "User profile loaded: name=" + name);
    }
}