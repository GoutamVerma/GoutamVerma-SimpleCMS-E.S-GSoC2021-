package com.lglab.goutam.simple_cms.create;

import android.Manifest;
import android.R.layout;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.client.http.ByteArrayContent;
import com.lglab.goutam.simple_cms.R;
import com.lglab.goutam.simple_cms.create.action.CreateStoryBoardActionBalloonActivity;
import com.lglab.goutam.simple_cms.create.action.CreateStoryBoardActionLocationActivity;
import com.lglab.goutam.simple_cms.create.action.CreateStoryBoardActionMovementActivity;
import com.lglab.goutam.simple_cms.create.action.CreateStoryBoardActionShapeActivity;
import com.lglab.goutam.simple_cms.create.utility.adapter.ActionRecyclerAdapter;
import com.lglab.goutam.simple_cms.create.utility.connection.LGConnectionTest;
import com.lglab.goutam.simple_cms.create.utility.model.Action;
import com.lglab.goutam.simple_cms.create.utility.model.ActionController;
import com.lglab.goutam.simple_cms.create.utility.model.ActionIdentifier;
import com.lglab.goutam.simple_cms.create.utility.model.StoryBoard;
import com.lglab.goutam.simple_cms.create.utility.model.movement.Movement;
import com.lglab.goutam.simple_cms.create.utility.model.balloon.Balloon;
import com.lglab.goutam.simple_cms.create.utility.model.poi.POI;
import com.lglab.goutam.simple_cms.create.utility.model.shape.Shape;
import com.lglab.goutam.simple_cms.db.AppDatabase;
import com.lglab.goutam.simple_cms.db.entity.StoryBoardDB;
import com.lglab.goutam.simple_cms.db.entity.StoryBoardJsonDB;
import com.lglab.goutam.simple_cms.db.entity.StoryBoardWithJson;
import com.lglab.goutam.simple_cms.dialog.CustomDialogUtility;
import com.lglab.goutam.simple_cms.export_esp.export_esp;
import com.lglab.goutam.simple_cms.export_esp.record;
import com.lglab.goutam.simple_cms.my_storyboards.StoryBoardConstant;
import com.lglab.goutam.simple_cms.utility.ConstantPrefs;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.lglab.goutam.simple_cms.dialog.CustomDialogUtility.*;

/**
 * This activity is in charge of creating the storyboards with the respective different actions
 */
