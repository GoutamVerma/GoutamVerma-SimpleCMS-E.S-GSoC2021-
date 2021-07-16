package com.lglab.goutam.simple_cms.create.utility.model;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.lglab.goutam.simple_cms.connection.LGCommand;
import com.lglab.goutam.simple_cms.connection.LGConnectionManager;
import com.lglab.goutam.simple_cms.connection.LGConnectionSendFile;
import com.lglab.goutam.simple_cms.create.utility.model.balloon.Balloon;
import com.lglab.goutam.simple_cms.create.utility.model.poi.POI;
import com.lglab.goutam.simple_cms.create.utility.model.shape.Shape;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * This class is in charge of sending the commands to liquid galaxy
 */
public class ActionController {

    private static final String TAG_DEBUG = "ActionController";

    private static ActionController instance = null;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Handler handler2 = new Handler(Looper.getMainLooper());

    public synchronized static ActionController getInstance() {
        if (instance == null)
            instance = new ActionController();
        return instance;
    }

    /**
     * Enforce private constructor
     */
    private ActionController() {}

    /**
     * Move the screen to the poi
     *
     * @param poi      The POI that is going to move
     * @param listener The listener of lgcommand
     */
    public void moveToPOI(POI poi, LGCommand.Listener listener) {
        cleanFileKMLs(0);
        sendPoiToLG(poi, listener);
    }

    /**
     * Create the lGCommand to send to the liquid galaxy
     *
     * @param listener The LGCommand listener
     */
    private void sendPoiToLG(POI poi, LGCommand.Listener listener) {
        LGCommand lgCommand = new LGCommand(ActionBuildCommandUtility.buildCommandPOITest(poi), LGCommand.CRITICAL_MESSAGE, (String result) -> {
            if (listener != null) {
            listener.onResponse(result);
        }
    });
    LGConnectionManager lgConnectionManager = LGConnectionManager.getInstance();
        lgConnectionManager.startConnection();
        lgConnectionManager.addCommandToLG(lgCommand);
    }


    /**
     * First Clean the KML and then do the orbit
     *
     * @param poi      POI
     * @param listener Listener
     */
    public synchronized void cleanOrbit(POI poi, LGCommand.Listener listener) {
        cleanFileKMLs(0);
        orbit(poi, listener);
    }

    /**
     * Do the orbit
     *
     * @param poi      POI
     * @param listener Listener
     */
    public void orbit(POI poi, LGCommand.Listener listener) {
        LGCommand lgCommandOrbit = new LGCommand(ActionBuildCommandUtility.buildCommandOrbit(poi), LGCommand.CRITICAL_MESSAGE, (String result) -> {
            if (listener != null) {
                listener.onResponse(result);
            }
        });
        LGConnectionManager lgConnectionManager = LGConnectionManager.getInstance();
        lgConnectionManager.startConnection();
        lgConnectionManager.addCommandToLG(lgCommandOrbit);

        LGCommand lgCommandWriteOrbit = new LGCommand(ActionBuildCommandUtility.buildCommandWriteOrbit(), LGCommand.CRITICAL_MESSAGE, (String result) -> {
            if (listener != null) {
                listener.onResponse(result);
            }
        });
        lgConnectionManager.addCommandToLG(lgCommandWriteOrbit);

        LGCommand lgCommandStartOrbit = new LGCommand(ActionBuildCommandUtility.buildCommandStartOrbit(), LGCommand.CRITICAL_MESSAGE, (String result) -> {
            if (listener != null) {
                listener.onResponse(result);
            }
        });
        handler.postDelayed(() -> lgConnectionManager.addCommandToLG(lgCommandStartOrbit), 500);
        cleanFileKMLs(46000);
    }

    /**
     * @param balloon  Balloon with the information to build command
     * @param listener listener
     */
    public void sendBalloon(Balloon balloon, LGCommand.Listener listener) {
        cleanFileKMLs(0);

        Uri imageUri = balloon.getImageUri();
        if (imageUri != null) {
            createResourcesFolder();
            String imagePath = balloon.getImagePath();
            LGConnectionSendFile lgConnectionSendFile = LGConnectionSendFile.getInstance();
            lgConnectionSendFile.addPath(imagePath);
            lgConnectionSendFile.startConnection();
        }

        handler.postDelayed(() -> {
            LGCommand lgCommand = new LGCommand(ActionBuildCommandUtility.buildCommandBalloonTest(balloon), LGCommand.CRITICAL_MESSAGE, (String result) -> {
                if (listener != null) {
                    listener.onResponse(result);
                }
            });
            LGConnectionManager lgConnectionManager = LGConnectionManager.getInstance();
            lgConnectionManager.startConnection();
            lgConnectionManager.addCommandToLG(lgCommand);

            handler.postDelayed(this::writeFileBalloonFile, 500);
        }, 500);
    }

