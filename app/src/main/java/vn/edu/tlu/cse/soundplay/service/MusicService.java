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

    private MediaPlayer mediaPlayer;
    private String currentSongUrl;
    private String currentSongTitle;
    private String currentSongThumbnail;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isPlaying = false;
    private List<String> songUrls = new ArrayList<>();
    private List<String> songTitles = new ArrayList<>();
    private List<String> songThumbnails = new ArrayList<>();
    private int currentSongIndex = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        Log.d("MusicService", "onCreate called");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MusicService", "onStartCommand: " + intent);
        String action = intent != null ? intent.getAction() : null;

        // Lấy danh sách bài hát, tiêu đề, và ảnh bìa
        ArrayList<String> receivedSongUrls = intent != null ? intent.getStringArrayListExtra("songUrls") : null;
        ArrayList<String> receivedSongTitles = intent != null ? intent.getStringArrayListExtra("songTitles") : null;
        ArrayList<String> receivedSongThumbnails = intent != null ? intent.getStringArrayListExtra("songThumbnails") : null;
        if (receivedSongUrls != null && !receivedSongUrls.isEmpty()) {
            songUrls.clear();
            songUrls.addAll(receivedSongUrls);
            if (receivedSongTitles != null && receivedSongTitles.size() == receivedSongUrls.size()) {
                songTitles.clear();
                songTitles.addAll(receivedSongTitles);
            } else {
                songTitles.clear();
                for (int i = 0; i < receivedSongUrls.size(); i++) {
                    songTitles.add("Bài hát " + (i + 1));
                }
            }
            if (receivedSongThumbnails != null && receivedSongThumbnails.size() == receivedSongUrls.size()) {
                songThumbnails.clear();
                songThumbnails.addAll(receivedSongThumbnails);
            } else {
                songThumbnails.clear();
                for (int i = 0; i < receivedSongUrls.size(); i++) {
                    songThumbnails.add("");
                }
            }
            currentSongIndex = intent.getIntExtra("currentIndex", 0);
            Log.d("MusicService", "Received song list: URLs=" + songUrls + ", Titles=" + songTitles + ", Thumbnails=" + songThumbnails + ", Index=" + currentSongIndex);
        }

        if (action != null) {
            switch (action) {
                case ACTION_PLAY:
                    String url = intent.getStringExtra("url");
                    String title = intent.getStringExtra("title");
                    String thumbnail = intent.getStringExtra("thumbnail");
                    if (url != null && !url.isEmpty()) {
                        playSong(url, title != null ? title : "Không có tiêu đề", thumbnail);
                    } else {
                        Log.e("MusicService", "No valid URL provided for ACTION_PLAY");
                        sendUpdateBroadcast("Không có bài hát", "", ACTION_PAUSE);
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
                    // Gửi broadcast trạng thái hiện tại
                    if (currentSongUrl != null) {
                        sendUpdateBroadcast(currentSongTitle != null ? currentSongTitle : "Không có tiêu đề",
                                currentSongThumbnail != null ? currentSongThumbnail : "",
                                isPlaying ? ACTION_PLAY : ACTION_PAUSE);
                    } else {
                        sendUpdateBroadcast("Không có bài hát", "", ACTION_PAUSE);
                    }
                    break;
                default:
                    Log.w("MusicService", "Unknown action: " + action);
            }
        } else {
            Log.w("MusicService", "No action provided in intent");
        }

        return START_STICKY;
    }

    private void playSong(String url, String title, String thumbnail) {
        Log.d("MusicService", "Attempting to play URL: " + url + ", Title: " + title + ", Thumbnail: " + thumbnail);
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
            }

            currentSongUrl = url;
            currentSongTitle = title;
            currentSongThumbnail = thumbnail;
            mediaPlayer = MediaPlayer.create(this, Uri.parse(url));
            if (mediaPlayer == null) {
                Log.e("MusicService", "MediaPlayer creation failed for URL: " + url);
                sendUpdateBroadcast("Không thể phát bài hát", "", ACTION_PAUSE);
                stopForeground(true);
                return;
            }

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e("MusicService", "MediaPlayer error: what=" + what + ", extra=" + extra);
                isPlaying = false;
                sendUpdateBroadcast("Không thể phát nhạc", "", ACTION_PAUSE);
                stopForeground(true);
                return true;
            });

            mediaPlayer.setOnPreparedListener(mp -> {
                Log.d("MusicService", "MediaPlayer prepared, starting playback");
                try {
                    mediaPlayer.start();
                    isPlaying = true;
                    sendUpdateBroadcast(currentSongTitle, currentSongThumbnail, ACTION_PLAY);
                    updateSeekBar();
                    updateNotification(true);
                } catch (Exception e) {
                    Log.e("MusicService", "Error starting playback: " + e.getMessage());
                    sendUpdateBroadcast("Không thể bắt đầu phát", "", ACTION_PAUSE);
                    stopForeground(true);
                }
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                Log.d("MusicService", "Song completed: " + currentSongTitle);
                nextSong();
            });

            mediaPlayer.prepareAsync();
            Log.d("MusicService", "Preparing MediaPlayer async for URL: " + url);
        } catch (Exception e) {
            Log.e("MusicService", "Error playing song: " + e.getMessage(), e);
            sendUpdateBroadcast("Không thể phát bài hát", "", ACTION_PAUSE);
            stopForeground(true);
        }
    }

    private void resumeSong() {
        Log.d("MusicService", "Resuming song: " + currentSongTitle);
        if (mediaPlayer != null && !isPlaying) {
            try {
                mediaPlayer.start();
                isPlaying = true;
                sendUpdateBroadcast(currentSongTitle != null ? currentSongTitle : "Không có tiêu đề",
                        currentSongThumbnail != null ? currentSongThumbnail : "",
                        ACTION_RESUME);
                updateSeekBar();
                updateNotification(true);
            } catch (Exception e) {
                Log.e("MusicService", "Error resuming song: " + e.getMessage());
                sendUpdateBroadcast(currentSongTitle != null ? currentSongTitle : "Không thể tiếp tục phát",
                        currentSongThumbnail != null ? currentSongThumbnail : "",
                        ACTION_PAUSE);
            }
        } else if (currentSongUrl != null && !currentSongUrl.isEmpty()) {
            Log.w("MusicService", "MediaPlayer is null or invalid, replaying song");
            playSong(currentSongUrl, currentSongTitle, currentSongThumbnail);
        } else {
            Log.w("MusicService", "Cannot resume: No valid song loaded");
            sendUpdateBroadcast("Không có bài hát để tiếp tục", "", ACTION_PAUSE);
        }
    }

    private void pauseSong() {
        Log.d("MusicService", "Pausing song: " + currentSongTitle);
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.pause();
                isPlaying = false;
                sendUpdateBroadcast(currentSongTitle != null ? currentSongTitle : "Không có tiêu đề",
                        currentSongThumbnail != null ? currentSongThumbnail : "",
                        ACTION_PAUSE);
                updateNotification(false);
                handler.removeCallbacksAndMessages(null); // Dừng update SeekBar khi pause
            } catch (Exception e) {
                Log.e("MusicService", "Error pausing song: " + e.getMessage());
                sendUpdateBroadcast(currentSongTitle != null ? currentSongTitle : "Không có tiêu đề",
                        currentSongThumbnail != null ? currentSongThumbnail : "",
                        ACTION_PAUSE);
            }
        } else {
            Log.w("MusicService", "Cannot pause: MediaPlayer is null or not playing");
            sendUpdateBroadcast(currentSongTitle != null ? currentSongTitle : "Không có tiêu đề",
                    currentSongThumbnail != null ? currentSongThumbnail : "",
                    ACTION_PAUSE);
        }
    }

    private void nextSong() {
        Log.d("MusicService", "Next song");
        if (songUrls.isEmpty()) {
            Log.w("MusicService", "No songs available for next");
            sendUpdateBroadcast("Không có bài hát tiếp theo", "", ACTION_PAUSE);
            stopForeground(true);
            return;
        }
        currentSongIndex = (currentSongIndex + 1) % songUrls.size();
        currentSongUrl = songUrls.get(currentSongIndex);
        currentSongTitle = songTitles.isEmpty() ? "Không có tiêu đề" : songTitles.get(currentSongIndex);
        currentSongThumbnail = songThumbnails.isEmpty() ? "" : songThumbnails.get(currentSongIndex);
        playSong(currentSongUrl, currentSongTitle, currentSongThumbnail);
    }

    private void prevSong() {
        Log.d("MusicService", "Previous song");
        if (songUrls.isEmpty()) {
            Log.w("MusicService", "No songs available for previous");
            sendUpdateBroadcast("Không có bài hát trước đó", "", ACTION_PAUSE);
            stopForeground(true);
            return;
        }
        currentSongIndex = (currentSongIndex - 1 + songUrls.size()) % songUrls.size();
        currentSongUrl = songUrls.get(currentSongIndex);
        currentSongTitle = songTitles.isEmpty() ? "Không có tiêu đề" : songTitles.get(currentSongIndex);
        currentSongThumbnail = songThumbnails.isEmpty() ? "" : songThumbnails.get(currentSongIndex);
        playSong(currentSongUrl, currentSongTitle, currentSongThumbnail);
    }

    private void shuffleSongs() {
        Log.d("MusicService", "Shuffling songs");
        if (songUrls.isEmpty()) {
            Log.w("MusicService", "No songs available to shuffle");
            sendUpdateBroadcast("Không có bài hát để xáo trộn", "", ACTION_PAUSE);
            stopForeground(true);
            return;
        }
        Collections.shuffle(songUrls);
        if (!songTitles.isEmpty()) {
            Collections.shuffle(songTitles);
        }
        if (!songThumbnails.isEmpty()) {
            Collections.shuffle(songThumbnails);
        }
        currentSongIndex = 0;
        currentSongUrl = songUrls.get(currentSongIndex);
        currentSongTitle = songTitles.isEmpty() ? "Không có tiêu đề" : songTitles.get(currentSongIndex);
        currentSongThumbnail = songThumbnails.isEmpty() ? "" : songThumbnails.get(currentSongIndex);
        playSong(currentSongUrl, currentSongTitle, currentSongThumbnail);
    }

    private void seekTo(int progress) {
        Log.d("MusicService", "Seeking to: " + progress);
        if (mediaPlayer != null) {
            try {
                mediaPlayer.seekTo(progress);
                sendUpdateBroadcast(currentSongTitle != null ? currentSongTitle : "Không có tiêu đề",
                        currentSongThumbnail != null ? currentSongThumbnail : "",
                        isPlaying ? ACTION_PLAY : ACTION_PAUSE);
                if (isPlaying) {
                    updateSeekBar(); // Tiếp tục cập nhật SeekBar và thời gian sau khi tua
                }
            } catch (Exception e) {
                Log.e("MusicService", "Error seeking: " + e.getMessage());
            }
        } else {
            Log.w("MusicService", "Cannot seek: MediaPlayer is null");
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
                        sendUpdateBroadcast(currentSongTitle != null ? currentSongTitle : "Không có tiêu đề",
                                currentSongThumbnail != null ? currentSongThumbnail : "",
                                ACTION_PLAY);
                        Log.d("MusicService", "SeekBar update: position=" + currentPosition + ", duration=" + duration);
                        handler.postDelayed(this, 1000);
                    } catch (Exception e) {
                        Log.e("MusicService", "Error updating SeekBar: " + e.getMessage());
                    }
                }
            }
        }, 0);
    }

    private void sendUpdateBroadcast(String songTitle, String thumbnail, String action) {
        Log.d("MusicService", "Sending broadcast: title=" + songTitle + ", thumbnail=" + thumbnail + ", action=" + action);
        Intent updateIntent = new Intent("UPDATE_UI");
        updateIntent.putExtra("title", songTitle);
        updateIntent.putExtra("thumbnail", thumbnail);
        if (action != null) {
            updateIntent.putExtra("action", action);
        }
        if (mediaPlayer != null) {
            try {
                updateIntent.putExtra("duration", mediaPlayer.getDuration());
                updateIntent.putExtra("currentPosition", mediaPlayer.getCurrentPosition());
            } catch (Exception e) {
                Log.e("MusicService", "Error getting MediaPlayer info: " + e.getMessage());
                updateIntent.putExtra("duration", 0);
                updateIntent.putExtra("currentPosition", 0);
            }
        } else {
            updateIntent.putExtra("duration", 0);
            updateIntent.putExtra("currentPosition", 0);
        }
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
                .setOngoing(true);

        Notification notification = builder.build();
        startForeground(NOTIFICATION_ID, notification);
        Log.d("MusicService", "Notification updated: " + currentSongTitle + ", isPlaying=" + isPlaying);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MusicService", "onDestroy");
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            } catch (Exception e) {
                Log.e("MusicService", "Error releasing MediaPlayer: " + e.getMessage());
            }
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}