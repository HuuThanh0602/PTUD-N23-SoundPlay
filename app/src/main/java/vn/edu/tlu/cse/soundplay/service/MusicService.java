package vn.edu.tlu.cse.soundplay.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vn.edu.tlu.cse.soundplay.R;
import vn.edu.tlu.cse.soundplay.ui.PlaySongActivity;

public class MusicService extends Service {

    public static final String ACTION_PLAY = "PLAY";
    public static final String ACTION_PAUSE = "PAUSE";
    public static final String ACTION_RESUME = "RESUME";
    public static final String ACTION_NEXT = "NEXT";
    public static final String ACTION_PREV = "PREV";
    public static final String ACTION_SHUFFLE = "SHUFFLE";
    public static final String ACTION_SEEK = "SEEK";

    private static final String CHANNEL_ID = "MusicServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    private static final String TAG = "MusicService";

    private MediaPlayer mediaPlayer;
    private String currentSongUrl;
    private String currentSongTitle;
    private String currentSongThumbnail;
    private String currentSongId;
    private String currentSongArtist;
    private boolean isPlaying = false;
    private List<String> songUrls = new ArrayList<>();
    private List<String> songTitles = new ArrayList<>();
    private List<String> songThumbnails = new ArrayList<>();
    private List<String> songIds = new ArrayList<>();
    private List<String> songArtists = new ArrayList<>();
    private int currentSongIndex = 0;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        Log.d(TAG, "onCreate called");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: " + intent);
        String action = intent != null ? intent.getAction() : null;

        updateSongList(intent);

        if (action != null) {
            switch (action) {
                case ACTION_PLAY:
                    String url = intent.getStringExtra("url");
                    String title = intent.getStringExtra("title");
                    String thumbnail = intent.getStringExtra("thumbnail");
                    String songId = intent.getStringExtra("songId");
                    String artist = intent.getStringExtra("artist");
                    if (url != null && !url.isEmpty()) {
                        playSong(url, title != null ? title : "Không có tiêu đề", thumbnail, songId, artist);
                    } else {
                        Log.e(TAG, "No valid URL provided for ACTION_PLAY");
                        sendUpdateBroadcast("Không có bài hát", "", "", "", "", ACTION_PAUSE);
                    }
                    break;
                case ACTION_RESUME:
                    resumeSong();
                    break;
                case ACTION_PAUSE:
                    pauseSong();
                    break;
                case ACTION_NEXT:
                    nextSong();
                    break;
                case ACTION_PREV:
                    prevSong();
                    break;
                case ACTION_SHUFFLE:
                    shuffleSongs();
                    break;
                case ACTION_SEEK:
                    int progress = intent.getIntExtra("progress", 0);
                    seekTo(progress);
                    break;
                case "CHECK_STATE":
                    sendCurrentState();
                    break;
                default:
                    Log.w(TAG, "Unknown action: " + action);
            }
        } else {
            Log.w(TAG, "No action provided in intent");
        }

