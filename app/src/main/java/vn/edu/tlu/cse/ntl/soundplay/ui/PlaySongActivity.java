package vn.edu.tlu.cse.ntl.soundplay.ui;

import android.media.MediaPlayer;
import android.os.Bundle;
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

        // Phát nhạc
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare(); // hoặc prepareAsync nếu bạn muốn dùng loading
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể phát nhạc", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
