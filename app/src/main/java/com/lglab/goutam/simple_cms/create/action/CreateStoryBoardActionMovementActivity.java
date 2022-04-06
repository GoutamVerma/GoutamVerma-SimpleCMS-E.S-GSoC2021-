    package com.lglab.goutam.simple_cms.create.action;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.lglab.goutam.simple_cms.R;
import com.lglab.goutam.simple_cms.create.utility.connection.LGConnectionTest;
import com.lglab.goutam.simple_cms.create.utility.model.ActionBuildCommandUtility;
import com.lglab.goutam.simple_cms.create.utility.model.ActionIdentifier;
import com.lglab.goutam.simple_cms.create.utility.model.movement.Movement;
import com.lglab.goutam.simple_cms.create.utility.model.poi.POI;
import com.lglab.goutam.simple_cms.create.utility.model.poi.POICamera;
import com.lglab.goutam.simple_cms.create.utility.model.ActionController;
import com.lglab.goutam.simple_cms.dialog.CustomDialogUtility;
import com.lglab.goutam.simple_cms.utility.ConstantPrefs;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is in charge of getting the information of movement in the camera
 */
public class CreateStoryBoardActionMovementActivity extends AppCompatActivity {

    //private static final String TAG_DEBUG = "CreateStoryBoardActionMovementActivity";

    private TextView seekBarValueHeading;
    private TextView seekBarValueTilt;
    private TextView oldHeading;
    private TextView oldTilt;
    private TextView connectionStatus;
    private TextView locationName;
    private TextView locationNameTitle;
    private EditText duration, positionSave;
    private SeekBar seekBarHeading, seekBarTilt;
//    private SwitchCompat switchCompatOrbitMode;
    private Spinner Animation;
    private ArrayAdapter<CharSequence> spinnerAdapter;

    private Handler handler = new Handler();
    private POI poi;
    private boolean isSave = false;
    private int position = -1;
    private int lastPosition = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_stroyboard_action_movement);

        seekBarValueHeading = findViewById(R.id.seek_bar_value_heading);
        seekBarValueTilt = findViewById(R.id.seek_bar_value_tilt);
        oldHeading = findViewById(R.id.old_heading);
        oldTilt = findViewById(R.id.old_tilt);
        connectionStatus = findViewById(R.id.connection_status);
        locationName = findViewById(R.id.location_name);
        locationNameTitle = findViewById(R.id.location_name_title);
        duration = findViewById(R.id.duration);
        positionSave = findViewById(R.id.position_save);
        Animation = findViewById(R.id.kml_mode);
        SharedPreferences sharedPreferences = getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE);
        loadConnectionStatus(sharedPreferences);

        seekBarHeading = findViewById(R.id.seek_bar_heading);
        seekBarTilt = findViewById(R.id.seek_bar_tilt);
//        switchCompatOrbitMode = findViewById(R.id.switch_button);

        Button buttTest = findViewById(R.id.butt_gdg);
        Button buttCancel = findViewById(R.id.butt_cancel);
        Button buttAdd = findViewById(R.id.butt_add);
        Button buttDelete = findViewById(R.id.butt_delete);

        spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.kml_mode, R.layout.spinner_text);
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        Animation.setAdapter(spinnerAdapter);
//        Animation.setOnItemSelectedListener(this);
        Intent intent = getIntent();
        poi = intent.getParcelableExtra(ActionIdentifier.LOCATION_ACTIVITY.name());
        if(poi != null){
            setTextView();
        }else{
            locationName.setVisibility(View.VISIBLE);
            locationNameTitle.setVisibility(View.VISIBLE);
            locationNameTitle.setText(getResources().getString(R.string.location_name_title_empty));
        }

        Movement movement = intent.getParcelableExtra(ActionIdentifier.MOVEMENT_ACTIVITY.name());
        position = intent.getIntExtra(ActionIdentifier.POSITION.name(), -1);
        lastPosition = intent.getIntExtra(ActionIdentifier.LAST_POSITION.name(), -1);
        int actionsSize = intent.getIntExtra(ActionIdentifier.ACTION_SIZE.name(), -1);

        int positionValue;
        if(position == -1) positionValue = actionsSize;
        else positionValue = position;
        positionValue++;
        positionSave.setText(String.valueOf(positionValue));

        if(movement != null){
            isSave = true;
            buttAdd.setText(getResources().getString(R.string.button_save));
            buttDelete.setVisibility(View.VISIBLE);
            poi = movement.getPoi();
            setTextView();
            seekBarValueHeading.setText(String.valueOf((int) movement.getNewHeading()));
            seekBarValueTilt.setText(String.valueOf((int) movement.getNewTilt()));
            seekBarHeading.setMax(360);
            seekBarHeading.setProgress((int) movement.getNewHeading());
            seekBarTilt.setMax(90);
            seekBarTilt.setProgress((int) movement.getNewTilt());
//            String isOrbitMode = movement.isOrbitMode();
//            switchCompatOrbitMode.setChecked(isOrbitMode.equals("Orbit"));
//            setSwitchAndSeekBar(isOrbitMode.equals("Orbit"));
            duration.setText(String.valueOf(movement.getDuration()));
        }

