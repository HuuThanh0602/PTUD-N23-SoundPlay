package vn.edu.tlu.cse.ntl.soundplay.data.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import vn.edu.tlu.cse.ntl.soundplay.data.model.Playlist;
import vn.edu.tlu.cse.ntl.soundplay.data.room.dao.PlaylistDAO;

@Database(entities = {Playlist.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PlaylistDAO playlistDAO();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "playlist_db").build();
        }
        return INSTANCE;
    }
}

