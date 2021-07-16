package com.lglab.goutam.simple_cms.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.lglab.goutam.simple_cms.db.dao.StoryBoardDao;
import com.lglab.goutam.simple_cms.db.entity.StoryBoardDB;
import com.lglab.goutam.simple_cms.db.entity.StoryBoardJsonDB;

/**
 * Singleton of connection to the database
 */
@Database(entities = {StoryBoardDB.class, StoryBoardJsonDB.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract StoryBoardDao storyBoardDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =  Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "story_board").allowMainThreadQueries().build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

}
