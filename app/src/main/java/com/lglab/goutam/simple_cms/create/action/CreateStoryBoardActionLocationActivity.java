package com.lglab.goutam.simple_cms.create.action;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.lglab.goutam.simple_cms.MainActivity;
import com.lglab.goutam.simple_cms.R;
import com.lglab.goutam.simple_cms.create.CreateStoryBoardActivity;
import com.lglab.goutam.simple_cms.create.utility.connection.LGConnectionTest;
import com.lglab.goutam.simple_cms.create.utility.model.ActionIdentifier;
import com.lglab.goutam.simple_cms.create.utility.model.poi.POI;
import com.lglab.goutam.simple_cms.create.utility.model.poi.POICamera;
import com.lglab.goutam.simple_cms.create.utility.model.ActionController;
import com.lglab.goutam.simple_cms.create.utility.model.poi.POILocation;
import com.lglab.goutam.simple_cms.dialog.CustomDialogUtility;
import com.lglab.goutam.simple_cms.export_esp.export_esp;
import com.lglab.goutam.simple_cms.utility.ConstantPrefs;
import com.lglab.goutam.simple_cms.export_esp.record;
import org.apache.commons.codec.StringEncoderComparator;



import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.lglab.goutam.simple_cms.dialog.CustomDialogUtility.getDialog;
import com.lglab.goutam.simple_cms.export_esp.datacapture;
/**
 * This class is in charge of getting the information of location action
 */
public class CreateStoryBoardActionLocationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    //private static final String TAG_DEBUG = "CreateStoryBoardActionLocationActivity";

    private EditText file_name, longitude, latitude, altitude, duration, heading, tilt, range, positionSave;
    private Spinner altitudeModeSpinner;
    private Spinner espmode;
    private TextView connectionStatus;
    ProgressDialog  progressDialog;
    public static HashMap people = new HashMap<String, List<String>>();

    public HashMap<String, List<String>> getPeopleMap() {
        return people;
    }

    private Handler handler = new Handler();
    private boolean isSave = false;
    private int position = -1;
    private int lastPosition = 0;
    private ArrayAdapter<CharSequence> spinnerAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_storyboard_action_location);

        file_name = findViewById(R.id.file_name);
        longitude = findViewById(R.id.longitude);
        latitude = findViewById(R.id.latitude);
        altitude = findViewById(R.id.altitude);
        duration = findViewById(R.id.duration);
        heading = findViewById(R.id.heading);
        tilt = findViewById(R.id.tilt);
        range = findViewById(R.id.range);
        positionSave = findViewById(R.id.position_save);
        altitudeModeSpinner = findViewById(R.id.altitude_mode);
        espmode = findViewById(R.id.esp_mode);
        connectionStatus = findViewById(R.id.connection_status);

        Button buttTest = findViewById(R.id.butt_gdg);
        Button buttCancel = findViewById(R.id.butt_cancel);
        Button buttAdd = findViewById(R.id.butt_add);
        Button buttDelete = findViewById(R.id.butt_delete);
        Button buttRec = findViewById(R.id.butt_rec);


        spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.elevation_types, R.layout.spinner_text);
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        altitudeModeSpinner.setAdapter(spinnerAdapter);
        altitudeModeSpinner.setOnItemSelectedListener(this);

        spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.esp_types, R.layout.spinner_text);
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        espmode.setAdapter(spinnerAdapter);
        espmode.setOnItemSelectedListener(this);

        Intent intent = getIntent();
        POI poi = intent.getParcelableExtra(ActionIdentifier.LOCATION_ACTIVITY.name());
        position = intent.getIntExtra(ActionIdentifier.POSITION.name(), -1);
        lastPosition = intent.getIntExtra(ActionIdentifier.LAST_POSITION.name(), -1);
        int actionsSize = intent.getIntExtra(ActionIdentifier.ACTION_SIZE.name(), -1);

        if (poi != null) {
            isSave = true;
            buttAdd.setText(getResources().getString(R.string.button_save));
            buttDelete.setVisibility(View.VISIBLE);
            loadPoiData(poi);
        } else {
            loadData();
        }

        int positionValue;
        if (position == -1) positionValue = actionsSize;
        else positionValue = position;
        positionValue++;
        positionSave.setText(String.valueOf(positionValue));


        buttCancel.setOnClickListener((view) ->
                finish()
        );

        buttTest.setOnClickListener((view) ->
                testConnection()
        );

        buttAdd.setOnClickListener((view) ->
                addPOI()
        );
        buttDelete.setOnClickListener((view) -> deletePoi());

        buttRec.setOnClickListener((view) ->boxer());
    }
    public void boxer(){
            progressDialog = new ProgressDialog(CreateStoryBoardActionLocationActivity.this);
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    DatagramSocket udpSocket = null;
                    SharedPreferences sharedPreferences = getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE);
                    String hostPort = sharedPreferences.getString(ConstantPrefs.URI_TEXT.name(), "");
                    String hostport = "192.168.29.198:1234";
                    String msg = "";
                    if(hostport!="") {
                        try {
                            String[] hostNPort = hostPort.split(":");
                            int port = Integer.parseInt(hostNPort[1]);
                            udpSocket = new DatagramSocket(1234);
                            byte[] message = new byte[8000];
                            Log.d("UDP client: ", "about to wait to receive");
                            Log.d("port no is ", String.valueOf(udpSocket.getLocalPort()));
                            DatagramPacket packet = new DatagramPacket(message, message.length);
                            udpSocket.receive(packet);
                            String text = new String(message, 0, packet.getLength());
                            udpSocket.setReuseAddress(true);
                            udpSocket.close();
                            msg = text;
                            Log.d("Received data", msg);
                            updater(msg);
                            message = new byte[8000];

                        } catch (BindException e) {
                            CustomDialogUtility.showDialog(CreateStoryBoardActionLocationActivity.this,
                                    getResources().getString(R.string.getting_unwanted_error_with_port));
                            e.printStackTrace();
                        } catch (SocketException e) {
                            CustomDialogUtility.showDialog(CreateStoryBoardActionLocationActivity.this,
                                    getResources().getString(R.string.unable_to_access_the_port));
                            e.printStackTrace();
                        } catch (IOException e) {
                            Log.e("UDP client has IOException", "error: ", e);
                        }
                    }
                    else {   progressDialog.dismiss();
                            CustomDialogUtility.showDialog(CreateStoryBoardActionLocationActivity.this,
                                getResources().getString(R.string.lg_is_not_connected));
                    }
                }

            });
            thread.start();
