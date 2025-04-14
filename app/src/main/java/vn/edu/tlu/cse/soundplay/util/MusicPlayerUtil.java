package vn.edu.tlu.cse.soundplay.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.cse.soundplay.data.model.Music;
import vn.edu.tlu.cse.soundplay.service.MusicService;
import vn.edu.tlu.cse.soundplay.ui.PlaySongActivity;

public class MusicPlayerUtil {

    public static void openMusicPlayer(Context context, Music music, List<Music> musicList, int position) {
        if (music == null || music.getUrl() == null || music.getUrl().isEmpty()) {
            Log.e("MusicPlayerUtil", "Invalid music or URL: " + (music != null ? music.getTitle() : "null"));
            return;
        }

        if (musicList == null || musicList.isEmpty()) {
            Log.e("MusicPlayerUtil", "Music list is null or empty");
            return;
        }

        // Tạo danh sách bài hát
        ArrayList<String> songUrls = new ArrayList<>();
        ArrayList<String> songTitles = new ArrayList<>();
        ArrayList<String> songThumbnails = new ArrayList<>();
        ArrayList<String> songArtists = new ArrayList<>();
        ArrayList<String> songIds = new ArrayList<>();

        for (Music m : musicList) {
            songUrls.add(m.getUrl() != null ? m.getUrl() : "");
            songTitles.add(m.getTitle() != null ? m.getTitle() : "Không có tiêu đề");
            songThumbnails.add(m.getThumbnail() != null ? m.getThumbnail() : "");
            songArtists.add(m.getArtist() != null ? m.getArtist() : "Unknown Artist");
            songIds.add(m.getId() != null ? m.getId() : "");
        }

        // Khởi động MusicService
        Intent serviceIntent = new Intent(context, MusicService.class);
        serviceIntent.setAction(MusicService.ACTION_PLAY);
        serviceIntent.putExtra("url", music.getUrl());
        serviceIntent.putExtra("title", music.getTitle());
        serviceIntent.putExtra("thumbnail", music.getThumbnail());
        serviceIntent.putExtra("songId", music.getId());
        serviceIntent.putExtra("artist", music.getArtist());
        serviceIntent.putStringArrayListExtra("songUrls", songUrls);
        serviceIntent.putStringArrayListExtra("songTitles", songTitles);
        serviceIntent.putStringArrayListExtra("songThumbnails", songThumbnails);
        serviceIntent.putStringArrayListExtra("songArtists", songArtists);
        serviceIntent.putStringArrayListExtra("songIds", songIds);
        serviceIntent.putExtra("currentIndex", position);
        context.startService(serviceIntent);

        // Mở PlaySongActivity
        Intent playIntent = new Intent(context, PlaySongActivity.class);
        playIntent.putExtra("MUSIC_ID", music.getId());
        playIntent.putExtra("MUSIC_TITLE", music.getTitle());
        playIntent.putExtra("MUSIC_THUMBNAIL", music.getThumbnail());
        playIntent.putExtra("MUSIC_URL", music.getUrl());
        playIntent.putExtra("MUSIC_ARTIST", music.getArtist());
        playIntent.putStringArrayListExtra("SONG_URLS", songUrls);
        playIntent.putStringArrayListExtra("SONG_TITLES", songTitles);
        playIntent.putStringArrayListExtra("SONG_THUMBNAILS", songThumbnails);
        playIntent.putStringArrayListExtra("SONG_ARTISTS", songArtists);
        playIntent.putStringArrayListExtra("SONG_IDS", songIds);
        playIntent.putExtra("SONG_INDEX", position);

        Log.d("MusicPlayerUtil", "Starting PlaySongActivity: title=" + music.getTitle() + ", id=" + music.getId() + ", position=" + position + ", listSize=" + musicList.size());
        context.startActivity(playIntent);
    }
}