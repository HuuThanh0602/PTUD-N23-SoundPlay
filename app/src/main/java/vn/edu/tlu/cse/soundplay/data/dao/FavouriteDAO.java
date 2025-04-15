package vn.edu.tlu.cse.soundplay.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import vn.edu.tlu.cse.soundplay.data.model.Favourite;

@Dao
public interface FavouriteDAO {
    @Insert
    void insert(Favourite favourite);

    @Delete
    void delete(Favourite favourite);

    @Query("SELECT * FROM favourites WHERE id = :id")
    Favourite findById(String id); // Đổi từ findByMusicId

    @Query("SELECT * FROM favourites")
    LiveData<List<Favourite>> getAllFavouritesLive();
}