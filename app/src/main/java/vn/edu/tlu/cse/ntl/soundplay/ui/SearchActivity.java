package vn.edu.tlu.cse.ntl.soundplay.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.cse.ntl.soundplay.R;
import vn.edu.tlu.cse.ntl.soundplay.adapter.MusicAdapter;
import vn.edu.tlu.cse.ntl.soundplay.adapter.PlaylistAdapter;
import vn.edu.tlu.cse.ntl.soundplay.data.model.Music;
import vn.edu.tlu.cse.ntl.soundplay.data.model.Playlist;
import vn.edu.tlu.cse.ntl.soundplay.data.repository.MusicRepository;

public class SearchActivity extends AppCompatActivity {

    private MusicRepository musicRepository;
    private MusicAdapter musicAdapter;
    private PlaylistAdapter playlistAdapter;
    private RecyclerView recyclerView;
    private EditText edtSearch;
    private TextView tvExplore;

    private ImageView homeIcon,libraryIcon, profileIcon;
    private boolean isSearching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        homeIcon = findViewById(R.id.home);
        libraryIcon = findViewById(R.id.library);
        profileIcon = findViewById(R.id.profile);

        recyclerView = findViewById(R.id.rvplaylist);
        edtSearch = findViewById(R.id.search_bar);
        tvExplore = findViewById(R.id.tvExplore);
        ImageView searchNavbar = findViewById(R.id.search); // navbar icon search

        musicRepository = new MusicRepository();
        playlistAdapter = new PlaylistAdapter(new ArrayList<>());
        musicAdapter = new MusicAdapter(new ArrayList<>());

        getTop100Music();

        // Ẩn dòng chữ "Khám phá nội dung mới" khi nhấn navbar tìm kiếm
        searchNavbar.setOnClickListener(v -> {
            tvExplore.setVisibility(View.GONE);
        });

        // Tìm kiếm bài hát
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString().trim();
                if (keyword.isEmpty()) {
                    isSearching = false;
                    tvExplore.setVisibility(View.VISIBLE); // hiện lại nếu xóa tìm kiếm
                    getTop100Music();
                } else {
                    isSearching = true;
                    tvExplore.setVisibility(View.GONE); // ẩn khi đang tìm kiếm
                    searchMusic(keyword);
                }
            }
        });

        // Về Trang chủ
        homeIcon.setOnClickListener(v -> {
            startActivity(new Intent(SearchActivity.this, HomeActivity.class));
            finish();
        });

        // Thư viện
        libraryIcon.setOnClickListener(v -> {
            startActivity(new Intent(SearchActivity.this, LibraryActivity.class));
            finish();
        });

        // Hồ sơ
        profileIcon.setOnClickListener(v -> {
            startActivity(new Intent(SearchActivity.this, ProfileActivity.class));
            finish();
        });
    }

    private void searchMusic(String keyword) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(musicAdapter);

        musicRepository.search(keyword, new MusicRepository.SearchCallback() {
            @Override
            public void onSearchCompleted(List<Music> musics) {
                musicAdapter.setData(musics);
            }

            @Override
            public void onSearchError(String error) {
                Log.e("MusicSearch", "Lỗi: " + error);
            }
        });
    }

    //đổ dữ liệu khi bấm vào tìm kiếm
    private void getTop100Music() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(playlistAdapter);

        musicRepository.getTop100(new MusicRepository.Top100Callback() {
            @Override
            public void onSuccess(List<Playlist> top100List) {
                for (Playlist playLists : top100List) {
                    Log.d("Top100", "ID: " + playLists.getId() + ", Tên bài hát: " + playLists.getTitle() + ", Ảnh: " + playLists.getThumbnail());
                }
                playlistAdapter.setData(top100List);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("Top100", "Lỗi: " + errorMessage);
            }
        });
    }

}