public class CreateStoryBoardActivity extends ExportGoogleDriveActivity implements
        ActionRecyclerAdapter.OnNoteListener {
    CreateStoryBoardActionLocationActivity a = new CreateStoryBoardActionLocationActivity();
    HashMap<String, List<String>> people = a.getPeopleMap();
    private static final String TAG_DEBUG = "CreateStoryBoardActivity";
    private static int BUFFER_SIZE = 6 * 1024;
    List<String> filesListInDir = new ArrayList<String>();
    private static final int PERMISSION_CODE_PACK = 1000;
    private static final long MAX_SIZE = 5242880;

    private RecyclerView mRecyclerView;
    List<Action> actions = new ArrayList<>();
    private boolean isPOI = false;
    private long currentStoryBoardId = Long.MIN_VALUE;
    private String currentStoryBoardGoogleDriveID = null;
    private Handler handler = new Handler();
    private Handler handler2 = new Handler();
    private boolean isStopStoryboard = false;


    private EditText storyBoardName;
    private Button buttCreate, buttTest, buttStopTest;
    private TextView connectionStatus, sizeFile;
    private AutoCompleteTextView actv;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_storyboard);

        mRecyclerView = findViewById(R.id.my_recycler_view);

        View topBar = findViewById(R.id.top_bar);
        buttCreate = topBar.findViewById(R.id.butt_create_menu);
        storyBoardName = findViewById(R.id.text_name);
        sizeFile = findViewById(R.id.size_file);
        //Charging data for other activitie
        buttTest = findViewById(R.id.butt_test);
        buttStopTest = findViewById(R.id.butt_stop);
        Button buttLocation = findViewById(R.id.butt_location);
        Button buttMovements = findViewById(R.id.butt_movements);
        Button buttBalloon = findViewById(R.id.butt_balloon);
        Button buttShapes = findViewById(R.id.butt_shapes);
        Button buttDelete = findViewById(R.id.butt_delete);
        Button buttSaveLocally = findViewById(R.id.butt_save_locally);
        Button buttSaveGoogleDrive = findViewById(R.id.butt_save_on_google_drive);
        Button exportEsp = findViewById(R.id.exportesp);
        connectionStatus = findViewById(R.id.connection_status);

        //MyStoryboards
        long storyboardID = getIntent().getLongExtra(StoryBoardConstant.STORY_BOARD_ID.name(), Long.MIN_VALUE);

        //Google Drive
        currentStoryBoardGoogleDriveID = getIntent().getStringExtra(StoryBoardConstant.STORY_BOARD_JSON_ID.name());
        if (storyboardID != Long.MIN_VALUE) {
            currentStoryBoardId = storyboardID;
            chargeStoryBoardLocally();
        } else if (currentStoryBoardGoogleDriveID != null) {
            chargeStoryBoardJsonGoogleDrive();
        } else {
            loadDataJson();
        }

        buttLocation.setOnClickListener((view) -> {
            Intent intent = new Intent(getApplicationContext(), CreateStoryBoardActionLocationActivity.class);
            intent.putExtra(ActionIdentifier.POSITION.name(), actions.size());
            startActivityForResult(intent, ActionIdentifier.LOCATION_ACTIVITY.getId());
        });

        buttMovements.setOnClickListener((view) -> {
            Intent intent = new Intent(getApplicationContext(), CreateStoryBoardActionMovementActivity.class);
            if (!isPOI) {
                CustomDialogUtility.showDialog(CreateStoryBoardActivity.this,
                        getResources().getString(R.string.You_need_a_location_to_create_a_movement));
            } else {
                POI lastPOI = findLastPOI(actions.size());
                intent.putExtra(ActionIdentifier.LOCATION_ACTIVITY.name(), lastPOI);
                intent.putExtra(ActionIdentifier.POSITION.name(), actions.size());
                startActivityForResult(intent, ActionIdentifier.MOVEMENT_ACTIVITY.getId());
            }
        });

        buttBalloon.setOnClickListener((view) -> {
            Intent intent = new Intent(getApplicationContext(), CreateStoryBoardActionBalloonActivity.class);
            if (!isPOI) {
                CustomDialogUtility.showDialog(CreateStoryBoardActivity.this,
                        getResources().getString(R.string.You_need_a_location_to_create_a_balloon));
            } else {
                POI lastPOI = findLastPOI(actions.size());
                intent.putExtra(ActionIdentifier.LOCATION_ACTIVITY.name(), lastPOI);
                intent.putExtra(ActionIdentifier.POSITION.name(), actions.size());
                startActivityForResult(intent, ActionIdentifier.BALLOON_ACTIVITY.getId());
            }
        });

        buttShapes.setOnClickListener((view) -> {
            Intent intent = new Intent(getApplicationContext(), CreateStoryBoardActionShapeActivity.class);
            if (!isPOI) {
                CustomDialogUtility.showDialog(CreateStoryBoardActivity.this,
                        getResources().getString(R.string.You_need_a_location_to_create_a_shape));
            } else {
                POI lastPOI = findLastPOI(actions.size());
                intent.putExtra(ActionIdentifier.LOCATION_ACTIVITY.name(), lastPOI);
                intent.putExtra(ActionIdentifier.POSITION.name(), actions.size());
                startActivityForResult(intent, ActionIdentifier.SHAPES_ACTIVITY.getId());
            }
        });

        buttDelete.setOnClickListener((view) -> deleteStoryboard());

        buttSaveLocally.setOnClickListener((view) -> saveStoryboardLocally());

        buttSaveGoogleDrive.setOnClickListener((view) -> saveStoryboardGoogleDrive());

        buttTest.setOnClickListener((view) -> testStoryboard());

        buttStopTest.setOnClickListener((view -> stopTestStoryBoard()));

        exportEsp.setOnClickListener((view -> {
            try {
                popup(people);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));


        initRecyclerView();

        changeButtonClickableBackgroundColor();
    }

    public void popup(Map<String, List<String>> position_data) throws IOException {
        String Filename = storyBoardName.getText().toString();
        if(!isPOI){
            CustomDialogUtility.showDialog(CreateStoryBoardActivity.this,
                    getResources().getString(R.string.You_need_a_location_to_export_esp));
        }
        else if (Filename.equals("")) {
            CustomDialogUtility.showDialog(CreateStoryBoardActivity.this,
                    getResources().getString(R.string.You_need_a_name_to_export_esp));
        }
        else {
            Collection getters = position_data.values();
            Iterator j = getters.iterator();
            String message= "";
            while (j.hasNext()){
                List positioning = (List) j.next();
                if(positioning.contains("None")) {
                    message = message + positioning.get(6);
                }
            }
            char[] chars =message.toCharArray();
            Arrays.sort(chars);
            String sorted = new String(chars);
            if(message!="") {
               try {
                   AlertDialog.Builder builder = new AlertDialog.Builder(CreateStoryBoardActivity.this);
                   builder.setMessage("We didn't find any esp mode on locations " +
                           sorted.replace("", ", ").trim() + "\nDo you still wants to continue?");
                   builder.setTitle("Alert !");
                   builder.setCancelable(false);
                   builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           export(position_data);
                       }
                   });
                   builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           dialog.cancel();
                       }
                   });
                   AlertDialog alertDialog = builder.create();
                   alertDialog.show();
               }
               catch (Exception e) { e.printStackTrace(); }
            }
            else{
                try{
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateStoryBoardActivity.this);
                builder.setMessage(R.string.only_going_to_export_esp);
                builder.setTitle("Alert !");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        export(position_data);
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
                catch (Exception e) {e.printStackTrace();}
            }
        }
    }
    private void export(Map<String, List<String>> position_data){
        try{
            String Filename = storyBoardName.getText().toString();
            File files = new File("/data/user/0/com.lglab.goutam.simple_cms_es/cache/"+Filename);
            if(files.isDirectory()){
                Log.d("file to deelete hori hai","piicture kja hjai");
                files.delete();
            }
            Collection getter = position_data.values();
            Iterator i = getter.iterator();
            int y = 0;
            int a = getter.size();
            Log.d("size of iterator is ", String.valueOf(a));
            while (y<a){
                Log.d("looping area", String.valueOf(position_data.get(y)));
                y++;
                List items = (List) i.next();
                if (items.contains("Orbit")) {
                    saveFile(String.valueOf(items.get(4)),
                            export_esp.orbit(Double.parseDouble(String.valueOf(items.get(1))), //here items.get
                                    Double.parseDouble(String.valueOf(items.get(0))),
                                    Integer.parseInt(String.valueOf(items.get(5))),
                                    String.valueOf(items.get(4))), Filename);
                }
                else if (items.contains("Spiral")) {
                    saveFile(String.valueOf(items.get(4)),
                            export_esp.spiral(Double.parseDouble(String.valueOf(items.get(0))),
                                    Double.parseDouble(String.valueOf(items.get(1))),
                                    Integer.parseInt(String.valueOf(items.get(5))),
                                    String.valueOf(items.get(4))), Filename);
                }
                else if (items.contains("Zoom-To")) {
                    saveFile(String.valueOf(items.get(4)),
                            export_esp.ZoomTo(Double.parseDouble(String.valueOf(items.get(0))),
                                    Double.parseDouble(String.valueOf(items.get(1))),
                                    Double.parseDouble(String.valueOf(items.get(2))),
                                    Integer.parseInt(String.valueOf(items.get(5))),
                                    String.valueOf(items.get(4))), Filename);
                }
            }
            zipDirectory(Filename);
            File file = new File("/data/user/0/com.lglab.goutam.simple_cms_es/cache/"+Filename);
            file.deleteOnExit();
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
            CustomDialogUtility.showDialog(CreateStoryBoardActivity.this,getResources().getString(R.string.You_need_to_select_mode_of_esp));

        }
    }

    private void zipDirectory(String Filename) {
        try {
            File dir = new File("/data/user/0/com.lglab.goutam.simple_cms_es/cache/"+Filename+"/");
            String zipDirName = "/data/user/0/com.lglab.goutam.simple_cms_es/cache/"+Filename+".zip";
            Log.d("filename",Filename);
            populateFilesList(dir);
            File zipFile = new File(zipDirName);
            FileOutputStream fos = new FileOutputStream(zipDirName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            for(String filePath : filesListInDir){
                System.out.println("Zipping "+filePath);
                ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length()+1, filePath.length()));
                zos.putNextEntry(ze);
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            fos.close();
            shareZIP(zipFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method populates all the files in a directory to a List
     * @param dir
     * @throws IOException
     */
    private void populateFilesList(File dir) throws IOException {
        File[] files = dir.listFiles();
        for(File file : files){
            if(file.isFile()) filesListInDir.add(file.getAbsolutePath());
            else populateFilesList(file);
        }
    }
    public void saveFile(String name,String data,String Filename){
        try {
            File file = new File(getCacheDir(),Filename);
            if(!file.exists()) {
                file.mkdirs();
            }
            File esp = new File(file,name+".esp");
            FileWriter outputStream = new FileWriter(esp);
            outputStream.write(data);
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDataJson() {
        SharedPreferences sharedPreferences = getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE);
        String storyBoardJson = sharedPreferences.getString(ConstantPrefs.STORY_BOARD_JSON.name(), "");
        if (!storyBoardJson.equals("")) {
            try {
                StoryBoard storyBoard = new StoryBoard();
                JSONObject jsonStoryBoard = new JSONObject(storyBoardJson);
                storyBoard.unpackStoryBoard(jsonStoryBoard, getApplicationContext());
                List<Action> actionsSaved = storyBoard.getActions();
                storyBoardName.setText(storyBoard.getName());
                isPOI = false;
                if (actionsSaved.size() > 0) {
                    actions = actionsSaved;
                    isPOI = true;
                }
                currentStoryBoardGoogleDriveID = storyBoard.getStoryBoardFileId();
                currentStoryBoardId = storyBoard.getStoryBoardId();
            } catch (JSONException jsonException) {
                Log.w(TAG_DEBUG, "ERROR CONVERTING JSON: " + jsonException);
            }
        }
    }

    /**
     * Test the connection and then do the tour
     */
    private void testStoryboard() {
        AtomicBoolean isConnected = new AtomicBoolean(false);
        LGConnectionTest.testPriorConnection(this, isConnected);
        SharedPreferences sharedPreferences = getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE);
        handler.postDelayed(() -> {
            if (isConnected.get()) {
                isStopStoryboard = false;
                Dialog dialog = getDialog(CreateStoryBoardActivity.this, "Setting Files");
                dialog.show();
                handler.postDelayed(() -> {
                    buttTest.setVisibility(View.INVISIBLE);
                    buttStopTest.setVisibility(View.VISIBLE);
                    ActionController actionController = ActionController.getInstance();
                    passingAllImages(actionController);
                    actionController.sendTour(actions, null);
                    CustomDialogUtility.showDialog(CreateStoryBoardActivity.this, "Testing the storyboard.");
                    dialog.dismiss();
                    handler2.postDelayed(() ->{
                        if(!isStopStoryboard){
                            Log.w(TAG_DEBUG, "WHAT");
                            stopTestStoryBoard();
                            buttTest.setVisibility(View.VISIBLE);
                            buttStopTest.setVisibility(View.INVISIBLE);
                        }
                    }, calculateStoryboardDuration() * 1000 + 1000);
                }, 200);
            }
            loadConnectionStatus(sharedPreferences);
        }, 1200);
    }

    private long calculateStoryboardDuration() {
        long duration = 0;
        Action action;
        for(int i = 0; i < actions.size(); i++){
            action = actions.get(i);
            if (action instanceof POI) {
                POI poi = (POI) action;
                duration = duration + poi.getPoiCamera().getDuration();
            } else if (action instanceof Movement) {
                Movement movement = (Movement) action;
                if(movement.isOrbitMode()) duration = duration + 45;
                else  duration = duration + movement.getDuration();
            } else if (action instanceof Balloon) {
                Balloon balloon = (Balloon) action;
                duration = duration + balloon.getDuration();
            }
        }
        Log.w(TAG_DEBUG, "duration: " + duration);
        return duration;
    }

    /**
     * Pass all the images of the storyboard to the Liquid Galaxy
     * @param actionController ActionController
     */
    private void passingAllImages(ActionController actionController) {
        actionController.createResourcesFolder();
        Action action;
        for (int i = 0; i < actions.size(); i++) {
            action = actions.get(i);
            if (action instanceof Balloon) {
                Balloon balloon = (Balloon) action;
                actionController.sendImageTestStoryboard(balloon);
            }
            try {
                Thread.sleep(500);
            }catch (Exception e){
                Log.w(TAG_DEBUG, "ERROR SENDING FILES: " + e.getMessage());
            }
        }
    }


    /**
     * Stop the testing of the storyboard
     */
    private void stopTestStoryBoard() {
        ActionController actionController = ActionController.getInstance();
        actionController.exitTour();
        buttTest.setVisibility(View.VISIBLE);
        buttStopTest.setVisibility(View.INVISIBLE);
        isStopStoryboard = true;
    }

    /**
     * Set the connection status on the view
     */
    private void loadConnectionStatus(SharedPreferences sharedPreferences) {
        boolean isConnected = sharedPreferences.getBoolean(ConstantPrefs.IS_CONNECTED.name(), false);
        if (isConnected) {
            connectionStatus.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_status_connection_green));
        } else {
            connectionStatus.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_status_connection_red));
        }
    }


    /**
     * It charge the storyboard that was selected from google drive
     */
    private void chargeStoryBoardJsonGoogleDrive() {
        unpackStoryBoardGoogleDrive();
    }

    /**
     * It unpack storyboard that was selected from google drive
     */
    private void unpackStoryBoardGoogleDrive() {
        SharedPreferences sharedPreferences = getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE);
        String storyBoardJson = sharedPreferences.getString(ConstantPrefs.STORY_BOARD_JSON.name(), "");
        unpackStoryBoard(storyBoardJson);
    }


    /**
     * Charge the storyboard that was selected locally
     */
    private void chargeStoryBoardLocally() {
        unpackStoryBoardLocally();
    }

    /**
     * Get the storyboard from my storyboards and passing to unpack
     */
    private void unpackStoryBoardLocally() {
        try {
            AppDatabase db = AppDatabase.getAppDatabase(this);
            StoryBoardWithJson storyBoardWithJson = db.storyBoardDao().getStoryBoardWithJson(currentStoryBoardId);
            StoryBoardJsonDB storyBoardJsonDB = storyBoardWithJson.storyBoardJsonDB;
            currentStoryBoardId = storyBoardJsonDB.storyBoardDBOwnerId;
            unpackStoryBoard(storyBoardJsonDB.json);
        } catch (Exception e) {
            CustomDialogUtility.showDialog(CreateStoryBoardActivity.this,
                    getResources().getString(R.string.error_my_storyboards));
            Log.w(TAG_DEBUG, "ERROR DB: " + e.getMessage());
        }
    }


    /**
     * Un pack and charge the storyboard
     *
     * @param storyBoardJson the storyboard
     */
    private void unpackStoryBoard(String storyBoardJson) {
        Dialog dialog = getDialog(CreateStoryBoardActivity.this, getResources().getString(R.string.create_message_charging_storyboard));
        dialog.show();
        try {
            StoryBoard storyBoard = new StoryBoard();
            JSONObject jsonStoryBoard = new JSONObject(storyBoardJson);
            storyBoard.unpackStoryBoard(jsonStoryBoard, getApplicationContext());
            actions = storyBoard.getActions();
            storyBoardName.setText(storyBoard.getName());
            isPOI = actions.size() > 0;
            dialog.dismiss();
        } catch (JSONException jsonException) {
            Log.w(TAG_DEBUG, "ERROR CONVERTING JSON: " + jsonException);
            dialog.dismiss();
            CustomDialogUtility.showDialog(CreateStoryBoardActivity.this,
                    getResources().getString(R.string.create_message_error_charging_storyboard));
        }
    }

    /**
     * Save or update the storyboard Google Drive
     */
    private void saveStoryboardGoogleDrive() {
        if (isLogIn()) {
            String name = storyBoardName.getText().toString();
            if (name.equals("")) {
                CustomDialogUtility.showDialog(CreateStoryBoardActivity.this,
                        getResources().getString(R.string.You_need_a_name_to_create_a_story_board));
            } else {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE_PACK);
                } else {
                    packSaveStoryboard();
                }
            }
        } else {
            CustomDialogUtility.showDialog(CreateStoryBoardActivity.this,
                    getResources().getString(R.string.message_you_need_log_in));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE_PACK) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                packSaveStoryboard();
            } else {
                CustomDialogUtility.showDialog(this, getResources().getString(R.string.alert_permission_denied_save));
            }
        }
    }

    private void packSaveStoryboard() {
        try {
            String name = storyBoardName.getText().toString();
            com.lglab.goutam.simple_cms.create.utility.model.StoryBoard storyBoard = new com.lglab.goutam.simple_cms.create.utility.model.StoryBoard();
            storyBoard.setStoryBoardFileId(currentStoryBoardGoogleDriveID);
            storyBoard.setName(name);
            storyBoard.setActions(actions);
            JSONObject jsonStoryboard = storyBoard.pack();
            requestSignIn(jsonStoryboard.toString(), storyBoard.getNameForExporting(), currentStoryBoardGoogleDriveID, this);
        } catch (JSONException e) {
            Log.w(TAG_DEBUG, "JSON ERROR: " + e.getMessage());
        }
    }

    /**
     * Save or update the storyboard locally
     */
    private void saveStoryboardLocally() {
        String name = storyBoardName.getText().toString();
        if (name.equals("")) {
            CustomDialogUtility.showDialog(CreateStoryBoardActivity.this,
                    getResources().getString(R.string.You_need_a_name_to_create_a_story_board));
        } else {
            try {
                AppDatabase db = AppDatabase.getAppDatabase(this);
                if (currentStoryBoardId != Long.MIN_VALUE) {
                    try {
                        db.storyBoardDao().updateStoryBoard(currentStoryBoardId, getStringStoryboard(name));
                        CustomDialogUtility.showDialog(CreateStoryBoardActivity.this,
                                getResources().getString(R.string.alert_update_story_board));
                    } catch (Exception e) {
                        Log.w(TAG_DEBUG, "Create a new Storyboard");
                        saveStoryBoardRoom(name, db);
                        CustomDialogUtility.showDialog(CreateStoryBoardActivity.this,
                                getResources().getString(R.string.alert_save_story_board));
                    }
                } else {
                    saveStoryBoardRoom(name, db);
                    CustomDialogUtility.showDialog(CreateStoryBoardActivity.this,
                            getResources().getString(R.string.alert_save_story_board));
                }
            } catch (Exception e) {
                Log.w(TAG_DEBUG, "ERROR DB SAVE: " + e.getMessage());
            }
        }
    }

    private String getStringStoryboard(String name) throws JSONException {
        StoryBoard storyBoard = new StoryBoard();
        storyBoard.setName(name);
        storyBoard.setActions(actions);
        return storyBoard.pack().toString();
    }

    /**
     * This is in charge of saving a new storyboard to the db
     *
     * @param name Name of the storyBoard
     * @param db   Connection to db
     */
    private void saveStoryBoardRoom(String name, AppDatabase db) throws JSONException {
        StoryBoardDB storyBoardDB = new StoryBoardDB();
        storyBoardDB.name = name;
        db.storyBoardDao().insertStoryBoardWithJson(storyBoardDB, getStringStoryboard(name));
    }


    /**
     * Clean the actions of the recyclerview
     */
    private void deleteStoryboard() {
        @SuppressLint("InflateParams") View v = this.getLayoutInflater().inflate(R.layout.dialog_fragment, null);
        v.getBackground().setAlpha(220);
        Button ok = v.findViewById(R.id.ok);
        TextView textMessage = v.findViewById(R.id.message);
        textMessage.setText(getResources().getString(R.string.alert_message_delete_storyboard));
        textMessage.setTextSize(23);
        textMessage.setGravity(View.TEXT_ALIGNMENT_CENTER);
        Button cancel = v.findViewById(R.id.cancel);
        cancel.setVisibility(View.VISIBLE);
        createAlertDialog(v, ok, cancel);
        people.clear();
    }

    /**
     * Create a alert dialog for the user
     *
     * @param v      view
     * @param ok     button ok
     * @param cancel button cancel
     */
    private void createAlertDialog(View v, Button ok, Button cancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        ok.setOnClickListener(v1 -> {
            actions = new ArrayList<>();
            isPOI = actions.size() > 0;
            currentStoryBoardId = Long.MIN_VALUE;
            currentStoryBoardGoogleDriveID = null;
            storyBoardName.setText("");
            initRecyclerView();
            setSizeFile();
            cleanLocationData();
            dialog.dismiss();
        });
        cancel.setOnClickListener(v1 -> dialog.dismiss());
    }

    /**
     * Clean the location data
     */
    private void cleanLocationData() {
        SharedPreferences.Editor editor = getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE).edit();
        editor.putString(ConstantPrefs.FILE_NAME.name(), "");
        editor.putString(ConstantPrefs.LATITUDE.name(), "");
        editor.putString(ConstantPrefs.LONGITUDE.name(), "");
        editor.putString(ConstantPrefs.ALTITUDE.name(), "");
        editor.putString(ConstantPrefs.DURATION.name(), "");
        editor.putString(ConstantPrefs.HEADING.name(), "");
        editor.putString(ConstantPrefs.TILT.name(), "");
        editor.putString(ConstantPrefs.RANGE.name(), "");
        editor.putString(ConstantPrefs.ALTITUDE_MODE.name(), "");
        editor.apply();
    }


    @Override
    protected void onResume() {
        super.onResume();
        isStopStoryboard = false;
        loadData();
        rePaintRecyclerView();
        setSizeFile();
    }

    /**
     * Set the current file size
     */
    private void setSizeFile() {
        if (actions.size() == 0) {
            isPOI = false;
            String message = getResources().getString(R.string.size_file) + 0 + " MB";
            sizeFile.setText(message);
            sizeFile.setTextColor(Color.BLACK);
        } else {
            isPOI = true;
            StoryBoard storyBoard = new StoryBoard();
            storyBoard.setActions(actions);
            try {
                JSONObject jsonStoryboard = storyBoard.pack();
                ByteArrayContent contentStream = ByteArrayContent.fromString("application/json", jsonStoryboard.toString());
                double length = (double) contentStream.getLength();
                double megaBytes = length / 1048576.0;
                if (length < MAX_SIZE) {
                    DecimalFormat df = new DecimalFormat("####0.00");
                    String message = getResources().getString(R.string.size_file) + df.format(megaBytes) + " MB";
                    sizeFile.setText(message);
                    sizeFile.setTextColor(Color.BLACK);
                } else {
                    sizeFile.setText(getResources().getString(R.string.message_error_file_size_5MB));
                    sizeFile.setTextColor(Color.RED);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        saveStoryBoardCache();
        super.onPause();
    }

    /**
     * Save the storyboard in cache
     */
    private void saveStoryBoardCache() {
        try {
            StoryBoard storyBoard = new StoryBoard();
            storyBoard.setStoryBoardFileId(currentStoryBoardGoogleDriveID);
            storyBoard.setStoryBoardId(currentStoryBoardId);
            storyBoard.setName(storyBoardName.getText().toString());
            storyBoard.setActions(actions);
            SharedPreferences.Editor editor = getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE).edit();
            editor.putString(ConstantPrefs.STORY_BOARD_JSON.name(), storyBoard.pack().toString());
            editor.apply();
        } catch (JSONException jsonException) {
            Log.w(TAG_DEBUG, "ERROR JSON: " + jsonException);
        }
    }

    @Override
    protected void onDestroy() {
        AppDatabase.destroyInstance();
        super.onDestroy();
    }

    /**
     * Load the data
     */
    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE);
        boolean isConnected = sharedPreferences.getBoolean(ConstantPrefs.IS_CONNECTED.name(), false);
        if (isConnected) {
            connectionStatus.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_status_connection_green));
        }
    }

    /**
     * Initiate the recycleview
     */
    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        RecyclerView.Adapter mAdapter = new ActionRecyclerAdapter(this, actions, this);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * It re paints the recyclerview with the actions
     */
    private void rePaintRecyclerView() {
        RecyclerView.Adapter mAdapter = new ActionRecyclerAdapter(this, actions, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActionIdentifier.LOCATION_ACTIVITY.getId() && resultCode == Activity.RESULT_OK) {
            resolvePOIAction(data);
        } else if (requestCode == ActionIdentifier.MOVEMENT_ACTIVITY.getId() && resultCode == Activity.RESULT_OK) {
            resolveMovementAction(data);
        } else if (requestCode == ActionIdentifier.BALLOON_ACTIVITY.getId() && resultCode == Activity.RESULT_OK) {
            resolveBalloonAction(data);
        } else if (requestCode == ActionIdentifier.SHAPES_ACTIVITY.getId() && resultCode == Activity.RESULT_OK) {
            resolveShapeAction(data);
        }
    }

    /**
     * Resolve if the shape action is going to be deleted or saved
     *
     * @param data Intent with the info
     */
    private void resolveShapeAction(@Nullable Intent data) {
        boolean isDelete = Objects.requireNonNull(data).getBooleanExtra(ActionIdentifier.IS_DELETE.name(), false);
        int position = Objects.requireNonNull(data).getIntExtra(ActionIdentifier.POSITION.name(), -1);
        if (isDelete) {
            if (position != -1) {
                actions.remove(position);
            }
        } else {
            Shape shape = Objects.requireNonNull(data).getParcelableExtra(ActionIdentifier.SHAPES_ACTIVITY.name());
            boolean isSave = Objects.requireNonNull(data).getBooleanExtra(ActionIdentifier.IS_SAVE.name(), false);
            int lastPosition = Objects.requireNonNull(data).getIntExtra(ActionIdentifier.LAST_POSITION.name(), -1);
            if (position > actions.size()) position = actions.size();
            POI poi = findLastPOI(position);
            if (shape != null && poi != null) {
                shape.setPoi(poi);
            }
            if (position >= actions.size()) {
                actions.add(shape);
                if (isSave) actions.remove(lastPosition);
            }else if(position == 0){
                CustomDialogUtility.showDialog(CreateStoryBoardActivity.this,
                        getResources().getString(R.string.You_must_have_a_location));
            } else if (position > 0) {
                if (isSave && position == lastPosition) {
                    actions.set(position, shape);
                } else {
                    if (isSave && position <= lastPosition) {
                        actions.add(position, shape);
                        lastPosition++;
                    } else if (!isSave) actions.add(position, shape);
                    else actions.add(position + 1, shape);
                    if (isSave) actions.remove(lastPosition);
                }
            }
        }
    }


    /**
     * Resolve if the poi action is going to be deleted or saved
     *
     * @param data Intent with the info
     */
    private void resolvePOIAction(@Nullable Intent data) {
        boolean isDelete = Objects.requireNonNull(data).getBooleanExtra(ActionIdentifier.IS_DELETE.name(), false);
        int position = Objects.requireNonNull(data).getIntExtra(ActionIdentifier.POSITION.name(), -1);
        if (isDelete) {
            if (position != -1) {
                deletePOI(position);
            }
        } else {
            savePOI(data, position);
        }
    }

    /**
     * Resolve if the movement action is going to be deleted or saved
     *
     * @param data Intent with the info
     */
    private void resolveMovementAction(@Nullable Intent data) {
        boolean isDelete = Objects.requireNonNull(data).getBooleanExtra(ActionIdentifier.IS_DELETE.name(), false);
        int position = Objects.requireNonNull(data).getIntExtra(ActionIdentifier.POSITION.name(), -1);
        if (isDelete) {
            if (position != -1) {
                actions.remove(position);
            }
        } else {
            Movement movement = Objects.requireNonNull(data).getParcelableExtra(ActionIdentifier.MOVEMENT_ACTIVITY.name());
            boolean isSave = Objects.requireNonNull(data).getBooleanExtra(ActionIdentifier.IS_SAVE.name(), false);
            int lastPosition = Objects.requireNonNull(data).getIntExtra(ActionIdentifier.LAST_POSITION.name(), -1);
            if (position > actions.size()) position = actions.size();
            POI poi = findLastPOI(position);
            if (movement != null && poi != null) {
                movement.setPoi(poi);
            }
            if (position >= actions.size()) {
                actions.add(movement);
                if (isSave) actions.remove(lastPosition);
            }else if(position == 0){
                CustomDialogUtility.showDialog(CreateStoryBoardActivity.this,
                        getResources().getString(R.string.You_must_have_a_location));
            } else if (position > 0) {
                if (isSave && position == lastPosition) {
                    actions.set(position, movement);
                } else {
                    if (isSave && position <= lastPosition) {
                        actions.add(position, movement);
                        lastPosition++;
                    } else if (!isSave) actions.add(position, movement);
                    else actions.add(position + 1, movement);
                    if (isSave) actions.remove(lastPosition);
                }
            }
        }
    }

    /**
     * Resolve if the balloon action is going to be deleted or saved
     *
     * @param data Intent with the info
     */
    private void resolveBalloonAction(@Nullable Intent data) {
        boolean isDelete = Objects.requireNonNull(data).getBooleanExtra(ActionIdentifier.IS_DELETE.name(), false);
        int position = Objects.requireNonNull(data).getIntExtra(ActionIdentifier.POSITION.name(), -1);
        if (isDelete) {
            if (position != -1) {
                actions.remove(position);
            }
        } else {
            Balloon balloon = Objects.requireNonNull(data).getParcelableExtra(ActionIdentifier.BALLOON_ACTIVITY.name());
            boolean isSave = Objects.requireNonNull(data).getBooleanExtra(ActionIdentifier.IS_SAVE.name(), false);
            int lastPosition = Objects.requireNonNull(data).getIntExtra(ActionIdentifier.LAST_POSITION.name(), -1);
            if (position > actions.size()) position = actions.size() - 1;
            POI poi = findLastPOI(position);
            if (balloon != null && poi != null) {
                balloon.setPoi(poi);
            }
            if (position >= actions.size()) {
                actions.add(balloon);
                if (isSave) actions.remove(lastPosition);
            } else if(position == 0){
                CustomDialogUtility.showDialog(CreateStoryBoardActivity.this,
                        getResources().getString(R.string.You_must_have_a_location));
            }
            else if (position > 0) {
                if (isSave && position == lastPosition) {
                    actions.set(position, balloon);
                } else {
                    if (isSave && position <= lastPosition) {
                        actions.add(position, balloon);
                        lastPosition++;
                    } else if (!isSave) actions.add(position, balloon);
                    else actions.add(position + 1, balloon);
                    if (isSave) actions.remove(lastPosition);
                }
            }
        }
    }

    private POI findLastPOI(int position) {
        Action action;
        POI poi = null;
        for (int i = (position - 1); 0 <= i; i--) {
            action = actions.get(i);
            if (action.getType() == ActionIdentifier.LOCATION_ACTIVITY.getId()) {
                poi = (POI) action;
                break;
            }
        }
        return poi;
    }

    /**
     * Delete the poi
     *
     * @param position the position of the poi
     */
    private void deletePOI(int position) {
        Action action;
        isPOI = false;
        ArrayList<Action> newActions = new ArrayList<>();
        int startNewPoi = -1;
        for (int i = 0; i < position; i++) {
            action = actions.get(i);
            newActions.add(action);
            if (action.getType() == ActionIdentifier.LOCATION_ACTIVITY.getId()) {
                isPOI = true;
            }
        }
        for (int i = position + 1; i < actions.size(); i++) {
            if (actions.get(i).getType() == ActionIdentifier.LOCATION_ACTIVITY.getId()) {
                startNewPoi = i;
                isPOI = true;
                newActions.add(actions.get(i));
                break;
            }
        }
        if (startNewPoi == -1) startNewPoi = actions.size();
        for (int i = startNewPoi + 1; i < actions.size(); i++) {
            action = actions.get(i);
            newActions.add(action);
            if (action.getType() == ActionIdentifier.LOCATION_ACTIVITY.getId()) {
                isPOI = true;
            }
        }
        actions.clear();
        actions.addAll(newActions);
    }

    /**
     * Save or add the poi
     *
     * @param data     the intent with the data
     * @param position the position of the poi
     */
    private void savePOI(@NonNull Intent data, int position) {
        POI poi = Objects.requireNonNull(data).getParcelableExtra(ActionIdentifier.LOCATION_ACTIVITY.name());
        int lastPosition = Objects.requireNonNull(data).getIntExtra(ActionIdentifier.LAST_POSITION.name(), -1);
        boolean isSave = Objects.requireNonNull(data).getBooleanExtra(ActionIdentifier.IS_SAVE.name(), false);
        if (position >= actions.size()) {
            actions.add(poi);
            if (isSave) actions.remove(lastPosition);
        } else if (position >= 0) {
            if (position == lastPosition) {
                actions.set(position, poi);
                updateData(position, poi);
            } else {
                cleanActions(position, poi, isSave, lastPosition);
            }
        }
    }

    /**
     * Modify the actions and clean it
     *
     * @param position     position to be cahnge
     * @param poi          poi to change
     * @param isSave       if a save action
     * @param lastPosition last position of the poi
     */
    private void cleanActions(int position, POI poi, boolean isSave, int lastPosition) {
        if (isSave && position <= lastPosition) {
            actions.add(position, poi);
            lastPosition++;
        } else if (!isSave) actions.add(position, poi);
        else actions.add(position + 1, poi);
        if (isSave) actions.remove(lastPosition);
        updateData(position, poi);
        Action action;
        if (lastPosition == 0) {
            int nextPOI = 0;
            for (int l = 0; l < actions.size(); l++) {
                action = actions.get(l);
                nextPOI = l;
                if (action.getType() == ActionIdentifier.LOCATION_ACTIVITY.getId()) break;
            }
            actions = actions.subList(nextPOI, actions.size());
        }
    }

    private void updateData(int position, POI poi) {
        Action action;
        for (int i = position + 1; i < actions.size(); i++) {
            action = actions.get(i);
            if (action.getType() == ActionIdentifier.LOCATION_ACTIVITY.getId()) break;
            if (action.getType() == ActionIdentifier.MOVEMENT_ACTIVITY.getId()) {
                Movement movement = (Movement) action;
                movement.setPoi(poi);
                actions.set(i, movement);
            } else if (action.getType() == ActionIdentifier.BALLOON_ACTIVITY.getId()) {
                Balloon balloon = (Balloon) action;
                balloon.setPoi(poi);
                actions.set(i, balloon);
            } else if (action.getType() == ActionIdentifier.SHAPES_ACTIVITY.getId()) {
                Shape shape = (Shape) action;
                shape.setPoi(poi);
                actions.set(i, shape);
            }
        }
    }

    /**
     * Change the background color and the option clickable to false of the button_connect
     */
    private void changeButtonClickableBackgroundColor() {
        changeButtonClickableBackgroundColor(getApplicationContext(), buttCreate);
    }

    public void savekml(String dirname, String data){
        File file1 = new File(getExternalFilesDir(null),  "/" + "Placemarks");
        if (!file1.exists()){
            file1.mkdir();
        }
        try {
            File esp = new File(file1, dirname + ".kml");
            FileWriter writer = new FileWriter(esp);
            writer.append(data);
            writer.flush();
            writer.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    private void shareFilekml(String name,String data){
        try {
            File file = new File(getCacheDir(),"esp");
            file.mkdirs();
            File esp = new File(file,name+".kml");
            FileWriter outputStream = new FileWriter(esp);
            outputStream.write(data);
            outputStream.close();
            Uri uri = FileProvider.getUriForFile(this, "com.lglab.goutam.simple_cms.fileProvider", esp);
            Intent intentShare = new Intent(Intent.ACTION_SEND);
            intentShare.setType("*/*");
            intentShare.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intentShare, "Share the file ..."));
            Log.d("inner intent", "kaam go gata");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Exception", "Dikkat hori hai exception main aai!");
        }
    }
    private void shareZIP(File name){
        try {
            Uri uri = FileProvider.getUriForFile(this, "com.lglab.goutam.simple_cms.fileProvider", name);
            Intent intentShare = new Intent(Intent.ACTION_SEND);
            intentShare.setType("*/*");
            intentShare.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intentShare, "Share the file ..."));
            Log.d("inner intent", "kaam go gata");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Exception", "Dikkat hori hai exception main aai!");
        }
    }


    @Override
    public void onNoteClick(int position) {
        Action selected = actions.get(position);
        if (selected instanceof POI) {
            Intent intent = new Intent(getApplicationContext(), CreateStoryBoardActionLocationActivity.class);
            intent.putExtra(ActionIdentifier.LOCATION_ACTIVITY.name(), (POI) selected);
            intent.putExtra(ActionIdentifier.POSITION.name(), position);
            intent.putExtra(ActionIdentifier.LAST_POSITION.name(), position);
            intent.putExtra(ActionIdentifier.ACTION_SIZE.name(), actions.size());
            startActivityForResult(intent, ActionIdentifier.LOCATION_ACTIVITY.getId());
        } else if (selected instanceof Movement) {
            Intent intent = new Intent(getApplicationContext(), CreateStoryBoardActionMovementActivity.class);
            intent.putExtra(ActionIdentifier.MOVEMENT_ACTIVITY.name(), (Movement) selected);
            intent.putExtra(ActionIdentifier.POSITION.name(), position);
            intent.putExtra(ActionIdentifier.LAST_POSITION.name(), position);
            intent.putExtra(ActionIdentifier.ACTION_SIZE.name(), actions.size());
            startActivityForResult(intent, ActionIdentifier.MOVEMENT_ACTIVITY.getId());
        } else if (selected instanceof Balloon) {
            Intent intent = new Intent(getApplicationContext(), CreateStoryBoardActionBalloonActivity.class);
            intent.putExtra(ActionIdentifier.BALLOON_ACTIVITY.name(), (Balloon) selected);
            intent.putExtra(ActionIdentifier.POSITION.name(), position);
            intent.putExtra(ActionIdentifier.LAST_POSITION.name(), position);
            intent.putExtra(ActionIdentifier.ACTION_SIZE.name(), actions.size());
            startActivityForResult(intent, ActionIdentifier.BALLOON_ACTIVITY.getId());
        } else if (selected instanceof Shape) {
            Intent intent = new Intent(getApplicationContext(), CreateStoryBoardActionShapeActivity.class);
            intent.putExtra(ActionIdentifier.SHAPES_ACTIVITY.name(), (Shape) selected);
            intent.putExtra(ActionIdentifier.POSITION.name(), position);
            intent.putExtra(ActionIdentifier.LAST_POSITION.name(), position);
            intent.putExtra(ActionIdentifier.ACTION_SIZE.name(), actions.size());
            startActivityForResult(intent, ActionIdentifier.SHAPES_ACTIVITY.getId());
        } else {
            Log.w(TAG_DEBUG, "ERROR EDIT");
        }
    }
}

