package vn.edu.tlu.cse.soundplay.ui;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vn.edu.tlu.cse.soundplay.R;
import vn.edu.tlu.cse.soundplay.data.model.Favourite;
import vn.edu.tlu.cse.soundplay.data.repository.FavouriteRepository;
import vn.edu.tlu.cse.soundplay.data.repository.MusicRepository;
import vn.edu.tlu.cse.soundplay.service.MusicService;

public class PlaySongActivity extends AppCompatActivity {

    private TextView txtSongTitle, txtCurrentTime, txtTotalTime, txtLyrics, txtArtist;
    private ImageView imgSongThumbnail, btnShuffle;
    private SeekBar seekBar;
    private ImageView btnBack, btnFavorite, btnBackPre, btnPlay, btnNextPre;
    private BroadcastReceiver uiUpdateReceiver;
    private boolean isPlaying = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private String currentSongUrl;
    private String currentSongId;
    private ObjectAnimator rotationAnimator;
    private MusicRepository musicRepository;
    private FavouriteActivity favouriteActivity;
    private FavouriteRepository favouriteRepository; // Thêm repository
    private List<LrcLine> lrcLines = new ArrayList<>();
    private Runnable lyricsUpdater;
    private boolean isFromMiniPlayer = false;
    private boolean isServiceStateChecked = false;
    private boolean isFetchingLyrics = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playsong);
        Log.d("PlaySongActivity", "onCreate called");

        // Khởi tạo MusicRepository
        musicRepository = new MusicRepository(this);
        // Khởi tạo FavouriteRepository
        favouriteRepository = new FavouriteRepository(getApplication());

        // Khởi tạo các view
        txtSongTitle = findViewById(R.id.txtSongTitle);
        txtCurrentTime = findViewById(R.id.txtCurrentTime);
        txtTotalTime = findViewById(R.id.txtTotalTime);
        imgSongThumbnail = findViewById(R.id.imgCover);
        seekBar = findViewById(R.id.seekBar);
        btnBack = findViewById(R.id.btnBack1);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnBackPre = findViewById(R.id.btnBackPre);
        btnPlay = findViewById(R.id.btnPlay);
        btnNextPre = findViewById(R.id.btnNextPre);
        btnShuffle = findViewById(R.id.ic_shuffle);
        txtLyrics = findViewById(R.id.txtLyrics);
        txtArtist = findViewById(R.id.txtArtist);

        Intent intent = getIntent();
        String songTitle = intent.getStringExtra("MUSIC_TITLE");
        String songThumbnail = intent.getStringExtra("MUSIC_THUMBNAIL");
        currentSongUrl = intent.getStringExtra("MUSIC_URL");
        currentSongId = intent.getStringExtra("MUSIC_ID");
        String artist = intent.getStringExtra("MUSIC_ARTIST");
        ArrayList<String> songUrls = intent.getStringArrayListExtra("SONG_URLS");
        ArrayList<String> songTitles = intent.getStringArrayListExtra("SONG_TITLES");
        ArrayList<String> songThumbnails = intent.getStringArrayListExtra("SONG_THUMBNAILS");
        ArrayList<String> songArtists = intent.getStringArrayListExtra("SONG_ARTISTS");
        ArrayList<String> songIds = intent.getStringArrayListExtra("SONG_IDS");
        int songIndex = intent.getIntExtra("SONG_INDEX", 0);

        isFromMiniPlayer = (currentSongUrl != null && !currentSongUrl.isEmpty());

        rotationAnimator = ObjectAnimator.ofFloat(imgSongThumbnail, "rotation", 0f, 360f);
        rotationAnimator.setDuration(10000);
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        rotationAnimator.setInterpolator(new LinearInterpolator());

        // Cập nhật UI ban đầu
        if (artist != null && !artist.isEmpty() && !artist.contains("Lỗi")) {
            txtArtist.setText(artist);
        } else {
            txtArtist.setText("Không có nghệ sĩ");
        }

        if (songTitle != null && !songTitle.isEmpty() && !songTitle.contains("Lỗi")) {
            txtSongTitle.setText(songTitle);
        } else {
            txtSongTitle.setText("Không có tiêu đề");
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

        txtCurrentTime.setText("0:00");
        txtTotalTime.setText("0:00");
        txtLyrics.setText("Đang tải lời bài hát...");

        // Kiểm tra trạng thái yêu thích ban đầu
        if (currentSongId != null && !currentSongId.isEmpty()) {
            new Thread(() -> {
                Favourite existing = favouriteRepository.findByIdSync(currentSongId);
                runOnUiThread(() -> {
                    btnFavorite.setImageResource(existing != null ? R.drawable.ic_heart_filled : R.drawable.heart);
                });
            }).start();
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

        registerReceiver();

        if (currentSongUrl != null && !currentSongUrl.isEmpty()) {
            Intent checkIntent = new Intent(this, MusicService.class);
            checkIntent.setAction("CHECK_STATE");
            startService(checkIntent);
            Log.d("PlaySongActivity", "Sent CHECK_STATE to MusicService to verify current song");
        } else {
            Log.e("PlaySongActivity", "Song URL is null or empty");
            txtSongTitle.setText("Lỗi: Không có bài hát để phát");
            txtLyrics.setText("Không có bài hát để tải lời.");
            btnPlay.setEnabled(false);
        }
    }

    private void toggleFavorite() {
        Log.d("PlaySongActivity", "Toggling favorite for id: " + currentSongId);

        if (currentSongId == null || currentSongId.isEmpty()) {
            Log.w("PlaySongActivity", "Invalid id, cannot toggle favorite");
            return;
        }

        // Tạo đối tượng Favourite
        Favourite favourite = new Favourite();
        favourite.setId(currentSongId);
        favourite.setTitle(txtSongTitle.getText().toString());
        favourite.setThumbnail((String) imgSongThumbnail.getTag());
        favourite.setArtist(txtArtist.getText().toString());
        favourite.setUrl(currentSongUrl);

        // Gọi toggleFavourite từ repository
        favouriteRepository.toggleFavourite(favourite);

        // Cập nhật giao diện nút yêu thích
        new Thread(() -> {
            Favourite existing = favouriteRepository.findByIdSync(currentSongId); // Sửa ở đây
            runOnUiThread(() -> {
                btnFavorite.setImageResource(existing != null ? R.drawable.ic_heart_filled : R.drawable.heart);
                Log.d("PlaySongActivity", existing != null ? "Added to favorites: " + currentSongId : "Removed from favorites: " + currentSongId);
            });
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isServiceStateChecked) {
            Intent checkIntent = new Intent(this, MusicService.class);
            checkIntent.setAction("CHECK_STATE");
            startService(checkIntent);
            Log.d("PlaySongActivity", "onStart: Sent CHECK_STATE to MusicService");
        }
    }

    private void fetchLyrics(String songId) {
        if (isFetchingLyrics) {
            Log.d("PlaySongActivity", "Lyrics fetch in progress, skipping duplicate call for songId: " + songId);
            return;
        }
        if (songId == null || songId.isEmpty()) {
            Log.w("PlaySongActivity", "Invalid songId, cannot fetch lyrics");
            txtLyrics.setText("Không có ID bài hát để tải lời.");
            return;
        }

        isFetchingLyrics = true;
        lrcLines.clear();
        stopLyricsUpdate();
        txtLyrics.setText("Đang tải lời bài hát...");
        Log.d("PlaySongActivity", "Fetching lyrics for songId: " + songId);

        musicRepository.getLyric(songId, new MusicRepository.LyricCallback() {
            @Override
            public void onSuccess(String lyric) {
                isFetchingLyrics = false;
                Log.d("PlaySongActivity", "Lyrics received: " + lyric.substring(0, Math.min(lyric.length(), 50)) + "...");
                parseLrcContent(lyric);
                if (!lrcLines.isEmpty()) {
                    txtLyrics.setText(""); // Xóa trạng thái loading
                    if (isPlaying) {
                        startLyricsUpdate();
                    }
                } else {
                    txtLyrics.setText("Không có lời bài hát.");
                }
            }

            @Override
            public void onError(String errorMessage) {
                isFetchingLyrics = false;
                Log.e("PlaySongActivity", "Error fetching lyrics: " + errorMessage);
                txtLyrics.setText("Không thể tải lời bài hát: " + errorMessage);
            }
        });
    }

    private void parseLrcContent(String lrcContent) {
        lrcLines.clear();
        if (lrcContent == null || lrcContent.isEmpty()) {
            Log.w("PlaySongActivity", "Empty lyric content");
            return;
        }
        Pattern pattern = Pattern.compile("\\[(\\d{2}):(\\d{2})\\.(\\d{2})\\](.*)");
        for (String line : lrcContent.split("\n")) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                int minutes = Integer.parseInt(matcher.group(1));
                int seconds = Integer.parseInt(matcher.group(2));
                int milliseconds = Integer.parseInt(matcher.group(3)) * 10;
                long time = minutes * 60 * 1000 + seconds * 1000 + milliseconds;
                String lyric = matcher.group(4).trim();
                lrcLines.add(new LrcLine(time, lyric));
            }
        }
        Log.d("PlaySongActivity", "Parsed " + lrcLines.size() + " lyric lines");
    }

    private void startLyricsUpdate() {
        if (lyricsUpdater != null) {
            handler.removeCallbacks(lyricsUpdater);
        }

        lyricsUpdater = new Runnable() {
            @Override
            public void run() {
                if (isPlaying && !lrcLines.isEmpty()) {
                    int currentPosition = seekBar.getProgress();
                    StringBuilder lyricsDisplay = new StringBuilder();
                    int currentLineIndex = -1;

                    for (int i = 0; i < lrcLines.size(); i++) {
                        if (currentPosition >= lrcLines.get(i).time &&
                                (i == lrcLines.size() - 1 || currentPosition < lrcLines.get(i + 1).time)) {
                            currentLineIndex = i;
                            break;
                        }
                    }

                    if (currentLineIndex == -1) {
                        if (currentPosition < lrcLines.get(0).time) {
                            currentLineIndex = 0;
                        } else {
                            currentLineIndex = lrcLines.size() - 1;
                        }
                    }

                    for (int i = 0; i < lrcLines.size(); i++) {
                        String lyric = lrcLines.get(i).lyric;
                        if (i == currentLineIndex) {
                            lyricsDisplay.append("<b>").append(lyric).append("</b>");
                        } else {
                            lyric = lyric.replaceAll("<b>", "").replaceAll("</b>", "");
                            lyricsDisplay.append(lyric);
                        }
                        if (i < lrcLines.size() - 1) {
                            lyricsDisplay.append("\n");
                        }
                    }

                    txtLyrics.setText(Html.fromHtml(lyricsDisplay.toString(), Html.FROM_HTML_MODE_LEGACY));
                    Log.d("PlaySongActivity", "Lyrics updated, highlighted line: " + currentLineIndex);
                } else if (lrcLines.isEmpty()) {
                    txtLyrics.setText("Không có lời bài hát.");
                }
                handler.postDelayed(this, 100);
            }
        };

        if (seekBar.getMax() > 0) {
            handler.post(lyricsUpdater);
        } else {
            Log.d("PlaySongActivity", "Delaying lyrics update until duration is set");
            handler.postDelayed(() -> startLyricsUpdate(), 500);
        }
    }

    private void stopLyricsUpdate() {
        if (lyricsUpdater != null) {
            handler.removeCallbacks(lyricsUpdater);
            lyricsUpdater = null;
        }
    }

    private void startMusicService(String songUrl, String songTitle, String songThumbnail, String songId, String artist,
                                   ArrayList<String> songUrls, ArrayList<String> songTitles, ArrayList<String> songThumbnails,
                                   ArrayList<String> songArtists, ArrayList<String> songIds, int songIndex) {
        Log.d("PlaySongActivity", "Starting MusicService with URL: " + songUrl + ", Title: " + songTitle + ", SongId: " + songId);
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.setAction(MusicService.ACTION_PLAY);
        serviceIntent.putExtra("url", songUrl);
        serviceIntent.putExtra("title", songTitle);
        serviceIntent.putExtra("thumbnail", songThumbnail);
        serviceIntent.putExtra("songId", songId);
        serviceIntent.putExtra("artist", artist);
        if (songUrls != null) {
            serviceIntent.putStringArrayListExtra("songUrls", songUrls);
            serviceIntent.putStringArrayListExtra("songTitles", songTitles);
            serviceIntent.putStringArrayListExtra("songThumbnails", songThumbnails);
            serviceIntent.putStringArrayListExtra("songArtists", songArtists);
            serviceIntent.putStringArrayListExtra("songIds", songIds);
            serviceIntent.putExtra("currentIndex", songIndex);
        }
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
                String songUrl = intent.getStringExtra("url");
                String songId = intent.getStringExtra("songId");
                String artist = intent.getStringExtra("artist");
                String action = intent.getStringExtra("action");
                int duration = intent.getIntExtra("duration", 0);
                int currentPosition = intent.getIntExtra("currentPosition", 0);

                // Log để kiểm tra songId
                Log.d("PlaySongActivity", "Received songId: " + songId + ", currentSongId: " + currentSongId);

                // Cập nhật tiêu đề nếu hợp lệ
                if (songTitle != null && !songTitle.isEmpty() && !songTitle.contains("Lỗi")) {
                    txtSongTitle.setText(songTitle);
                    Log.d("PlaySongActivity", "Updated title: " + songTitle);
                } else {
                    txtSongTitle.setText("Không có tiêu đề");
                }

                // Cập nhật nghệ sĩ
                if (artist != null && !artist.isEmpty() && !artist.contains("Lỗi")) {
                    txtArtist.setText(artist);
                } else {
                    txtArtist.setText("Không có nghệ sĩ");
                }

                // Cập nhật ảnh bìa
                if (songThumbnail != null && !songThumbnail.isEmpty()) {
                    Glide.with(PlaySongActivity.this)
                            .load(songThumbnail)
                            .placeholder(R.drawable.ic_music)
                            .error(R.drawable.ic_music)
                            .into(imgSongThumbnail);
                    imgSongThumbnail.setTag(songThumbnail);
                    Log.d("PlaySongActivity", "Updated thumbnail: " + songThumbnail);
                } else {
                    imgSongThumbnail.setImageResource(R.drawable.ic_music);
                }

                // Kiểm tra trạng thái khi đến từ mini player
                if (isFromMiniPlayer && !isServiceStateChecked) {
                    isServiceStateChecked = true;
                    if (songUrl != null && songUrl.equals(currentSongUrl)) {
                        Log.d("PlaySongActivity", "Song matches, maintaining current playback state");
                        // Không gọi startMusicService, giữ trạng thái hiện tại
                    } else {
                        Log.d("PlaySongActivity", "Song does not match, starting new playback");
                        Intent serviceIntent = new Intent(PlaySongActivity.this, MusicService.class);
                        serviceIntent.setAction(MusicService.ACTION_PLAY);
                        serviceIntent.putExtra("url", currentSongUrl);
                        serviceIntent.putExtra("title", songTitle);
                        serviceIntent.putExtra("thumbnail", songThumbnail);
                        serviceIntent.putExtra("songId", currentSongId);
                        serviceIntent.putExtra("artist", artist);
                        ArrayList<String> songUrls = intent.getStringArrayListExtra("SONG_URLS");
                        if (songUrls != null) {
                            serviceIntent.putStringArrayListExtra("songUrls", songUrls);
                            serviceIntent.putStringArrayListExtra("songTitles", intent.getStringArrayListExtra("SONG_TITLES"));
                            serviceIntent.putStringArrayListExtra("songThumbnails", intent.getStringArrayListExtra("SONG_THUMBNAILS"));
                            serviceIntent.putStringArrayListExtra("songArtists", intent.getStringArrayListExtra("SONG_ARTISTS"));
                            serviceIntent.putStringArrayListExtra("songIds", intent.getStringArrayListExtra("SONG_IDS"));
                            serviceIntent.putExtra("currentIndex", intent.getIntExtra("SONG_INDEX", 0));
                        }
                        startService(serviceIntent);
                    }
                }

                // Cập nhật URL và ID
                currentSongUrl = songUrl;
                if (songId != null && !songId.isEmpty()) {
                    if (!songId.equals(currentSongId) || lrcLines.isEmpty()) {
                        currentSongId = songId;
                        fetchLyrics(songId);
                    }
                } else {
                    Log.w("PlaySongActivity", "Received empty songId, cannot fetch lyrics");
                    txtLyrics.setText("Không có ID bài hát để tải lời.");
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

                // Cập nhật trạng thái play/pause và hiệu ứng xoay
                if (MusicService.ACTION_PLAY.equals(action) || MusicService.ACTION_RESUME.equals(action)) {
                    btnPlay.setImageResource(R.drawable.ic_pause);
                    isPlaying = true;
                    btnPlay.setEnabled(true);
                    if (!rotationAnimator.isStarted() || rotationAnimator.isPaused()) {
                        rotationAnimator.start();
                    }
                    if (!lrcLines.isEmpty()) {
                        startLyricsUpdate();
                    }
                    Log.d("PlaySongActivity", "UI updated to PLAY/RESUME state, rotation started");
                } else if (MusicService.ACTION_PAUSE.equals(action)) {
                    btnPlay.setImageResource(R.drawable.ic_play);
                    isPlaying = false;
                    btnPlay.setEnabled(true);
                    if (rotationAnimator.isRunning()) {
                        rotationAnimator.pause();
                    }
                    stopLyricsUpdate();
                    Log.d("PlaySongActivity", "UI updated to PAUSE state, rotation paused");
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
        stopLyricsUpdate();
    }

    private static class LrcLine {
        long time;
        String lyric;

        LrcLine(long time, String lyric) {
            this.time = time;
            this.lyric = lyric;
        }
    }
}