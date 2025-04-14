package vn.edu.tlu.cse.soundplay.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import vn.edu.tlu.cse.soundplay.R;
import vn.edu.tlu.cse.soundplay.service.MusicService;

public class BaseActivity extends AppCompatActivity {
    LinearLayout miniPlayer;
    TextView txtTitle;
    ImageView btnPlayPause, imgCover;
    private BroadcastReceiver uiUpdateReceiver;

    @Override
    protected void onStart() {
        super.onStart();
        setupMiniPlayer();
        registerReceiver();
        // Kiểm tra trạng thái MusicService khi activity khởi động
        Intent checkIntent = new Intent(this, MusicService.class);
        checkIntent.setAction("CHECK_STATE");
        startService(checkIntent);
        Log.d("BaseActivity", "Sent CHECK_STATE to MusicService");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (uiUpdateReceiver != null) {
            try {
                unregisterReceiver(uiUpdateReceiver);
                Log.d("BaseActivity", "BroadcastReceiver unregistered");
            } catch (Exception e) {
                Log.e("BaseActivity", "Error unregistering receiver: " + e.getMessage());
            }
        }
    }

    protected void setupMiniPlayer() {
        miniPlayer = findViewById(R.id.mini_player);
        txtTitle = findViewById(R.id.txt_title);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        imgCover = findViewById(R.id.img_cover);

        if (miniPlayer == null || txtTitle == null || btnPlayPause == null || imgCover == null) {
            Log.w("BaseActivity", "Mini player views not found");
            return;
        }

        // Khi mini player được nhấn, chuyển đến PlaySongActivity
        miniPlayer.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlaySongActivity.class);
            intent.putExtra("MUSIC_TITLE", txtTitle.getText().toString());
            intent.putExtra("MUSIC_THUMBNAIL", imgCover.getTag() != null ? imgCover.getTag().toString() : "");
            intent.putExtra("MUSIC_URL", "");
            startActivity(intent);
            Log.d("BaseActivity", "Mini player clicked, starting PlaySongActivity");
        });

        // Khi nút play/pause được nhấn, gửi action tới MusicService
        btnPlayPause.setOnClickListener(v -> {
            Intent intent = new Intent(this, MusicService.class);
            String currentAction = (String) btnPlayPause.getTag();
            if ("playing".equals(currentAction)) {
                intent.setAction(MusicService.ACTION_PAUSE);
            } else {
                intent.setAction(MusicService.ACTION_RESUME);
            }
            startService(intent);
            Log.d("BaseActivity", "Play/Pause button clicked, action: " + intent.getAction());
        });
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerReceiver() {
        uiUpdateReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("BaseActivity", "Received broadcast: " + intent);
                String songTitle = intent.getStringExtra("title");
                String thumbnail = intent.getStringExtra("thumbnail");
                String action = intent.getStringExtra("action");

                boolean isValidTitle = songTitle != null && !songTitle.isEmpty() && !songTitle.contains("Lỗi");
                boolean isValidThumbnail = thumbnail != null && !thumbnail.isEmpty();

                if (isValidTitle) {
                    txtTitle.setText(songTitle);
                    Log.d("BaseActivity", "Updated title: " + songTitle);
                } else {
                    Log.w("BaseActivity", "Invalid title received: " + songTitle);
                }

                if (isValidThumbnail) {
                    Glide.with(BaseActivity.this)
                            .load(thumbnail)
                            .placeholder(R.drawable.ic_music)
                            .error(R.drawable.ic_music)
                            .into(imgCover);
                    imgCover.setTag(thumbnail);
                    Log.d("BaseActivity", "Updated thumbnail: " + thumbnail);
                } else {
                    imgCover.setImageResource(R.drawable.ic_music);
                    imgCover.setTag(null);
                }

                if ((MusicService.ACTION_PLAY.equals(action) || MusicService.ACTION_RESUME.equals(action)) && isValidTitle && isValidThumbnail) {
                    btnPlayPause.setImageResource(R.drawable.ic_pause);
                    btnPlayPause.setTag("playing");
                    miniPlayer.setVisibility(View.VISIBLE);
                    Log.d("BaseActivity", "Mini player updated to PLAY/RESUME state");
                } else if (MusicService.ACTION_PAUSE.equals(action) && isValidTitle && isValidThumbnail) {
                    btnPlayPause.setImageResource(R.drawable.ic_play);
                    btnPlayPause.setTag("paused");
                    miniPlayer.setVisibility(View.VISIBLE);
                    Log.d("BaseActivity", "Mini player updated to PAUSE state");
                } else {
                    miniPlayer.setVisibility(View.GONE);
                    Log.d("BaseActivity", "Mini player hidden due to invalid data or STOP action");
                }
            }

        };

        IntentFilter filter = new IntentFilter("UPDATE_UI");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(uiUpdateReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(uiUpdateReceiver, filter);
        }
        Log.d("BaseActivity", "BroadcastReceiver registered");
    }
}