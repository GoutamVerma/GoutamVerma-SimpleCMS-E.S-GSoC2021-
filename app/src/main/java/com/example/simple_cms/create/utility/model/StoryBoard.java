package com.example.simple_cms.create.utility.model;

import android.util.Log;

import com.example.simple_cms.create.utility.IJsonPacker;
import com.example.simple_cms.create.utility.model.balloon.Balloon;
import com.example.simple_cms.create.utility.model.movement.Movement;
import com.example.simple_cms.create.utility.model.poi.POI;
import com.example.simple_cms.create.utility.model.shape.Shape;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is in charge of representing a storyboard
 */
public class StoryBoard implements IJsonPacker {

    private static final String TAG_DEBUG = "StoryBoard";

    private long storyBoardId;
    private String name;
    private List<Action> actions;

    public StoryBoard() {}

    public StoryBoard(long storyBoardId, String name, List<Action> actions) {
        this.storyBoardId = storyBoardId;
        this.name = name;
        this.actions = actions;
    }


    public long getStoryBoardId() {
        return storyBoardId;
    }

    public void setStoryBoardId(long storyBoardId) {
        this.storyBoardId = storyBoardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public static List<StoryBoard> getStoryBoardsWithOutActions(List<com.example.simple_cms.db.entity.StoryBoard> storyBoardsDB) {
        List<StoryBoard> storyBoards = new ArrayList<>();

        com.example.simple_cms.db.entity.StoryBoard storyBoardDB;
        for (int i = 0; i < storyBoardsDB.size(); i++) {
            storyBoardDB = storyBoardsDB.get(i);

            StoryBoard storyBoard = new StoryBoard();
            storyBoard.storyBoardId = storyBoardDB.storyBoardId;
            storyBoard.name = storyBoardDB.nameStoryBoard;
            storyBoard.actions = new ArrayList<>();

            storyBoards.add(storyBoard);
        }

        return storyBoards;
    }

    @Override
    public JSONObject pack() throws JSONException {
        JSONObject obj = new JSONObject();

        obj.put("storyboard_id", storyBoardId);
        obj.put("name", name);

        JSONArray jsonActions = new JSONArray();
        Action action;
        for (int i = 0; i < actions.size(); i++) {
            action = actions.get(i);
            if (action instanceof POI) {
                POI poi = (POI) action;
                jsonActions.put(poi.pack());
            } else if (action instanceof Movement) {
                Movement movement = (Movement) action;
                jsonActions.put(movement.pack());
            } else if (action instanceof Balloon) {
                Balloon balloon = (Balloon) action;
                jsonActions.put(balloon.pack());
            } else if (action instanceof Shape) {
                Shape shape = (Shape) action;
                jsonActions.put(shape.pack());
            } else {
                Log.w(TAG_DEBUG, "ERROR TYPE");
            }
        }

        obj.put("jsonActions", jsonActions);

        return obj;
    }

    @Override
    public StoryBoard unpack(JSONObject obj) throws JSONException {

        storyBoardId = obj.getLong("storyboard_id");
        name = obj.getString("name");

        JSONArray jsonActions = obj.getJSONArray("jsonActions");
        List<Action> arrayActions = new ArrayList<>();
        JSONObject actionJson;
        int type;
        for (int i = 0; i < jsonActions.length(); i++) {
            actionJson = jsonActions.getJSONObject(i);
            type = actionJson.getInt("type");
            if (type == ActionIdentifier.LOCATION_ACTIVITY.getId()) {
                POI poi = new POI();
                arrayActions.add(poi.unpack(actionJson));
            } else if (type == ActionIdentifier.MOVEMENT_ACTIVITY.getId()) {
                Movement movement = new Movement();
                arrayActions.add(movement.unpack(actionJson));
            } else if (type == ActionIdentifier.BALLOON_ACTIVITY.getId()) {
                Balloon balloon = new Balloon();
                arrayActions.add(balloon.unpack(actionJson));
            } else if (type == ActionIdentifier.SHAPES_ACTIVITY.getId()) {
                Shape shape = new Shape();
                arrayActions.add(shape.unpack(actionJson));
            } else {
                Log.w(TAG_DEBUG, "ERROR TYPE");
            }
        }

        actions = arrayActions;

        return this;
    }
}