    /**
     * @param balloon  Balloon with the information to build command
     * @param listener listener
     */
    public void sendBalloonTestStoryBoard(Balloon balloon, LGCommand.Listener listener) {
        cleanFileKMLs(0);

        LGCommand lgCommand = new LGCommand(ActionBuildCommandUtility.buildCommandBalloonTest(balloon), LGCommand.CRITICAL_MESSAGE, (String result) -> {
            if (listener != null) {
                listener.onResponse(result);
            }
        });
        LGConnectionManager lgConnectionManager = LGConnectionManager.getInstance();
        lgConnectionManager.startConnection();
        lgConnectionManager.addCommandToLG(lgCommand);

        handler.postDelayed(this::writeFileBalloonFile, 500);
    }

    /**
     * Send the image of the balloon
     *
     * @param balloon Balloon
     */
    public void sendImageTestStoryboard(Balloon balloon) {
        Uri imageUri = balloon.getImageUri();
        if (imageUri != null) {
            String imagePath = balloon.getImagePath();
            Log.w(TAG_DEBUG, "Image Path: " + imagePath);
            LGConnectionSendFile lgConnectionSendFile = LGConnectionSendFile.getInstance();
            lgConnectionSendFile.addPath(imagePath);
            lgConnectionSendFile.startConnection();
        }
    }

    /**
     * Paint a balloon with the logos
     */
    public void sendBalloonWithLogos(AppCompatActivity activity) {
        createResourcesFolder();

        String imagePath = getLogosFile(activity);
        LGConnectionSendFile lgConnectionSendFile = LGConnectionSendFile.getInstance();
        lgConnectionSendFile.addPath(imagePath);
        lgConnectionSendFile.startConnection();

        cleanFileKMLs(0);

        handler.postDelayed(() -> {
            LGCommand lgCommand = new LGCommand(ActionBuildCommandUtility.buildCommandBalloonWithLogos(),
                    LGCommand.CRITICAL_MESSAGE, (String result) -> {
            });
            LGConnectionManager lgConnectionManager = LGConnectionManager.getInstance();
            lgConnectionManager.startConnection();
            lgConnectionManager.addCommandToLG(lgCommand);
            }, 2000);
    }

    private String getLogosFile(AppCompatActivity activity) {
        File file = new File(activity.getCacheDir() + "/logos.png");
        if (!file.exists()) {
            try {
                InputStream is = activity.getAssets().open("logos.png");
                int size = is.available();
                Log.w(TAG_DEBUG, "SIZE: " + size);
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(buffer);
                fos.close();

                return file.getPath();
            } catch (Exception e) {
                Log.w(TAG_DEBUG, "ERROR: " + e.getMessage());
            }
        }
        return file.getPath();
    }


    /**
     * Create the Resource folder
     */
    public void createResourcesFolder() {
        LGCommand lgCommand = new LGCommand(ActionBuildCommandUtility.buildCommandCreateResourcesFolder(), LGCommand.CRITICAL_MESSAGE, (String result) -> {
        });
        LGConnectionManager lgConnectionManager = LGConnectionManager.getInstance();
        lgConnectionManager.startConnection();
        lgConnectionManager.addCommandToLG(lgCommand);
    }


    /**
     * Write the shape.kml in the Liquid Galaxy
     */
    private void writeFileShapeFile() {
        LGCommand lgCommand = new LGCommand(ActionBuildCommandUtility.buildWriteShapeFile(),
                LGCommand.CRITICAL_MESSAGE, (String result) -> {
        });
        LGConnectionManager lgConnectionManager = LGConnectionManager.getInstance();
        lgConnectionManager.startConnection();
        lgConnectionManager.addCommandToLG(lgCommand);
    }

    /**
     * Send the command to liquid galaxy
     *
     * @param shape    Shape with the information to build the command
     * @param listener listener
     */
    public void sendShape(Shape shape, LGCommand.Listener listener) {
        cleanFileKMLs(0);

        LGCommand lgCommand = new LGCommand(ActionBuildCommandUtility.buildCommandSendShape(shape), LGCommand.CRITICAL_MESSAGE, (String result) -> {
            if (listener != null) {
                listener.onResponse(result);
            }
        });
        LGConnectionManager lgConnectionManager = LGConnectionManager.getInstance();
        lgConnectionManager.startConnection();
        lgConnectionManager.addCommandToLG(lgCommand);

        handler.postDelayed(this::writeFileShapeFile, 500);
    }

