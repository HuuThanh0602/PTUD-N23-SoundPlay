package vn.edu.tlu.cse.ntl.soundplay.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import vn.edu.tlu.cse.ntl.soundplay.data.dao.MusicDAO;
import vn.edu.tlu.cse.ntl.soundplay.data.model.Music;
//
//@Database(entities = {Music.class}, version = 1)
//public abstract class AppDatabase extends RoomDatabase {
//    public abstract MusicDAO musicDAO();
//
//    private static volatile AppDatabase INSTANCE;
//
//    public static AppDatabase getInstance(Context context) {
//        if (INSTANCE == null) {
//            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
//                    AppDatabase.class, "playlist_db").build();
//        }
//        return INSTANCE;
//    }
//}

