package com.lglab.goutam.simple_cms.db.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.lglab.goutam.simple_cms.db.entity.StoryBoardDB;
import com.lglab.goutam.simple_cms.db.entity.StoryBoardJsonDB;
import com.lglab.goutam.simple_cms.db.entity.StoryBoardWithJson;

import java.util.List;

/**
 * This class is the dao to the storyboard
 */
@Dao
public abstract class StoryBoardDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insertStoryBoard(StoryBoardDB storyBoardDB);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertStoryBoardJsonDB(StoryBoardJsonDB storyBoardJsonDB);

    /**
     * Insert a storyboard
     * @param storyBoardDB StoryBoardDb
     * @param storyBoardJson storyBoardJson
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertStoryBoardWithJson(StoryBoardDB storyBoardDB, String storyBoardJson){
        final long storyBoardId = insertStoryBoard(storyBoardDB);

        StoryBoardJsonDB storyBoardJsonDB = new StoryBoardJsonDB();
        storyBoardJsonDB.storyBoardDBOwnerId = storyBoardId;
        storyBoardJsonDB.json = storyBoardJson;
        insertStoryBoardJsonDB(storyBoardJsonDB);
    }

    /**
     * Update the storyboard
     * @param id Id of the current storyboard
     * @param storyBoardJson json to be update
     */
    @Transaction
    @Update
    public void updateStoryBoard(long id, String storyBoardJson){

        StoryBoardWithJson storyBoardWithJson = getStoryBoardWithJson(id);

        deleteStoryBoardJson(storyBoardWithJson.storyBoardJsonDB);
        deleteStoryBoardDB(storyBoardWithJson.storyBoardDB);
        insertStoryBoardWithJson(storyBoardWithJson.storyBoardDB, storyBoardJson);
    }

    @Transaction
    @Query("SELECT * FROM StoryBoardDB")
    public abstract List<StoryBoardDB> getStoryBoards();

    @Transaction
    @Query("SELECT * FROM StoryBoardDB WHERE storyBoardId = :id")
    public abstract StoryBoardWithJson getStoryBoardWithJson(long id);

    /**
     * Delete the storyboards in the bd
     * @param storyBoardDB  StoryBoardDB
     */
    @Delete
    public void deleteStoryBoardDBWithJson(StoryBoardDB storyBoardDB){
        StoryBoardWithJson storyBoardWithJson = getStoryBoardWithJson(storyBoardDB.storyBoardId);
        deleteStoryBoardJson(storyBoardWithJson.storyBoardJsonDB);
        deleteStoryBoardDB(storyBoardWithJson.storyBoardDB);
    }

    @Delete
    public abstract void deleteStoryBoardDB(StoryBoardDB storyBoardDB);

    @Delete
    public abstract void deleteStoryBoardJson(StoryBoardJsonDB storyBoardJsonDB);

}
