package vn.edu.tlu.cse.hyn.soundplay.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import vn.edu.tlu.cse.hyn.soundplay.data.model.Favourite;

@Dao
public interface favouriteDAO {

    @Insert
    void insert(Favourite favourite);

    @Delete
    void delete(Favourite favourite);

    @Query("SELECT * FROM favourites")
    LiveData<List<Favourite>> getAllFavouritesLive();

    @Query("SELECT * FROM favourites WHERE musicId = :musicId LIMIT 1")
    Favourite findByMusicId(String musicId);
}
