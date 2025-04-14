package vn.edu.tlu.cse.soundplay.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.cse.soundplay.R;
import vn.edu.tlu.cse.soundplay.adapter.MusicAdapter;
import vn.edu.tlu.cse.soundplay.data.model.Music;
import vn.edu.tlu.cse.soundplay.data.repository.MusicRepository;


public class LibraryActivity extends BaseActivity {
    private MusicRepository musicRepository;
    private MusicAdapter musicAdapter;
    private RecyclerView  recyclerViewMusic;

    private ImageView searchIcon,homeIcon, profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        searchIcon = findViewById(R.id.search);
        homeIcon = findViewById(R.id.home);
        profileIcon = findViewById(R.id.profile);

        musicRepository = new MusicRepository(this);
        recyclerViewMusic = findViewById(R.id.rvmusic);

        recyclerViewMusic.setLayoutManager(new GridLayoutManager(this, 1));

        musicAdapter = new MusicAdapter(new ArrayList<>() ,musicRepository);
        recyclerViewMusic.setAdapter(musicAdapter);

        initMiniPlayer();

        initMiniPlayer();
        recyclerViewMusic.setLayoutManager(new LinearLayoutManager(this));
        getRecentPlays();


        // Tìm kiếm
        searchIcon.setOnClickListener(v -> {
            startActivity(new Intent(LibraryActivity.this, SearchActivity.class));
            finish();
        });

        // Trang chủ
        homeIcon.setOnClickListener(v -> {
            startActivity(new Intent(LibraryActivity.this, HomeActivity.class));
            finish();
        });

        // Hồ sơ
        profileIcon.setOnClickListener(v -> {
            startActivity(new Intent(LibraryActivity.this, ProfileActivity.class));
            finish();
        });

    }

    private void getRecentPlays() {
        musicRepository.getRecentPlays(new MusicRepository.RecentCallback() {
            @Override
            public void onSuccess(List<Music> musicList) {
                MusicAdapter adapter = new MusicAdapter(musicList,musicRepository);
                recyclerViewMusic.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("RecentPlays", "Lỗi: " + errorMessage);
            }
        });
    }
}