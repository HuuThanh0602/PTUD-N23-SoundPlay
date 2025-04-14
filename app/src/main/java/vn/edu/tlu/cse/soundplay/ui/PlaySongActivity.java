package vn.edu.tlu.cse.soundplay.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import vn.edu.tlu.cse.soundplay.R;
import vn.edu.tlu.cse.soundplay.service.MusicService;

public class PlaySongActivity extends AppCompatActivity {

    private TextView txtSongTitle, txtCurrentTime, txtTotalTime;
    private ImageView imgSongThumbnail, btnShuffle;
    private SeekBar seekBar;
    private ImageView btnBack, btnFavorite, btnBackPre, btnPlay, btnNextPre;
    private BroadcastReceiver uiUpdateReceiver;
    private boolean isPlaying = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private String currentSongUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playsong);
        Log.d("PlaySongActivity", "onCreate called");

        // Khởi tạo các view
        txtSongTitle = findViewById(R.id.txtSongTitle);
        txtCurrentTime = findViewById(R.id.txtCurrentTime); // Giả định ID
        txtTotalTime = findViewById(R.id.txtTotalTime); // Giả định ID
        imgSongThumbnail = findViewById(R.id.imgCover);
        seekBar = findViewById(R.id.seekBar);
        btnBack = findViewById(R.id.btnBack1);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnBackPre = findViewById(R.id.btnBackPre);
        btnPlay = findViewById(R.id.btnPlay);
        btnNextPre = findViewById(R.id.btnNextPre);
        btnShuffle = findViewById(R.id.ic_shuffle);

        // Lấy thông tin từ Intent
        String songTitle = getIntent().getStringExtra("MUSIC_TITLE");
        String songThumbnail = getIntent().getStringExtra("MUSIC_THUMBNAIL");
        currentSongUrl = getIntent().getStringExtra("MUSIC_URL");
        Log.d("PlaySongActivity", "Received: Title=" + songTitle + ", Thumbnail=" + songThumbnail + ", URL=" + currentSongUrl);

        // Cập nhật UI ban đầu
        if (songTitle != null && !songTitle.isEmpty() && !songTitle.contains("Lỗi")) {
            txtSongTitle.setText(songTitle);
        } else {
            txtSongTitle.setText("Không có tiêu đề");
            Log.w("PlaySongActivity", "Song title is null or empty");
        }

        if (songThumbnail != null && !songThumbnail.isEmpty()) {
            Glide.with(this)
                    .load(songThumbnail)
                    .placeholder(R.drawable.ic_music)
                    .error(R.drawable.ic_music)
                    .into(imgSongThumbnail);
            imgSongThumbnail.setTag(songThumbnail);
        } else {
            Log.w("PlaySongActivity", "Song thumbnail is null or empty");
            imgSongThumbnail.setImageResource(R.drawable.ic_music);
        }

        // Khởi tạo thời gian ban đầu
        txtCurrentTime.setText("0:00");
        txtTotalTime.setText("0:00");

        // Gọi MusicService nếu có URL hợp lệ
        if (currentSongUrl != null && !currentSongUrl.isEmpty()) {
            startMusicService(currentSongUrl, songTitle, songThumbnail);
        } else {
            Log.e("PlaySongActivity", "Song URL is null or empty");
            txtSongTitle.setText("Lỗi: Không có bài hát để phát");
            btnPlay.setEnabled(false);
        }

        // Thiết lập sự kiện
        btnBack.setOnClickListener(v -> {
            Log.d("PlaySongActivity", "Back button clicked");
            finish();
        });

        btnFavorite.setOnClickListener(v -> {
            Log.d("PlaySongActivity", "Favorite button clicked");
            toggleFavorite();
        });

        btnBackPre.setOnClickListener(v -> {
            Log.d("PlaySongActivity", "Previous button clicked");
            controlMusic(MusicService.ACTION_PREV);
        });

        btnPlay.setOnClickListener(v -> {
            Log.d("PlaySongActivity", "Play/Pause button clicked");
            togglePlayPause();
        });

        btnNextPre.setOnClickListener(v -> {
            Log.d("PlaySongActivity", "Next button clicked");
            controlMusic(MusicService.ACTION_NEXT);
        });

        btnShuffle.setOnClickListener(v -> {
            Log.d("PlaySongActivity", "Shuffle button clicked");
            controlMusic(MusicService.ACTION_SHUFFLE);
        });

        // SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Log.d("PlaySongActivity", "Seeking to: " + progress);
                    controlMusic(MusicService.ACTION_SEEK, progress);
                    // Cập nhật thời gian hiện tại ngay khi tua
                    txtCurrentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("PlaySongActivity", "SeekBar touch started");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("PlaySongActivity", "SeekBar touch stopped");
            }
        });

        // Đăng ký BroadcastReceiver
        registerReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Kiểm tra trạng thái MusicService khi activity khởi động hoặc quay lại
        Intent checkIntent = new Intent(this, MusicService.class);
        checkIntent.setAction("CHECK_STATE");
        startService(checkIntent);
        Log.d("PlaySongActivity", "Sent CHECK_STATE to MusicService");
    }

    private void startMusicService(String songUrl, String songTitle, String songThumbnail) {
        Log.d("PlaySongActivity", "Starting MusicService with URL: " + songUrl + ", Title: " + songTitle);
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.setAction(MusicService.ACTION_PLAY);
        serviceIntent.putExtra("url", songUrl);
        serviceIntent.putExtra("title", songTitle);
        serviceIntent.putExtra("thumbnail", songThumbnail);
        startService(serviceIntent);
    }

    private void controlMusic(String action) {
        Log.d("PlaySongActivity", "Control music: " + action);
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.setAction(action);
        startService(serviceIntent);
    }

    private void controlMusic(String action, int progress) {
        Log.d("PlaySongActivity", "Control music: " + action + ", progress: " + progress);
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.setAction(action);
        serviceIntent.putExtra("progress", progress);
        startService(serviceIntent);
    }

    private void togglePlayPause() {
        Intent serviceIntent = new Intent(this, MusicService.class);
        if (isPlaying) {
            serviceIntent.setAction(MusicService.ACTION_PAUSE);
            Log.d("PlaySongActivity", "Sending ACTION_PAUSE");
        } else {
            serviceIntent.setAction(MusicService.ACTION_RESUME);
            Log.d("PlaySongActivity", "Sending ACTION_RESUME");
        }
        startService(serviceIntent);
    }

    private void toggleFavorite() {
        Log.d("PlaySongActivity", "Toggling favorite");
        // TODO: Cập nhật trạng thái yêu thích
    }

    private String formatTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter("UPDATE_UI");
        uiUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("PlaySongActivity", "Received broadcast: " + intent);
                String songTitle = intent.getStringExtra("title");
                String songThumbnail = intent.getStringExtra("thumbnail");
                String action = intent.getStringExtra("action");
                int duration = intent.getIntExtra("duration", 0);
                int currentPosition = intent.getIntExtra("currentPosition", 0);

                // Cập nhật tiêu đề nếu hợp lệ
                if (songTitle != null && !songTitle.isEmpty() && !songTitle.contains("Lỗi")) {
                    txtSongTitle.setText(songTitle);
                    Log.d("PlaySongActivity", "Updated title: " + songTitle);
                } else {
                    Log.w("PlaySongActivity", "Invalid title received: " + songTitle);
                }

                if (songThumbnail != null && !songThumbnail.isEmpty()) {
                    Glide.with(PlaySongActivity.this)
                            .load(songThumbnail)
                            .placeholder(R.drawable.ic_music)
                            .error(R.drawable.ic_music)
                            .into(imgSongThumbnail);
                    imgSongThumbnail.setTag(songThumbnail);
                    Log.d("PlaySongActivity", "Updated thumbnail: " + songThumbnail);
                }

                // Cập nhật thời gian và SeekBar
                if (duration > 0) {
                    seekBar.setMax(duration);
                    txtTotalTime.setText(formatTime(duration));
                    Log.d("PlaySongActivity", "Set SeekBar max and total time: " + duration);
                }
                seekBar.setProgress(currentPosition);
                txtCurrentTime.setText(formatTime(currentPosition));
                Log.d("PlaySongActivity", "Set SeekBar progress and current time: " + currentPosition);

                // Cập nhật trạng thái play/pause
                if (MusicService.ACTION_PLAY.equals(action) || MusicService.ACTION_RESUME.equals(action)) {
                    btnPlay.setImageResource(R.drawable.ic_pause);
                    isPlaying = true;
                    btnPlay.setEnabled(true);
                    Log.d("PlaySongActivity", "UI updated to PLAY/RESUME state");
                } else if (MusicService.ACTION_PAUSE.equals(action)) {
                    btnPlay.setImageResource(R.drawable.ic_play);
                    isPlaying = false;
                    btnPlay.setEnabled(true);
                    Log.d("PlaySongActivity", "UI updated to PAUSE state");
                }
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(uiUpdateReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(uiUpdateReceiver, filter);
        }
        Log.d("PlaySongActivity", "BroadcastReceiver registered");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("PlaySongActivity", "onDestroy called");
        if (uiUpdateReceiver != null) {
            try {
                unregisterReceiver(uiUpdateReceiver);
                Log.d("PlaySongActivity", "BroadcastReceiver unregistered");
            } catch (Exception e) {
                Log.e("PlaySongActivity", "Error unregistering receiver: " + e.getMessage());
            }
        }
        // Gửi yêu cầu cập nhật trạng thái MusicService khi thoát
        Intent checkIntent = new Intent(this, MusicService.class);
        checkIntent.setAction("CHECK_STATE");
        startService(checkIntent);
        Log.d("PlaySongActivity", "Sent CHECK_STATE to MusicService");
    }
}