//        }
    }
    public void updater(final String value){
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String[] values = value.split(",");
                    latitude.setText(values[1]);
                    longitude.setText(values[2]);
                    altitude.setText(values[3]);
                    heading.setText(values[4]);
                    tilt.setText(values[5]);
                    progressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            CustomDialogUtility.showDialog(CreateStoryBoardActionLocationActivity.this,
                    getResources().getString(R.string.unable_to_put_data_on_fields));
        }
    }
    /**
     * Charge the data for the poi
     *
     * @param poi Poi that is going to be edit
     */

    private void loadPoiData(POI poi) {
        SharedPreferences sharedPreferences = getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE);
        loadConnectionStatus(sharedPreferences);
        file_name.setText(poi.getPoiLocation().getName());
        latitude.setText(String.valueOf(poi.getPoiLocation().getLatitude()));
        longitude.setText(String.valueOf(poi.getPoiLocation().getLongitude()));
        altitude.setText(String.valueOf(poi.getPoiLocation().getAltitude()));
        duration.setText(String.valueOf(poi.getPoiCamera().getDuration()));
        heading.setText(String.valueOf(poi.getPoiCamera().getHeading()));
        tilt.setText(String.valueOf(poi.getPoiCamera().getTilt()));
        range.setText(String.valueOf(poi.getPoiCamera().getRange()));
        altitudeModeSpinner.setSelection(orbit_position(String.valueOf(poi.getPoiCamera().getAltitudeMode())));
        try{
        List<String> position = (List<String>) people.get( poi.getPoiLocation().getName());
        espmode.setSelection(esp_position(position.get(3)));
        } catch (NullPointerException e) {
            espmode.setSelection(0);
        }
    }
    private int esp_position(String name){
        int postion=0;
                if(name.equals("None")){
                    position=0;
        }else if(name.equals("Orbit")){
                    position=1;
        }else if(name.equals("Spiral")){
                    position=2;
                } else if(name.equals("Zoom-To")){
                    position=3;
                }
                return position;
    }
    private int orbit_position(String name) {
        int position=0;
        if (name.equals("absolute")){
            position=0;
        } else if (name.equals("clampToGround")){
            position=1;
        }else if(name.equals("clampToSeaFloor")){
            position=2;
        }else if(name.equals("relativeToGround")){
            position=3;
        }else if(name.equals("relativeToSeaFloor")){
            position=4;
        }
        return position;
    }

    /**
     * Load the data
     */
    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE);
        loadConnectionStatus(sharedPreferences);
        file_name.setText(sharedPreferences.getString(ConstantPrefs.FILE_NAME.name(), ""));


        latitude.setText(sharedPreferences.getString(ConstantPrefs.LATITUDE.name(), ""));
        longitude.setText(sharedPreferences.getString(ConstantPrefs.LONGITUDE.name(), ""));
        altitude.setText(sharedPreferences.getString(ConstantPrefs.ALTITUDE.name(), ""));
        duration.setText(sharedPreferences.getString(ConstantPrefs.DURATION.name(), ""));
        heading.setText(sharedPreferences.getString(ConstantPrefs.HEADING.name(), ""));
        tilt.setText(sharedPreferences.getString(ConstantPrefs.TILT.name(), ""));
        range.setText(sharedPreferences.getString(ConstantPrefs.RANGE.name(), ""));
        altitudeModeSpinner.setSelection(spinnerAdapter.getPosition(sharedPreferences.getString(ConstantPrefs.ALTITUDE_MODE.name(), "")));
    }

    /**
     * Set the connection status on the view
     *
     * @param sharedPreferences sharedPreferences
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
     * Test the poi creation
     */
    private void testConnection() {
        String latitudeText = latitude.getText().toString();
        String longitudeText = longitude.getText().toString();
        String altitudeText = altitude.getText().toString();
        String durationText = duration.getText().toString();
        String headingText = heading.getText().toString();
        String tiltText = tilt.getText().toString();
        String rangeText = range.getText().toString();
        String altitudeModeText = altitudeModeSpinner.getSelectedItem().toString();
        if(verificationData(latitudeText, longitudeText, altitudeText, durationText, headingText, tiltText, rangeText)){
            saveData(latitudeText, longitudeText, altitudeText, durationText, headingText, tiltText, rangeText, altitudeModeText);
            AtomicBoolean isConnected = new AtomicBoolean(false);
            LGConnectionTest.testPriorConnection(this, isConnected);
            SharedPreferences sharedPreferences = getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE);
            handler.postDelayed(() -> {
                if(isConnected.get()){
                    POILocation poiLocation = new POILocation("Test", Double.parseDouble(longitudeText), Double.parseDouble(latitudeText), Double.parseDouble(altitudeText));
                    POICamera poiCamera = new POICamera(Double.parseDouble(headingText), Double.parseDouble(tiltText), Double.parseDouble(rangeText), altitudeModeText, Integer.parseInt(durationText));
                    POI poi = new POI().setPoiLocation(poiLocation).setPoiCamera(poiCamera);
                    ActionController.getInstance().moveToPOI(poi, null);
                }
                loadConnectionStatus(sharedPreferences);
            }, 1300);
        }
    }
    public void record(HashMap people){
        String name = file_name.getText().toString();
        Double latitudeText = Double.parseDouble(latitude.getText().toString());
        Double longitudeText =  Double.parseDouble(longitude.getText().toString());
        Double altitudeText =  Double.parseDouble(altitude.getText().toString());
        String esp_mode = espmode.getSelectedItem().toString();
        String durationText = duration.getText().toString();
        String position = positionSave.getText().toString();
        List<String> values = new ArrayList<String>();
        values.add(String.valueOf(longitudeText));
        values.add(String.valueOf(latitudeText));
        values.add(String.valueOf(altitudeText));
        values.add(esp_mode);
        values.add(name);
        values.add(durationText);
        values.add(position);
        people.put(name,values);

    }

    /**
     * Safe the date in shared preference
     * @param latitudeText latitude
     * @param longitudeText longitude
     * @param altitudeText altitude
     * @param durationText duration
     * @param headingText heading
     * @param tiltText tilt
     * @param rangeText range
     * @param altitudeModeText altitude mode
     */
    private void saveData(String latitudeText, String longitudeText, String altitudeText, String durationText, String headingText, String tiltText, String rangeText, String altitudeModeText) {

        String fileNameText = file_name.getText().toString();
        String esp_mode = espmode.getSelectedItem().toString();
        String position = positionSave.getText().toString();
        SharedPreferences.Editor editor = getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE).edit();
        editor.putString(ConstantPrefs.FILE_NAME.name(), fileNameText);
        editor.putString(ConstantPrefs.LATITUDE.name(), latitudeText);
        editor.putString(ConstantPrefs.LONGITUDE.name(), longitudeText);
        editor.putString(ConstantPrefs.ALTITUDE.name(), altitudeText);
        editor.putString(ConstantPrefs.DURATION.name(), durationText);
        editor.putString(ConstantPrefs.HEADING.name(), headingText);
        editor.putString(ConstantPrefs.TILT.name(), tiltText);
        editor.putString(ConstantPrefs.RANGE.name(), rangeText);
        editor.putString(ConstantPrefs.ALTITUDE_MODE.name(), altitudeModeText);
        editor.apply();
        record.findanddelete(position, people);
        record.Update(people, fileNameText, Double.parseDouble(String.valueOf(longitudeText)),Double.parseDouble(String.valueOf(latitudeText)),Double.parseDouble(String.valueOf(altitudeText)),esp_mode,durationText,fileNameText,position);
    }

    /**
     * Add a POI to the storyBoard
     */
    private void addPOI() {
        String latitudeText = latitude.getText().toString();
        String longitudeText = longitude.getText().toString();
        String altitudeText = altitude.getText().toString();
        String durationText = duration.getText().toString();
        String headingText = heading.getText().toString();
        String tiltText = tilt.getText().toString();
        String rangeText = range.getText().toString();
        String altitudeModeText = altitudeModeSpinner.getSelectedItem().toString();
        if(verificationData(latitudeText, longitudeText, altitudeText, durationText, headingText,
                tiltText, rangeText)){
            String fileNameText = file_name.getText().toString();
             if(!fileNameText.equals("")){
                saveData(latitudeText, longitudeText, altitudeText, durationText, headingText, tiltText, rangeText, altitudeModeText);
                POILocation poiLocation = new POILocation(fileNameText, Double.parseDouble(longitudeText),
                        Double.parseDouble(latitudeText), Double.parseDouble(altitudeText));
                POICamera poiCamera = new POICamera(Double.parseDouble(headingText), Double.parseDouble(tiltText),
                        Double.parseDouble(rangeText), altitudeModeText, Integer.parseInt(durationText));
                POI poi = new POI().setPoiLocation(poiLocation).setPoiCamera(poiCamera);
                Intent returnInfoIntent = new Intent();
                returnInfoIntent.putExtra(ActionIdentifier.LOCATION_ACTIVITY.name(), poi);
                returnInfoIntent.putExtra(ActionIdentifier.IS_SAVE.name(), isSave);
                returnInfoIntent.putExtra(ActionIdentifier.LAST_POSITION.name(), lastPosition);
                returnInfoIntent.putExtra(ActionIdentifier.POSITION.name(),
                        Integer.parseInt(positionSave.getText().toString()) - 1);
                setResult(Activity.RESULT_OK, returnInfoIntent);
                finish();
                record(people);

            } else{
                CustomDialogUtility.showDialog(CreateStoryBoardActionLocationActivity.this,  getResources().getString(R.string.activity_create_location_missing_file_name));
            }
        }
    }
    /**
     * Send the information of deleting the POI selected
     */
    private void deletePoi() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams") View v = this.getLayoutInflater().inflate(R.layout.dialog_fragment, null);
        v.getBackground().setAlpha(220);
        Button ok = v.findViewById(R.id.ok);
        TextView textMessage = v.findViewById(R.id.message);
        textMessage.setText(getResources().getString(R.string.alert_message_delete_action_location));
        textMessage.setTextSize(23);
        textMessage.setGravity(View.TEXT_ALIGNMENT_CENTER);
        Button cancel = v.findViewById(R.id.cancel);
        cancel.setVisibility(View.VISIBLE);
        builder.setView(v);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        ok.setOnClickListener( view -> {
            Intent returnInfoIntent = new Intent();
            returnInfoIntent.putExtra(ActionIdentifier.POSITION.name(), position);
            returnInfoIntent.putExtra(ActionIdentifier.IS_DELETE.name(), true);
            setResult(Activity.RESULT_OK, returnInfoIntent);
            String fileNameText = file_name.getText().toString();
            record.Delete(people, fileNameText);
            dialog.dismiss();
            finish();
        });
        cancel.setOnClickListener( view ->
                dialog.dismiss());
    }

    /**
     * verify the camps
     * @param latitudeText latitude
     * @param longitudeText longitude
     * @param altitudeText altitude
     * @param durationText duration
     * @param headingText heading
     * @param tiltText tilt
     * @param rangeText range
     * @return false if a camp is empty
     */
    private boolean verificationData(String latitudeText, String longitudeText, String altitudeText, String durationText, String headingText, String tiltText, String rangeText) {
        if(latitudeText.equals("")){
            CustomDialogUtility.showDialog(CreateStoryBoardActionLocationActivity.this,  getResources().getString(R.string.activity_create_location_missing_latitude_field_error));
            return false;
        }
        if(longitudeText.equals("")){
            CustomDialogUtility.showDialog(CreateStoryBoardActionLocationActivity.this,  getResources().getString(R.string.activity_create_location_missing_longitude_field_error));
            return false;
        }
        if(altitudeText.equals("")){
            CustomDialogUtility.showDialog(CreateStoryBoardActionLocationActivity.this,
                    getResources().getString(R.string.activity_create_location_missing_altitude_field_error));
            return false;
        }
        if(durationText.equals("")){
            CustomDialogUtility.showDialog(CreateStoryBoardActionLocationActivity.this,
                    getResources().getString(R.string.activity_create_location_missing_duration_field_error));
            return false;
        }
        if(headingText.equals("")){
            CustomDialogUtility.showDialog(CreateStoryBoardActionLocationActivity.this,
                    getResources().getString(R.string.activity_create_location_missing_heading_field_error));
            return false;
        }
        if(tiltText.equals("")){
            CustomDialogUtility.showDialog(CreateStoryBoardActionLocationActivity.this,
                    getResources().getString(R.string.activity_create_location_missing_tilt_field_error));
            return false;
        }
        if(rangeText.equals("")){
            CustomDialogUtility.showDialog(CreateStoryBoardActionLocationActivity.this,
                    getResources().getString(R.string.activity_create_location_missing_range_field_error));
            return false;
        }
        return  true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}