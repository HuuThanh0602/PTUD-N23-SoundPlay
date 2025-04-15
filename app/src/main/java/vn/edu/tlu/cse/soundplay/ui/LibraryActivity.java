package vn.edu.tlu.cse.soundplay.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
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
    private RecyclerView recyclerViewMusic;
    private ImageView searchIcon, homeIcon, profileIcon, favourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        // Initialize views
        searchIcon = findViewById(R.id.search);
        homeIcon = findViewById(R.id.home);
        profileIcon = findViewById(R.id.profile);
        favourite = findViewById(R.id.favorite);
        recyclerViewMusic = findViewById(R.id.rvmusic);

        // Debug null views
        if (searchIcon == null) Log.e("LibraryActivity", "searchIcon is null");
        if (homeIcon == null) Log.e("LibraryActivity", "homeIcon is null");
        if (profileIcon == null) Log.e("LibraryActivity", "profileIcon is null");
        if (favourite == null) Log.e("LibraryActivity", "favourite is null");

        // Initialize repository and RecyclerView
        musicRepository = new MusicRepository(this);
        recyclerViewMusic.setLayoutManager(new LinearLayoutManager(this));
        musicAdapter = new MusicAdapter(new ArrayList<>(), musicRepository);
        recyclerViewMusic.setAdapter(musicAdapter);

        // Initialize mini player (called once)
        initMiniPlayer();

        // Fetch recent plays
        getRecentPlays();

        // Set click listeners with null checks
        if (searchIcon != null) {
            searchIcon.setOnClickListener(v -> {
                startActivity(new Intent(LibraryActivity.this, SearchActivity.class));
            });
        }

        if (favourite != null) {
            favourite.setOnClickListener(v -> {
                startActivity(new Intent(LibraryActivity.this, FavouriteActivity.class));
            });
        }

        if (homeIcon != null) {
            homeIcon.setOnClickListener(v -> {
                startActivity(new Intent(LibraryActivity.this, HomeActivity.class));
            });
        }

        if (profileIcon != null) {
            profileIcon.setOnClickListener(v -> {
                startActivity(new Intent(LibraryActivity.this, ProfileActivity.class));

            });
        }
    }

    private void getRecentPlays() {
        musicRepository.getRecentPlays(new MusicRepository.RecentCallback() {
            @Override
            public void onSuccess(List<Music> musicList) {
                musicAdapter = new MusicAdapter(musicList, musicRepository);
                recyclerViewMusic.setAdapter(musicAdapter);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("RecentPlays", "Error: " + errorMessage);
            }
        });
    }
}