        return START_STICKY;
    }

    private void updateSongList(Intent intent) {
        if (intent == null) return;

        ArrayList<String> receivedSongUrls = intent.getStringArrayListExtra("songUrls");
        ArrayList<String> receivedSongTitles = intent.getStringArrayListExtra("songTitles");
        ArrayList<String> receivedSongThumbnails = intent.getStringArrayListExtra("songThumbnails");
        ArrayList<String> receivedSongIds = intent.getStringArrayListExtra("songIds");
        ArrayList<String> receivedSongArtists = intent.getStringArrayListExtra("songArtists");

        if (receivedSongUrls != null && !receivedSongUrls.isEmpty()) {
            songUrls.clear();
            songUrls.addAll(receivedSongUrls);

            songTitles.clear();
            if (receivedSongTitles != null && receivedSongTitles.size() == receivedSongUrls.size()) {
                songTitles.addAll(receivedSongTitles);
            } else {
                for (int i = 0; i < receivedSongUrls.size(); i++) {
                    songTitles.add("Bài hát " + (i + 1));
                }
            }

            songThumbnails.clear();
            if (receivedSongThumbnails != null && receivedSongThumbnails.size() == receivedSongUrls.size()) {
                songThumbnails.addAll(receivedSongThumbnails);
            } else {
                for (int i = 0; i < receivedSongUrls.size(); i++) {
                    songThumbnails.add("");
                }
            }

            songIds.clear();
            if (receivedSongIds != null && receivedSongIds.size() == receivedSongUrls.size()) {
                songIds.addAll(receivedSongIds);
            } else {
                Log.w(TAG, "No song IDs provided, setting empty IDs");
                for (int i = 0; i < receivedSongUrls.size(); i++) {
                    songIds.add("");
                }
            }

            songArtists.clear();
            if (receivedSongArtists != null && receivedSongArtists.size() == receivedSongUrls.size()) {
                songArtists.addAll(receivedSongArtists);
            } else {
                for (int i = 0; i < receivedSongUrls.size(); i++) {
                    songArtists.add("Unknown Artist");
                }
            }

            currentSongIndex = intent.getIntExtra("currentIndex", 0);
            Log.d(TAG, "Updated song list: URLs=" + songUrls.size() + ", IDs=" + songIds.size() + ", Artists=" + songArtists.size() + ", Index=" + currentSongIndex);
        } else {
            Log.w(TAG, "Received empty song URLs");
        }
    }

    private void sendCurrentState() {
        if (currentSongUrl != null && !currentSongUrl.isEmpty()) {
            sendUpdateBroadcast(
                    currentSongTitle != null ? currentSongTitle : "Không có tiêu đề",
                    currentSongThumbnail != null ? currentSongThumbnail : "",
                    currentSongUrl,
                    currentSongId != null ? currentSongId : "",
                    currentSongArtist != null ? currentSongArtist : "Unknown Artist",
                    isPlaying ? ACTION_PLAY : ACTION_PAUSE
            );
        } else {
            sendUpdateBroadcast("Không có bài hát", "", "", "", "", ACTION_PAUSE);
        }
        Log.d(TAG, "CHECK_STATE: currentSongUrl=" + currentSongUrl + ", currentSongId=" + currentSongId + ", isPlaying=" + isPlaying);
    }

    private void playSong(String url, String title, String thumbnail, String songId, String artist) {
        Log.d(TAG, "Playing song: URL=" + url + ", Title=" + title + ", SongId=" + songId + ", Artist=" + artist);
        try {
            // Luôn dừng và làm mới MediaPlayer khi phát bài mới
            stopCurrentPlayback();
            currentSongUrl = url;
            currentSongTitle = title;
            currentSongThumbnail = thumbnail != null ? thumbnail : "";
            currentSongId = songId != null ? songId : "";
            currentSongArtist = artist != null ? artist : "Unknown Artist";

            mediaPlayer = MediaPlayer.create(this, Uri.parse(url));
            if (mediaPlayer == null) {
                Log.e(TAG, "MediaPlayer creation failed for URL: " + url);
                sendUpdateBroadcast("Không thể phát bài hát", "", "", "", "", ACTION_PAUSE);
                stopForeground(true);
                return;
            }
            setupMediaPlayer();
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, "Error playing song: " + e.getMessage());
            sendUpdateBroadcast("Không thể phát bài hát", "", "", "", "", ACTION_PAUSE);
            stopForeground(true);
        }
    }

    private void stopCurrentPlayback() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
                mediaPlayer.release();
                Log.d(TAG, "MediaPlayer stopped and released");
            } catch (Exception e) {
                Log.e(TAG, "Error stopping MediaPlayer: " + e.getMessage());
            }
            mediaPlayer = null;
        }
        isPlaying = false;
        handler.removeCallbacksAndMessages(null);
        Log.d(TAG, "All playback callbacks cleared");
    }

    private void setupMediaPlayer() {
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            Log.e(TAG, "MediaPlayer error: what=" + what + ", extra=" + extra);
            isPlaying = false;
            sendUpdateBroadcast("Không thể phát nhạc", "", "", "", "", ACTION_PAUSE);
            stopForeground(true);
            return true;
        });

        mediaPlayer.setOnPreparedListener(mp -> {
            Log.d(TAG, "MediaPlayer prepared, starting playback for: " + currentSongTitle);
            try {
                mediaPlayer.start();
                isPlaying = true;
                sendUpdateBroadcast(currentSongTitle, currentSongThumbnail, currentSongUrl, currentSongId, currentSongArtist, ACTION_PLAY);
                updateSeekBar();
                updateNotification(true);
            } catch (Exception e) {
                Log.e(TAG, "Error starting playback: " + e.getMessage());
                sendUpdateBroadcast("Không thể bắt đầu phát", "", "", "", "", ACTION_PAUSE);
                stopForeground(true);
            }
        });

        mediaPlayer.setOnCompletionListener(mp -> {
            Log.d(TAG, "Song completed: " + currentSongTitle);
            nextSong();
        });
    }

    private void resumeSong() {
        Log.d(TAG, "Resuming song: " + currentSongTitle);
        if (mediaPlayer != null && !isPlaying) {
            try {
                mediaPlayer.start();
                isPlaying = true;
                sendUpdateBroadcast(
                        currentSongTitle != null ? currentSongTitle : "Không có tiêu đề",
                        currentSongThumbnail != null ? currentSongThumbnail : "",
                        currentSongUrl,
                        currentSongId != null ? currentSongId : "",
                        currentSongArtist != null ? currentSongArtist : "Unknown Artist",
                        ACTION_RESUME
                );
                updateSeekBar();
                updateNotification(true);
            } catch (Exception e) {
                Log.e(TAG, "Error resuming song: " + e.getMessage());
                sendUpdateBroadcast("Không thể tiếp tục phát", "", "", "", "", ACTION_PAUSE);
            }
        } else if (currentSongUrl != null && !currentSongUrl.isEmpty()) {
            playSong(currentSongUrl, currentSongTitle, currentSongThumbnail, currentSongId, currentSongArtist);
        } else {
            Log.w(TAG, "Cannot resume: No valid song loaded");
            sendUpdateBroadcast("Không có bài hát để tiếp tục", "", "", "", "", ACTION_PAUSE);
        }
    }

    private void pauseSong() {
        Log.d(TAG, "Pausing song: " + currentSongTitle);
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.pause();
                isPlaying = false;
                sendUpdateBroadcast(
                        currentSongTitle != null ? currentSongTitle : "Không có tiêu đề",
                        currentSongThumbnail != null ? currentSongThumbnail : "",
                        currentSongUrl,
                        currentSongId != null ? currentSongId : "",
                        currentSongArtist != null ? currentSongArtist : "Unknown Artist",
                        ACTION_PAUSE
                );
                updateNotification(false);
                handler.removeCallbacksAndMessages(null);
            } catch (Exception e) {
                Log.e(TAG, "Error pausing song: " + e.getMessage());
            }
        } else {
            sendUpdateBroadcast(
                    currentSongTitle != null ? currentSongTitle : "Không có tiêu đề",
                    currentSongThumbnail != null ? currentSongThumbnail : "",
                    currentSongUrl,
                    currentSongId != null ? currentSongId : "",
                    currentSongArtist != null ? currentSongArtist : "Unknown Artist",
                    ACTION_PAUSE
            );
        }
    }

    private void nextSong() {
        Log.d(TAG, "Next song requested, current index: " + currentSongIndex);
        if (songUrls.isEmpty()) {
            Log.w(TAG, "No songs available for next");
            sendUpdateBroadcast("Không có bài hát tiếp theo", "", "", "", "", ACTION_PAUSE);
            stopForeground(true);
            return;
        }
        currentSongIndex = (currentSongIndex + 1) % songUrls.size();
        Log.d(TAG, "New song index: " + currentSongIndex);
        playCurrentSong();
    }

    private void prevSong() {
        Log.d(TAG, "Previous song requested, current index: " + currentSongIndex);
        if (songUrls.isEmpty()) {
            Log.w(TAG, "No songs available for previous");
            sendUpdateBroadcast("Không có bài hát trước đó", "", "", "", "", ACTION_PAUSE);
            stopForeground(true);
            return;
        }
        currentSongIndex = (currentSongIndex - 1 + songUrls.size()) % songUrls.size();
        Log.d(TAG, "New song index: " + currentSongIndex);
        playCurrentSong();
    }

    private void playCurrentSong() {
        if (songUrls.isEmpty() || currentSongIndex < 0 || currentSongIndex >= songUrls.size()) {
            Log.e(TAG, "Invalid song index or empty song list");
            sendUpdateBroadcast("Không có bài hát để phát", "", "", "", "", ACTION_PAUSE);
            return;
        }
        currentSongUrl = songUrls.get(currentSongIndex);
        currentSongTitle = songTitles.isEmpty() ? "Không có tiêu đề" : songTitles.get(currentSongIndex);
        currentSongThumbnail = songThumbnails.isEmpty() ? "" : songThumbnails.get(currentSongIndex);
        currentSongId = songIds.isEmpty() ? "" : songIds.get(currentSongIndex);
        currentSongArtist = songArtists.isEmpty() ? "Unknown Artist" : songArtists.get(currentSongIndex);
        Log.d(TAG, "Playing current song: index=" + currentSongIndex + ", URL=" + currentSongUrl + ", Title=" + currentSongTitle);
        playSong(currentSongUrl, currentSongTitle, currentSongThumbnail, currentSongId, currentSongArtist);
    }

    private void shuffleSongs() {
        Log.d(TAG, "Shuffling songs");
        if (songUrls.isEmpty()) {
            Log.w(TAG, "No songs available to shuffle");
            sendUpdateBroadcast("Không có bài hát để xáo trộn", "", "", "", "", ACTION_PAUSE);
            stopForeground(true);
            return;
        }
        // Tạo danh sách chỉ số để xáo trộn
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < songUrls.size(); i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);

        // Tạo danh sách mới theo thứ tự xáo trộn
        List<String> shuffledUrls = new ArrayList<>();
        List<String> shuffledTitles = new ArrayList<>();
        List<String> shuffledThumbnails = new ArrayList<>();
        List<String> shuffledIds = new ArrayList<>();
        List<String> shuffledArtists = new ArrayList<>();

        for (int index : indices) {
            shuffledUrls.add(songUrls.get(index));
            shuffledTitles.add(songTitles.isEmpty() ? "Bài hát " + (index + 1) : songTitles.get(index));
            shuffledThumbnails.add(songThumbnails.isEmpty() ? "" : songThumbnails.get(index));
            shuffledIds.add(songIds.isEmpty() ? "" : songIds.get(index));
            shuffledArtists.add(songArtists.isEmpty() ? "Unknown Artist" : songArtists.get(index));
        }

        songUrls.clear();
        songUrls.addAll(shuffledUrls);
        songTitles.clear();
        songTitles.addAll(shuffledTitles);
        songThumbnails.clear();
        songThumbnails.addAll(shuffledThumbnails);
        songIds.clear();
        songIds.addAll(shuffledIds);
        songArtists.clear();
        songArtists.addAll(shuffledArtists);

        currentSongIndex = 0;
        Log.d(TAG, "Shuffled song list, new index: " + currentSongIndex);
        playCurrentSong();
    }

    private void seekTo(int progress) {
        Log.d(TAG, "Seeking to: " + progress);
        if (mediaPlayer != null) {
            try {
                mediaPlayer.seekTo(progress);
                sendUpdateBroadcast(
                        currentSongTitle != null ? currentSongTitle : "Không có tiêu đề",
                        currentSongThumbnail != null ? currentSongThumbnail : "",
                        currentSongUrl,
                        currentSongId != null ? currentSongId : "",
                        currentSongArtist != null ? currentSongArtist : "Unknown Artist",
                        isPlaying ? ACTION_PLAY : ACTION_PAUSE
                );
                if (isPlaying) updateSeekBar();
            } catch (Exception e) {
                Log.e(TAG, "Error seeking: " + e.getMessage());
            }
        }
    }

    private void updateSeekBar() {
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && isPlaying) {
                    try {
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        int duration = mediaPlayer.getDuration();
                        sendUpdateBroadcast(currentSongTitle, currentSongThumbnail, currentSongUrl, currentSongId, currentSongArtist, ACTION_PLAY);
                        Log.d(TAG, "SeekBar update: position=" + currentPosition + ", duration=" + duration);
                        handler.postDelayed(this, 1000);
                    } catch (Exception e) {
                        Log.e(TAG, "Error updating SeekBar: " + e.getMessage());
                    }
                }
            }
        }, 0);
    }

    private void sendUpdateBroadcast(String songTitle, String thumbnail, String url, String songId, String artist, String action) {
        Log.d(TAG, "Sending broadcast: title=" + songTitle + ", thumbnail=" + thumbnail + ", songId=" + songId + ", artist=" + artist + ", action=" + action);
        Intent updateIntent = new Intent("UPDATE_UI");
        updateIntent.putExtra("title", songTitle);
        updateIntent.putExtra("thumbnail", thumbnail);
        updateIntent.putExtra("url", url);
        updateIntent.putExtra("songId", songId);
        updateIntent.putExtra("artist", artist);
        updateIntent.putExtra("action", action);
        updateIntent.putExtra("duration", mediaPlayer != null ? mediaPlayer.getDuration() : 0);
        updateIntent.putExtra("currentPosition", mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0);
        updateIntent.putStringArrayListExtra("SONG_URLS", new ArrayList<>(songUrls));
        updateIntent.putStringArrayListExtra("SONG_TITLES", new ArrayList<>(songTitles));
        updateIntent.putStringArrayListExtra("SONG_THUMBNAILS", new ArrayList<>(songThumbnails));
        updateIntent.putStringArrayListExtra("SONG_IDS", new ArrayList<>(songIds));
        updateIntent.putStringArrayListExtra("SONG_ARTISTS", new ArrayList<>(songArtists));
        updateIntent.putExtra("SONG_INDEX", currentSongIndex);
        sendBroadcast(updateIntent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void updateNotification(boolean isPlaying) {
        Intent intent = new Intent(this, PlaySongActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent pauseIntent = new Intent(this, MusicService.class);
        pauseIntent.setAction(isPlaying ? ACTION_PAUSE : ACTION_RESUME);
        PendingIntent pausePendingIntent = PendingIntent.getService(
                this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music)
                .setContentTitle(currentSongTitle != null ? currentSongTitle : "Không có tiêu đề")
                .setContentText(isPlaying ? "Đang phát" : "Đã tạm dừng")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .addAction(
                        isPlaying ? R.drawable.ic_pause : R.drawable.ic_play,
                        isPlaying ? "Pause" : "Play",
                        pausePendingIntent
                )
                .setOngoing(isPlaying);

        startForeground(NOTIFICATION_ID, builder.build());
        Log.d(TAG, "Notification updated: " + currentSongTitle + ", isPlaying=" + isPlaying);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        stopCurrentPlayback();
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}