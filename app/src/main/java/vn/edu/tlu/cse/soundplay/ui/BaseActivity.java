package vn.edu.tlu.cse.soundplay.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import vn.edu.tlu.cse.soundplay.R;
import vn.edu.tlu.cse.soundplay.service.MusicService;

public abstract class BaseActivity extends AppCompatActivity {

    protected LinearLayout miniPlayer;
    protected ImageView imgCover, btnPlayPause, btnPrev, btnNext;
    protected TextView txtTitle, txtArtist;
    protected BroadcastReceiver miniPlayerReceiver;
    protected boolean isPlaying = false;
    protected String currentSongUrl;
    protected String currentSongId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initMiniPlayer() {
        // Khởi tạo các view của mini player
        miniPlayer = findViewById(R.id.mini_player);
        imgCover = findViewById(R.id.img_cover);
        txtTitle = findViewById(R.id.txt_title);
        txtArtist = findViewById(R.id.txt_artist);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        btnPrev = findViewById(R.id.btn_prev);
        btnNext = findViewById(R.id.btn_next);

        if (miniPlayer == null) {
            Log.w("BaseActivity", "Mini player layout not found");
            return; // Nếu không có mini player trong layout, bỏ qua
        }

        // Ẩn mini player ngay lập tức
        miniPlayer.setVisibility(View.GONE);
        Log.d("BaseActivity", "Mini player initialized and hidden");

        // Sự kiện click cho mini player
        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnPrev.setOnClickListener(v -> controlMusic(MusicService.ACTION_PREV));
        btnNext.setOnClickListener(v -> controlMusic(MusicService.ACTION_NEXT));
        miniPlayer.setOnClickListener(v -> {
            if (currentSongUrl != null && !currentSongUrl.isEmpty()) {
                Intent intent = new Intent(BaseActivity.this, PlaySongActivity.class);
                intent.putExtra("MUSIC_TITLE", txtTitle.getText().toString());
                intent.putExtra("MUSIC_THUMBNAIL", imgCover.getTag() != null ? imgCover.getTag().toString() : "");
                intent.putExtra("MUSIC_URL", currentSongUrl);
                intent.putExtra("MUSIC_ID", currentSongId);
                intent.putExtra("MUSIC_ARTIST", txtArtist.getText().toString());
                Log.d("BaseActivity", "Mini player clicked, songId: " + currentSongId);
                startActivity(intent);
            } else {
                Log.w("BaseActivity", "Cannot start PlaySongActivity: Invalid song data");
            }
        });

        // Đăng ký BroadcastReceiver
        registerMiniPlayerReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Trì hoãn gửi CHECK_STATE để đảm bảo view được khởi tạo
        if (miniPlayer != null) {
            miniPlayer.post(() -> {
                Intent checkIntent = new Intent(this, MusicService.class);
                checkIntent.setAction("CHECK_STATE");
                startService(checkIntent);
                Log.d("BaseActivity", "onStart: Sent CHECK_STATE to MusicService");
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (miniPlayerReceiver != null) {
            try {
                unregisterReceiver(miniPlayerReceiver);
                Log.d("BaseActivity", "MiniPlayerReceiver unregistered");
            } catch (Exception e) {
                Log.e("BaseActivity", "Error unregistering receiver: " + e.getMessage());
            }
        }
    }

    protected void togglePlayPause() {
        Intent serviceIntent = new Intent(this, MusicService.class);
        if (isPlaying) {
            serviceIntent.setAction(MusicService.ACTION_PAUSE);
            Log.d("BaseActivity", "Sending ACTION_PAUSE");
        } else {
            serviceIntent.setAction(MusicService.ACTION_RESUME);
            Log.d("BaseActivity", "Sending ACTION_RESUME");
        }
        startService(serviceIntent);
    }

    protected void controlMusic(String action) {
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.setAction(action);
        startService(serviceIntent);
        Log.d("BaseActivity", "Control music: " + action);
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    protected void registerMiniPlayerReceiver() {
        IntentFilter filter = new IntentFilter("UPDATE_UI");
        miniPlayerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("BaseActivity", "MiniPlayerReceiver received: " + intent);
                String songTitle = intent.getStringExtra("title");
                String songArtist = intent.getStringExtra("artist");
                String songThumbnail = intent.getStringExtra("thumbnail");
                String songId = intent.getStringExtra("songId");
                String songUrl = intent.getStringExtra("url");
                String action = intent.getStringExtra("action");

                Log.d("BaseActivity", "Received songId: " + songId);
                
                // Kiểm tra dữ liệu hợp lệ trước khi hiển thị
                if (songTitle != null && !songTitle.isEmpty() && !songTitle.contains("Lỗi") &&
                        songUrl != null && !songUrl.isEmpty()) {
                    txtTitle.setText(songTitle);
                    miniPlayer.setVisibility(View.VISIBLE);
                    Log.d("mini_player", "Mini player shown with title: " + songTitle);
                } else {
                    miniPlayer.setVisibility(View.GONE);
                    Log.d("mini_player", "Mini player hidden due to invalid data");
                    return;
                }

                // Cập nhật nghệ sĩ
                if (songArtist != null && !songArtist.isEmpty()) {
                    txtArtist.setText(songArtist);
                } else {
                    txtArtist.setText("Unknown Artist");
                }

                // Cập nhật ảnh bìa
                if (songThumbnail != null && !songThumbnail.isEmpty()) {
                    Glide.with(BaseActivity.this)
                            .load(songThumbnail)
                            .placeholder(R.drawable.ic_music)
                            .error(R.drawable.ic_music)
                            .into(imgCover);
                    imgCover.setTag(songThumbnail);
                } else {
                    imgCover.setImageResource(R.drawable.ic_music);
                }

                // Lưu thông tin bài hát
                currentSongUrl = songUrl;
                currentSongId = songId;
                Log.d("BaseActivity", "Updated currentSongId: " + currentSongId);

                // Cập nhật trạng thái play/pause
                if (MusicService.ACTION_PLAY.equals(action) || MusicService.ACTION_RESUME.equals(action)) {
                    btnPlayPause.setImageResource(R.drawable.ic_pause);
                    isPlaying = true;
                    Log.d("BaseActivity", "Mini player updated to PLAY/RESUME state");
                } else if (MusicService.ACTION_PAUSE.equals(action)) {
                    btnPlayPause.setImageResource(R.drawable.ic_play);
                    isPlaying = false;
                    Log.d("BaseActivity", "Mini player updated to PAUSE state");
                }
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(miniPlayerReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(miniPlayerReceiver, filter);
        }
        Log.d("BaseActivity", "MiniPlayerReceiver registered");
    }
}