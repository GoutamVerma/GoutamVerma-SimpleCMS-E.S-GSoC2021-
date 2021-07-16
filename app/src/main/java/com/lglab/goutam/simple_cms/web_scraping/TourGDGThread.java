package com.lglab.goutam.simple_cms.web_scraping;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.lglab.goutam.simple_cms.create.utility.model.ActionController;
import com.lglab.goutam.simple_cms.create.utility.model.balloon.Balloon;
import com.lglab.goutam.simple_cms.create.utility.model.poi.POI;
import com.lglab.goutam.simple_cms.create.utility.model.poi.POICamera;
import com.lglab.goutam.simple_cms.create.utility.model.poi.POILocation;
import com.lglab.goutam.simple_cms.web_scraping.data.GDG;
import com.lglab.goutam.simple_cms.web_scraping.data.InfoScraping;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TourGDGThread implements Runnable {

    private static final String TAG_DEBUG = "TourGDG";

    private final AtomicBoolean running = new AtomicBoolean(false);
    private List<InfoScraping> infoScrapingList;
    private AppCompatActivity  activity;
    private Button buttTour, buttStopTour;


    TourGDGThread(List<InfoScraping> infoScrapingList, AppCompatActivity activity, Button buttTour, Button buttStopTour){
        this.infoScrapingList = infoScrapingList;
        this.activity = activity;
        this.buttTour = buttTour;
        this.buttStopTour = buttStopTour;
    }

    void start() {
        Thread worker = new Thread(this);
        worker.start();
    }

    void stop() {
        running.set(false);
    }

    public void run() {
        running.set(true);
        int duration = 15000;
        ActionController actionController = ActionController.getInstance();
        for(int i = 0; i < infoScrapingList.size() && running.get(); i++){
            Log.w(TAG_DEBUG, "GOING");
            sendInformationLG((GDG) infoScrapingList.get(i), actionController);
            try {
                Log.w(TAG_DEBUG, "DURATION ACTION: " + duration);
                Thread.sleep(duration);
            } catch (Exception e) {
                Log.w(TAG_DEBUG, "ERROR: " + e.getMessage());
            }
        }
        actionController.cleanFileKMLs(0);
        Log.w(TAG_DEBUG, "END");

        activity.runOnUiThread(() -> {
            buttTour.setVisibility(View.VISIBLE);
            buttStopTour.setVisibility(View.INVISIBLE);
        });
    }

    private void sendInformationLG(GDG gdg, ActionController actionController){
        POILocation poiLocation = new POILocation(gdg.getName(), gdg.getLongitude(), gdg.getLatitude(), 3000);
        POICamera poiCamera = new POICamera(10, 0, 3000, "absolute", 4);
        POI poi = new POI().setPoiLocation(poiLocation).setPoiCamera(poiCamera);

        Balloon balloon = new Balloon();
        String description = gdg.getCity() + ", " + gdg.getCountry();
        balloon.setPoi(poi).setDescription(description)
                .setImageUri(null).setImagePath(null).setVideoPath(null).setDuration(15);
        actionController.TourGDG(poi, balloon);
    }
}
