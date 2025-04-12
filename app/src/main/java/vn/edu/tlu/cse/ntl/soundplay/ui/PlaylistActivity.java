package vn.edu.tlu.cse.ntl.soundplay.ui;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import vn.edu.tlu.cse.ntl.soundplay.R;
import vn.edu.tlu.cse.ntl.soundplay.adapter.PlaylistAdapter;
import vn.edu.tlu.cse.ntl.soundplay.data.model.Playlist;
import vn.edu.tlu.cse.ntl.soundplay.data.room.AppDatabase;

public class PlaylistActivity extends AppCompatActivity {

    private PlaylistAdapter adapter;
    private AppDatabase db;
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        ImageView imgSearch = findViewById(R.id.search);
        imgSearch.setOnClickListener(v -> {
            Intent intent = new Intent(PlaylistActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        RecyclerView rv = findViewById(R.id.rvPlaylist);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlaylistAdapter();
        rv.setAdapter(adapter);

        db = AppDatabase.getInstance(this);
        insertSampleData();

        db.playlistDAO().getAllPlaylists().observe(this, playlists -> {
            adapter.setData(playlists);
            if (playlists.isEmpty()) {

            }
        });
        playTestMusic();
    }
    private void playTestMusic() {
        String audioUrl = "https://vnso-pt-51-tf-a128-z3.zmdcdn.me/e56880dcdfdb114500a4cdc29fce8130?authen=exp=1744602899~acl=/e56880dcdfdb114500a4cdc29fce8130*~hmac=6412086eb138b25472bf5968c6a9186d";

        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(MediaPlayer::start); // phát luôn không báo gì
        } catch (Exception ignored) {
            // Bỏ qua lỗi
        }
    }


    private void insertSampleData() {
        new Thread(() -> {
            db.playlistDAO().insert(new Playlist("3:00am vibes", "18 songs", R.drawable.pictr_1));
            db.playlistDAO().insert(new Playlist("Top Hits", "24 songs", R.drawable.pictr_2));
            db.playlistDAO().insert(new Playlist("Chill Mood", "15 songs", R.drawable.pictr_3));
            db.playlistDAO().insert(new Playlist("Chill Mood", "15 songs", R.drawable.pictr_3));
            db.playlistDAO().insert(new Playlist("Chill Mood", "15 songs", R.drawable.pictr_3));
            db.playlistDAO().insert(new Playlist("Chill Mood", "15 songs", R.drawable.pictr_3));
        }).start();
    }
}
