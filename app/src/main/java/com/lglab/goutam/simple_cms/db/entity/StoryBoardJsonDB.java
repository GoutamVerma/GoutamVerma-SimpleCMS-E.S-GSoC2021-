package com.lglab.goutam.simple_cms.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class StoryBoardJsonDB {

    @PrimaryKey(autoGenerate = true)
    public long storyBoardId;
    public long storyBoardDBOwnerId;
    public String json;
}
