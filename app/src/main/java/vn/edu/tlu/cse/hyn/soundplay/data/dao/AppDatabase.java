package vn.edu.tlu.cse.hyn.soundplay.data.dao;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import vn.edu.tlu.cse.hyn.soundplay.data.model.Favourite;

@Database(entities = {Favourite.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract favouriteDAO favouriteDAO();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "soundplay_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
