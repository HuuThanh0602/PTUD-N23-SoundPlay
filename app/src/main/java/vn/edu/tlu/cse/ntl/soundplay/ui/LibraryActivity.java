package vn.edu.tlu.cse.ntl.soundplay.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.edu.tlu.cse.ntl.soundplay.R;
import vn.edu.tlu.cse.ntl.soundplay.adapter.MusicAdapter;
import vn.edu.tlu.cse.ntl.soundplay.data.repository.MusicRepository;

public class LibraryActivity extends AppCompatActivity {
    private MusicRepository musicRepository;
    private MusicAdapter musicAdapter;
    private RecyclerView recyclerView;

    private ImageView searchIcon,homeIcon, profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        searchIcon = findViewById(R.id.search);
        homeIcon = findViewById(R.id.home);
        profileIcon = findViewById(R.id.profile);

        // Khởi tạo RecyclerView
        recyclerView = findViewById(R.id.rvmusic);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        musicAdapter = new MusicAdapter(new ArrayList<>());
        recyclerView.setAdapter(musicAdapter);

        // Khởi tạo MusicRepository
        musicRepository = new MusicRepository();

        //getTop100Music();

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

//    private void getTop100Music() {
//        musicRepository.getTop100(new MusicRepository.Top100Callback() {
//            @Override
//            public void onSuccess(List<Playlist> top100List) {
//                musicAdapter.setData(top100List);
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//                Log.e("Top100", "Lỗi: " + errorMessage);
//            }
//        });
//    }
}