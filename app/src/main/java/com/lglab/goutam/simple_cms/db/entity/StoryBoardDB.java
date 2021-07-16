package com.lglab.goutam.simple_cms.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


/**
 * This class represent a StoryBoard in the DB
 */
@Entity
public class StoryBoardDB {

    @PrimaryKey(autoGenerate = true)
    public long storyBoardId;
    public String name;
}
