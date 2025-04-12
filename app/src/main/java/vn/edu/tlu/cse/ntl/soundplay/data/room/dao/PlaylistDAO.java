package vn.edu.tlu.cse.ntl.soundplay.data.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import vn.edu.tlu.cse.ntl.soundplay.data.model.Playlist;

@Dao
public interface PlaylistDAO {

    @Query("SELECT * FROM playlists")
    LiveData<List<Playlist>> getAllPlaylists();

    @Query("SELECT * FROM playlists LIMIT 10")
    LiveData<List<Playlist>> getTop4Playlists();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Playlist playlist);
}
