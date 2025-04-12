package vn.edu.tlu.cse.ntl.soundplay.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executors;

import vn.edu.tlu.cse.ntl.soundplay.data.model.Playlist;
import vn.edu.tlu.cse.ntl.soundplay.data.room.AppDatabase;
import vn.edu.tlu.cse.ntl.soundplay.data.room.dao.PlaylistDAO;

public class PlaylistRepository {
    private PlaylistDAO playlistDAO;
    private LiveData<List<Playlist>> allPlaylists;

    public PlaylistRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        playlistDAO = db.playlistDAO();
        allPlaylists = playlistDAO.getAllPlaylists();
    }

    public LiveData<List<Playlist>> getAllPlaylists() {
        return allPlaylists;
    }

    public void insert(Playlist playlist) {
        Executors.newSingleThreadExecutor().execute(() -> {
            playlistDAO.insert(playlist);
        });
    }
}
