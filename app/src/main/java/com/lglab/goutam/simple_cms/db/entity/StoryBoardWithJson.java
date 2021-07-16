package com.lglab.goutam.simple_cms.db.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

public class StoryBoardWithJson {
    @Embedded
    public StoryBoardDB storyBoardDB;
    @Relation(
            parentColumn = "storyBoardId",
            entityColumn = "storyBoardDBOwnerId"
    )
    public StoryBoardJsonDB storyBoardJsonDB;
}
