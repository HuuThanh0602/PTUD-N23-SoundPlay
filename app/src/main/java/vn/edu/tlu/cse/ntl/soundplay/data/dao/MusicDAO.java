package vn.edu.tlu.cse.ntl.soundplay.data.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import vn.edu.tlu.cse.ntl.soundplay.data.model.Music;

@Dao
public interface MusicDAO {
    @Query("SELECT * FROM musics ORDER BY rowid DESC")
    List<Music> getRecentPlays();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Music music);

    @Query("DELETE FROM musics")
    void clearAll();
}



