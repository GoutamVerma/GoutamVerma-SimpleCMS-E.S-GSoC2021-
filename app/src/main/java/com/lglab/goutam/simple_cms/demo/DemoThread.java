package com.lglab.goutam.simple_cms.demo;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.lglab.goutam.simple_cms.R;
import com.lglab.goutam.simple_cms.create.utility.model.Action;
import com.lglab.goutam.simple_cms.create.utility.model.ActionController;
import com.lglab.goutam.simple_cms.create.utility.model.balloon.Balloon;
import com.lglab.goutam.simple_cms.create.utility.model.movement.Movement;
import com.lglab.goutam.simple_cms.create.utility.model.poi.POI;
import com.lglab.goutam.simple_cms.create.utility.model.poi.POICamera;
import com.lglab.goutam.simple_cms.create.utility.model.shape.Shape;
import com.lglab.goutam.simple_cms.dialog.CustomDialog;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DemoThread implements Runnable {

    private static final String TAG_DEBUG = "TestStoryboardThread";

    private final AtomicBoolean running = new AtomicBoolean(false);
    private List<Action> actions;
    private AppCompatActivity activity;
    private Dialog dialog;


    DemoThread(List<Action> actions, AppCompatActivity activity, Dialog dialog){
        this.actions = actions;
        this.activity = activity;
        this.dialog = dialog;
    }

    void start() {
        Thread worker = new Thread(this);
        worker.start();
    }

    void stop() {
        running.set(false);
    }

    @Override
    public void run() {
        running.set(true);
        ActionController actionController = ActionController.getInstance();
        passingAllImages(actionController);
        Action actionSend;
        int duration = 4000;
        for (int i = 0; i < actions.size() && running.get(); i++) {
            actionSend = actions.get(i);
            if (actionSend instanceof POI) {
                POI poi = (POI) actionSend;
                duration = poi.getPoiCamera().getDuration() * 1000;
                actionController.moveToPOI(poi, null);
            } else if (actionSend instanceof Movement) {
                Movement movement = (Movement) actionSend;
                POI poi = movement.getPoi();
                actionController.orbit(poi, null);

                duration = movement.getDuration() * 1000;
            } else if (actionSend instanceof Balloon) {
                Balloon balloon = (Balloon) actionSend;
                duration = balloon.getDuration() * 1000;
                actionController.sendBalloonTestStoryBoard(balloon, null);
            } else if (actionSend instanceof Shape) {
                Shape shape = (Shape) actionSend;
                duration = shape.getDuration() * 1000;
                actionController.sendShape(shape, null);
            }
            try {
                Log.w(TAG_DEBUG, "DURATION ACTION: " + duration);
                Thread.sleep(duration);
            } catch (Exception e) {
                Log.w(TAG_DEBUG, "ERROR: " + e.getMessage());
            }
        }
        actionController.cleanFileKMLs(1000);
        activity.runOnUiThread(() -> {
            dialog.dismiss();
            CustomDialog dialogFragment = new CustomDialog();
            Bundle bundle = new Bundle();
            bundle.putString("TEXT", activity.getResources().getString(R.string.stop_message));
            dialogFragment.setArguments(bundle);
            dialogFragment.show(activity.getSupportFragmentManager(),"Image Dialog");
        });
    }

    private void passingAllImages(ActionController actionController) {
        actionController.createResourcesFolder();
        Action action;
        for (int i = 0; i < actions.size() && running.get(); i++) {
            action = actions.get(i);
            if (action instanceof Balloon) {
                Balloon balloon = (Balloon) action;
                actionController.sendImageTestStoryboard(balloon);
            }
        }
    }
}
