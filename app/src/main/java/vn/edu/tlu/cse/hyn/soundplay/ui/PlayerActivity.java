package vn.edu.tlu.cse.hyn.soundplay.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import android.os.Handler;
import android.os.Looper;


import vn.edu.tlu.cse.hyn.soundplay.R;

public class PlayerActivity extends AppCompatActivity {

    private ImageView btnBack1, btnPlay, btnBackPre, btnNextPre, btnFavorite, btnShare;
    private SeekBar seekBar;
    private TextView txtStart, txtEnd, txtSongTitle, txtArtist, txtLyrics;

    private MediaPlayer mediaPlayer;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private boolean isPlaying = false;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        // Ánh xạ view
        btnBack1 = findViewById(R.id.btnBack1);
        btnPlay = findViewById(R.id.btnPlay);
        btnBackPre = findViewById(R.id.btnBackPre);
        btnNextPre = findViewById(R.id.btnNextPre);
        btnFavorite = findViewById(R.id.btnFavorite);
        //btnShare = findViewById(R.id.btnShare);
        seekBar = findViewById(R.id.seekBar);
        txtStart = findViewById(R.id.txtStart);
        txtEnd = findViewById(R.id.txtEnd);
        txtSongTitle = findViewById(R.id.txtSongTitle);
        txtArtist = findViewById(R.id.txtArtist);
        txtLyrics = findViewById(R.id.txtLyrics);

        // Set bài hát mẫu
        txtSongTitle.setText("1000 ánh mắt");
        txtArtist.setText("Obito");
        txtLyrics.setText("Thật ra anh yêu rồi\\n1000 ánh mắt trông đợi\\nTình cờ mình gặp gỡ lúc ta chào nhau\\nThật lòng này là đã chết trước em từ lâu\\nThật ra anh yêu rồi\\n1000 ánh mắt trông đợi\\nTìm lại được cảm giác lãng quên từ lâu\\nVà giờ thì chỉ mỗi em là đậm sâu");

        mediaPlayer = MediaPlayer.create(this, R.raw.sample_song); // Thêm sample_song.mp3 vào thư mục res/raw

        seekBar.setMax(mediaPlayer.getDuration());
        txtEnd.setText(millisecondsToTime(mediaPlayer.getDuration()));

        // Cập nhật tiến độ
        handler.postDelayed(updateSeekBar, 1000);

        // Nút play/pause
        btnPlay.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlay.setImageResource(R.drawable.ic_play);
                isPlaying = false;
            } else {
                mediaPlayer.start();
                btnPlay.setImageResource(R.drawable.ic_pause);
                isPlaying = true;
            }
        });

        // Nút back
        btnBack1.setOnClickListener(v -> finish());

        // SeekBar lướt
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) mediaPlayer.seekTo(progress);
                txtStart.setText(millisecondsToTime(progress));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Nút yêu thích
        btnFavorite.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            btnFavorite.setImageResource(isFavorite ? R.drawable.ic_heart_filled : R.drawable.ic_heart);
        });

        // Chia sẻ bài hát
        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this song: grainy days by moody.");
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });
    }

    // Cập nhật SeekBar mỗi giây
    private Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && isPlaying) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                txtStart.setText(millisecondsToTime(mediaPlayer.getCurrentPosition()));
                handler.postDelayed(this, 1000);
            }
        }
    };

    // Đổi mili giây thành phút:giây
    private String millisecondsToTime(int millis) {
        int minutes = (millis / 1000) / 60;
        int seconds = (millis / 1000) % 60;
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            handler.removeCallbacks(updateSeekBar);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}

