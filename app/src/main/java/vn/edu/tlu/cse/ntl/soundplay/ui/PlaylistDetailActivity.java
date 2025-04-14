package vn.edu.tlu.cse.ntl.soundplay.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.edu.tlu.cse.ntl.soundplay.R;
import vn.edu.tlu.cse.ntl.soundplay.adapter.MusicAdapter;
import vn.edu.tlu.cse.ntl.soundplay.data.model.Music;
import vn.edu.tlu.cse.ntl.soundplay.data.repository.MusicRepository;

public class PlaylistDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerPlaylistSongs;
    private MusicAdapter musicAdapter;
    private MusicRepository musicRepository;
    private TextView tvPlaylistName;
    private ImageView imgPlaylist;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);


        // Khởi tạo RecyclerView và Adapter
        recyclerPlaylistSongs = findViewById(R.id.rvSongList);
        recyclerPlaylistSongs.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Adapter rỗng ban đầu
        musicAdapter = new MusicAdapter(null);
        recyclerPlaylistSongs.setAdapter(musicAdapter);

        // Nhận thông tin từ Intent
        String playlistId = getIntent().getStringExtra("playlistId");
        String playlistName = getIntent().getStringExtra("playlistName");
        String playlistThumbnail = getIntent().getStringExtra("playlistThumbnail");

        // Khởi tạo các View cho tên playlist và ảnh playlist
        tvPlaylistName = findViewById(R.id.tvPlaylistName);
        imgPlaylist = findViewById(R.id.imgPlaylist);

        // Cập nhật UI với các thông tin nhận được
        if (playlistName != null) {
            tvPlaylistName.setText(playlistName);
        }

        if (playlistThumbnail != null) {
            Glide.with(this)
                    .load(playlistThumbnail)
                    .into(imgPlaylist);
        }

        if (playlistId != null) {
            Log.d("PlaylistDetail", "Playlist ID nhận được: " + playlistId);
            musicRepository = new MusicRepository();
            loadPlaylistSongs(playlistId);
        } else {
            Log.e("PlaylistDetail", "Không có playlistId truyền vào Intent");
        }
    }

    // Hàm tải bài hát trong playlist
    private void loadPlaylistSongs(String playlistId) {
        musicRepository.getPlayList(playlistId, new MusicRepository.PlayListCallback() {
            @Override
            public void onSuccess(List<Music> playList) {
                Log.d("PlaylistDetail", "Số bài hát: " + playList.size());
                // Cập nhật dữ liệu cho Adapter
                musicAdapter.setData(playList);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("PlaylistDetail", "Lỗi khi tải playlist: " + errorMessage);
            }
        });
    }
}