//        switchCompatOrbitMode.setOnCheckedChangeListener((buttonView, isChecked) -> setSwitchAndSeekBar(isChecked));


        seekBarHeading.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String progressString = progress + "°";
                seekBarValueHeading.setText(progressString);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarTilt.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String progressString = progress + "°";
                seekBarValueTilt.setText(progressString);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        buttCancel.setOnClickListener( (view) ->
                finish()
        );

        buttTest.setOnClickListener((view) -> testConnection() );

        buttAdd.setOnClickListener((view) ->
                addMovement()
        );

        buttDelete.setOnClickListener((view) ->
                deleteMovement() );
    }


    /**
     * Set the seekbar and the switch
     * @param isOrbitMode If is orbit mode slected or no
     */
    private void setSwitchAndSeekBar(boolean isOrbitMode) {
        if (isOrbitMode) {
            seekBarTilt.setEnabled(false);
            seekBarHeading.setEnabled(false);
            seekBarTilt.setProgressDrawable(ContextCompat.getDrawable(this, R.drawable.custom_seek_bar_black));
            seekBarHeading.setProgressDrawable(ContextCompat.getDrawable(this, R.drawable.custom_seek_bar_black));
        } else {
            Animation.setEnabled(false);
            seekBarTilt.setEnabled(true);
            seekBarHeading.setEnabled(true);
            seekBarTilt.setProgressDrawable(ContextCompat.getDrawable(this, R.drawable.custom_seek_bar));
            seekBarHeading.setProgressDrawable(ContextCompat.getDrawable(this, R.drawable.custom_seek_bar));
        }
    }

    /**
     * Test the connection to the liquid galaxy
     */
    private void testConnection() {
        AtomicBoolean isConnected = new AtomicBoolean(false);
        LGConnectionTest.testPriorConnection(this, isConnected);
        SharedPreferences sharedPreferences = getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE);
        handler.postDelayed(() -> {
            if(isConnected.get()){
//                if(switchCompatOrbitMode.isChecked()){
                    ActionBuildCommandUtility.mode = Animation.getSelectedItem().toString();
                    Toast.makeText(this, ActionBuildCommandUtility.mode+" TEST MODE", Toast.LENGTH_SHORT).show();
                    ActionController.getInstance().orbit(poi, null);
//                } else{
//                    POI poiSend = new POI(poi);
//                    POICamera poiCamera = poiSend.getPoiCamera();
//                    POICamera poiCameraSend = new POICamera(seekBarHeading.getProgress(),
//                            seekBarTilt.getProgress(), poiCamera.getRange(),
//                            poiCamera.getAltitudeMode(), poiCamera.getDuration());
//                    poiSend.setPoiCamera(poiCameraSend);
//                    ActionController.getInstance().moveToPOI(poiSend, null);
//                }
            }
            loadConnectionStatus(sharedPreferences);
        }, 1200);
    }

    /**
     * Delete the movement open
     */
    private void deleteMovement() {
        Intent returnInfoIntent = new Intent();
        returnInfoIntent.putExtra(ActionIdentifier.POSITION.name(), position);
        returnInfoIntent.putExtra(ActionIdentifier.IS_DELETE.name(), true);
        setResult(Activity.RESULT_OK, returnInfoIntent);
        finish();
    }

    /**
     * Set the information of the oldHeading and oldTilt
     */
    private void setTextView() {
        oldHeading.setText(String.valueOf(poi.getPoiCamera().getHeading()));
        oldTilt.setText(String.valueOf(poi.getPoiCamera().getTilt()));
        locationName.setVisibility(View.VISIBLE);
        locationNameTitle.setVisibility(View.VISIBLE);
        locationNameTitle.setText(poi.getPoiLocation().getName());
    }

    /**
     * Send the information to add a movement
     */
    private void addMovement() {
        int seekBarHeadingValue = seekBarHeading.getProgress();
        int seekBarTiltValue = seekBarTilt.getProgress();
        String durationString = duration.getText().toString();
        String CompatMode = Animation.getSelectedItem().toString();
        if(durationString.equals("")){
            CustomDialogUtility.showDialog(CreateStoryBoardActionMovementActivity.this, getResources().getString(R.string.activity_create_missing_duration_field_error));
        }else{
            int durationInt = Integer.parseInt(durationString);
            Movement movement = new Movement().setNewHeading(seekBarHeadingValue)
                    .setNewTilt(seekBarTiltValue).setPoi(poi).set_KML_Mode(CompatMode).setDuration(durationInt);
            ActionBuildCommandUtility.mode = Animation.getSelectedItem().toString();
            Intent returnInfoIntent = new Intent();
            returnInfoIntent.putExtra(ActionIdentifier.MOVEMENT_ACTIVITY.name(), movement);
            returnInfoIntent.putExtra(ActionIdentifier.IS_SAVE.name(), isSave);
            returnInfoIntent.putExtra(ActionIdentifier.POSITION.name(),
                    Integer.parseInt(positionSave.getText().toString()) - 1);
            returnInfoIntent.putExtra(ActionIdentifier.LAST_POSITION.name(), lastPosition);
            setResult(Activity.RESULT_OK, returnInfoIntent);
            finish();
        }
    }

    /**
     * Set the connection status on the view
     */
    private void loadConnectionStatus(SharedPreferences sharedPreferences) {
        boolean isConnected = sharedPreferences.getBoolean(ConstantPrefs.IS_CONNECTED.name(), false);
        if (isConnected) {
            connectionStatus.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_status_connection_green));
        }else{
            connectionStatus.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_status_connection_red));
        }
    }
}