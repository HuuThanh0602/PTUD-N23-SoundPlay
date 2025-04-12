package vn.edu.tlu.cse.ntl.soundplay.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vn.edu.tlu.cse.ntl.soundplay.R;
import vn.edu.tlu.cse.ntl.soundplay.adapter.CategoryAdapter;
import vn.edu.tlu.cse.ntl.soundplay.adapter.PlaylistAdapter;
import vn.edu.tlu.cse.ntl.soundplay.data.model.Playlist;
import vn.edu.tlu.cse.ntl.soundplay.data.room.AppDatabase;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView rvSearchResults;
    private EditText searchBar;

    private CategoryAdapter categoryAdapter;
    private PlaylistAdapter playlistAdapter;

    private List<String> categoryList;
    private List<Playlist> allPlaylists = new ArrayList<>();

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchBar = findViewById(R.id.search_bar);
        rvSearchResults = findViewById(R.id.rvCategories); // Sử dụng 1 RecyclerView duy nhất

        setupRecyclerView();
        showCategories(); // Mặc định hiển thị danh sách thể loại

        db = AppDatabase.getInstance(this);
        loadPlaylistFromRoom(); // Lấy dữ liệu thực từ Room

        // Bắt sự kiện nhập tìm kiếm
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    // Khi không có gì nhập vào, hiển thị danh sách thể loại
                    showCategories();
                } else {
                    // Khi có từ khóa tìm kiếm, hiển thị danh sách playlist
                    showPlaylists();
                    filterPlaylists(query);
                }
                setupRecyclerView();  // Cập nhật layout của RecyclerView tùy theo tìm kiếm
            }
        });
    }

    // Thiết lập RecyclerView chung cho thể loại và playlist
    private void setupRecyclerView() {
        if (searchBar.getText().toString().trim().isEmpty()) {
            rvSearchResults.setLayoutManager(new GridLayoutManager(this, 2)); // 2 cột cho thể loại
        } else {
            rvSearchResults.setLayoutManager(new GridLayoutManager(this, 1)); // 1 cột cho playlist
        }
    }

    // Lấy dữ liệu playlist từ Room
    private void loadPlaylistFromRoom() {
        db.playlistDAO().getAllPlaylists().observe(this, new Observer<List<Playlist>>() {
            @Override
            public void onChanged(List<Playlist> playlists) {
                allPlaylists = playlists;
                if (playlistAdapter == null) {
                    playlistAdapter = new PlaylistAdapter(); // Khởi tạo adapter nếu chưa có
                }
                playlistAdapter.setData(playlists); // Hiển thị toàn bộ playlist khi mới vào
            }
        });
    }

    // Lọc playlist theo từ khóa tìm kiếm
    private void filterPlaylists(String query) {
        List<Playlist> filtered = new ArrayList<>();
        for (Playlist p : allPlaylists) {
            if (p.title.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(p);
            }
        }
        playlistAdapter.setData(filtered); // Cập nhật danh sách playlist đã lọc
    }

    // Hiển thị danh sách thể loại
    private void showCategories() {
        if (categoryAdapter == null) {
            categoryList = new ArrayList<>(Arrays.asList("Kpop", "Pop", "Rock", "Rap", "Jazz", "Ballad"));
            categoryAdapter = new CategoryAdapter(this, categoryList);
        }
        rvSearchResults.setAdapter(categoryAdapter); // Cập nhật adapter với danh sách thể loại
    }

    // Hiển thị danh sách playlist
    private void showPlaylists() {
        if (playlistAdapter == null) {
            playlistAdapter = new PlaylistAdapter(); // Khởi tạo adapter nếu chưa có
        }
        rvSearchResults.setAdapter(playlistAdapter); // Cập nhật adapter với danh sách playlist
    }
}
