package vn.edu.tlu.cse.soundplay.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import vn.edu.tlu.cse.soundplay.data.dao.FavouriteDAO;
import vn.edu.tlu.cse.soundplay.data.dao.MusicDAO;
import vn.edu.tlu.cse.soundplay.data.model.Favourite;
import vn.edu.tlu.cse.soundplay.data.model.Music;

@Database(entities = {Music.class, Favourite.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MusicDAO musicDAO();
    public abstract FavouriteDAO favouriteDAO();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "music_db2")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}