    /**
     * It cleans the kmls.txt file
     */
    public void cleanFileKMLs(int duration) {
        handler.postDelayed(() -> {
            LGCommand lgCommand = new LGCommand(ActionBuildCommandUtility.buildCleanKMLs(),
                    LGCommand.CRITICAL_MESSAGE, (String result) -> {
            });
            LGConnectionManager lgConnectionManager = LGConnectionManager.getInstance();
            lgConnectionManager.startConnection();
            lgConnectionManager.addCommandToLG(lgCommand);
        }, duration);
    }

    /**
     * It cleans the kmls.txt file
     */
    public void cleanQuery(int duration) {
        handler.postDelayed(() -> {
            LGCommand lgCommand = new LGCommand(ActionBuildCommandUtility.buildCleanQuery(),
                    LGCommand.CRITICAL_MESSAGE, (String result) -> {
            });
            LGConnectionManager lgConnectionManager = LGConnectionManager.getInstance();
            lgConnectionManager.startConnection();
            lgConnectionManager.addCommandToLG(lgCommand);
        }, duration);
    }


    /**
     * Send both command to the Liquid Galaxy
     *
     * @param poi     Poi with the location information
     * @param balloon Balloon with the information to paint the balloon
     */
    public void TourGDG(POI poi, Balloon balloon) {
        cleanFileKMLs(0);
        sendBalloonTourGDG(balloon, null);
        sendPoiToLG(poi, null);
    }

    /**
     * Send a balloon in the case of the tour
     *
     * @param balloon  Balloon with the information to build command
     * @param listener listener
     */
    private void sendBalloonTourGDG(Balloon balloon, LGCommand.Listener listener) {
        LGCommand lgCommand = new LGCommand(ActionBuildCommandUtility.buildCommandBalloonTest(balloon), LGCommand.CRITICAL_MESSAGE, (String result) -> {
            if (listener != null) {
                listener.onResponse(result);
            }
        });
        LGConnectionManager lgConnectionManager = LGConnectionManager.getInstance();
        lgConnectionManager.startConnection();
        lgConnectionManager.addCommandToLG(lgCommand);

        handler.postDelayed(this::writeFileBalloonFile, 1000);
    }

    /**
     * Write the file of the balloon
     */
    private void writeFileBalloonFile() {
        LGCommand lgCommand = new LGCommand(ActionBuildCommandUtility.buildWriteBalloonFile(),
                LGCommand.CRITICAL_MESSAGE, (String result) -> {
        });
        LGConnectionManager lgConnectionManager = LGConnectionManager.getInstance();
        lgConnectionManager.startConnection();
        lgConnectionManager.addCommandToLG(lgCommand);
    }

    /**
     * Send the tour kml
     * @param actions Storyboard's actions
     * @param listener Listener
     */
    public void sendTour(List<Action> actions, LGCommand.Listener listener){
        cleanFileKMLs(0);
        handler.postDelayed(() -> {
            LGCommand lgCommand = new LGCommand(ActionBuildCommandUtility.buildCommandTour(actions), LGCommand.CRITICAL_MESSAGE, (String result) -> {
                if (listener != null) {
                    listener.onResponse(result);
                }
            });
            LGConnectionManager lgConnectionManager = LGConnectionManager.getInstance();
            lgConnectionManager.startConnection();
            lgConnectionManager.addCommandToLG(lgCommand);

            LGCommand lgCommandWriteTour = new LGCommand(ActionBuildCommandUtility.buildCommandwriteStartTourFile(), LGCommand.CRITICAL_MESSAGE, (String result) -> {
                if (listener != null) {
                    listener.onResponse(result);
                }
            });
            lgConnectionManager.addCommandToLG(lgCommandWriteTour);

            LGCommand lgCommandStartTour = new LGCommand(ActionBuildCommandUtility.buildCommandStartTour(),
                    LGCommand.CRITICAL_MESSAGE, (String result) -> {
            });
            handler2.postDelayed(() -> lgConnectionManager.addCommandToLG(lgCommandStartTour), 1500);
        }, 1000);
    }


    /**
     * Exit Tour
     */
    public void exitTour(){
        cleanFileKMLs(0);
        LGCommand lgCommand = new LGCommand(ActionBuildCommandUtility.buildCommandExitTour(),
                LGCommand.CRITICAL_MESSAGE, (String result) -> {
        });
        LGConnectionManager lgConnectionManager = LGConnectionManager.getInstance();
        lgConnectionManager.startConnection();
        lgConnectionManager.addCommandToLG(lgCommand);

        LGCommand lgCommandCleanSlaves = new LGCommand(ActionBuildCommandUtility.buildCommandCleanSlaves(),
                LGCommand.CRITICAL_MESSAGE, (String result) -> {
        });
        lgConnectionManager.addCommandToLG(lgCommandCleanSlaves);
    };

}
