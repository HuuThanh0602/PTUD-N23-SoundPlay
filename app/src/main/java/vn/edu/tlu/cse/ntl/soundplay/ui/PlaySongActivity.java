package vn.edu.tlu.cse.ntl.soundplay.ui;

import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.IOException;

import vn.edu.tlu.cse.ntl.soundplay.R;

public class PlaySongActivity extends AppCompatActivity {

    private TextView txtSongTitle, txtArtist;
    private ImageView imgCover;
    private MediaPlayer mediaPlayer;
    private ObjectAnimator rotationAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playsong);

        txtSongTitle = findViewById(R.id.txtSongTitle);
        txtArtist = findViewById(R.id.txtArtist);
        imgCover = findViewById(R.id.imgCover);

        // Nhận dữ liệu từ Intent
        String title = getIntent().getStringExtra("TITLE");
        String artist = getIntent().getStringExtra("ARTIST");
        String url = getIntent().getStringExtra("URL");
        String thumbnail = getIntent().getStringExtra("THUMBNAIL");

        txtSongTitle.setText(title);
        txtArtist.setText(artist);
        Glide.with(this).load(thumbnail).into(imgCover);

        // Tạo hiệu ứng xoay ảnh
        rotationAnimator = ObjectAnimator.ofFloat(imgCover, "rotation", 0f, 360f);
        rotationAnimator.setDuration(10000); // Quay 1 vòng trong 10 giây
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE); // Lặp vô hạn
        rotationAnimator.setInterpolator(new LinearInterpolator());

        // Phát nhạc
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare(); // hoặc dùng prepareAsync nếu muốn loading mượt hơn
            mediaPlayer.start();
            rotationAnimator.start(); // Bắt đầu xoay ảnh khi phát nhạc
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể phát nhạc", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Giải phóng media player
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Dừng xoay ảnh
        if (rotationAnimator != null) {
            rotationAnimator.cancel();
        }
    }
}
