package com.lglab.goutam.simple_cms.create.action;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lglab.goutam.simple_cms.R;
import com.lglab.goutam.simple_cms.create.utility.adapter.PointRecyclerAdapter;
import com.lglab.goutam.simple_cms.create.utility.connection.LGConnectionTest;
import com.lglab.goutam.simple_cms.create.utility.model.ActionController;
import com.lglab.goutam.simple_cms.create.utility.model.ActionIdentifier;
import com.lglab.goutam.simple_cms.create.utility.model.poi.POI;
import com.lglab.goutam.simple_cms.create.utility.model.shape.Point;
import com.lglab.goutam.simple_cms.create.utility.model.shape.Shape;
import com.lglab.goutam.simple_cms.dialog.CustomDialogUtility;
import com.lglab.goutam.simple_cms.utility.ConstantPrefs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is in charge of getting the information of shape action
 */
public class CreateStoryBoardActionShapeActivity extends AppCompatActivity {


    private static final String TAG_DEBUG = "CreateStoryBoardActionShapeActivity";


    private TextView connectionStatus,
            locationName, locationNameTitle;
    private EditText duration, positionSave;

    private RecyclerView mRecyclerView;
    List<Point> points = new ArrayList<>();
    PointRecyclerAdapter mAdapter;

    private Handler handler = new Handler();
    private POI poi;
    private boolean isSave = false;
    private int position = -1;
    private int lastPosition = 0;
    private SwitchCompat switchCompatExtrude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_storyboard_action_shape);

        connectionStatus = findViewById(R.id.connection_status);
        locationName = findViewById(R.id.location_name);
        locationNameTitle = findViewById(R.id.location_name_title);
        duration = findViewById(R.id.duration);
        positionSave = findViewById(R.id.position_save);


        mRecyclerView = findViewById(R.id.my_recycler_view);
        switchCompatExtrude = findViewById(R.id.switch_button);

        Button buttTest = findViewById(R.id.butt_gdg);
        Button buttCancel = findViewById(R.id.butt_cancel);
        Button buttAdd = findViewById(R.id.butt_add);
        Button buttDelete = findViewById(R.id.butt_delete);
        Button buttAddPoint = findViewById(R.id.butt_add_point);
        Button buttDeletePoint = findViewById(R.id.butt_delete_point);
        Button buttDeletePoints = findViewById(R.id.butt_delete_points);

        Intent intent = getIntent();
        poi = intent.getParcelableExtra(ActionIdentifier.LOCATION_ACTIVITY.name());
        if (poi != null) {
            setTextView();
        }else{
            locationName.setVisibility(View.VISIBLE);
            locationNameTitle.setVisibility(View.VISIBLE);
            locationNameTitle.setText(getResources().getString(R.string.location_name_title_empty));
        }

        Shape shape = intent.getParcelableExtra(ActionIdentifier.SHAPES_ACTIVITY.name());
        position = intent.getIntExtra(ActionIdentifier.POSITION.name(), -1);
        lastPosition = intent.getIntExtra(ActionIdentifier.LAST_POSITION.name(), -1);
        int actionsSize = intent.getIntExtra(ActionIdentifier.ACTION_SIZE.name(), -1);

        int positionValue;
        if(position == -1) positionValue = actionsSize;
        else positionValue = position;
        positionValue++;
        positionSave.setText(String.valueOf(positionValue));

        if (shape != null) {
            position = intent.getIntExtra(ActionIdentifier.POSITION.name(), -1);
            isSave = true;
            buttAdd.setText(getResources().getString(R.string.button_save));
            buttDelete.setVisibility(View.VISIBLE);
            poi = shape.getPoi();
            setTextView();
            points = shape.getPoints();
            boolean isExtrude = shape.isExtrude();
            switchCompatExtrude.setChecked(isExtrude);
            duration.setText(String.valueOf(shape.getDuration()));
        }else {
            Point point = new Point();
            double temp = 0.0;
            point.setLongitude(temp);
            point.setLatitude(temp);
            point.setAltitude(temp);
            points.add(point);
            points.add(point);
        }

        SharedPreferences sharedPreferences = getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE);
        loadConnectionStatus(sharedPreferences);

        initRecyclerView();

        buttCancel.setOnClickListener((view) ->
                finish()
        );

        buttTest.setOnClickListener((view) ->
                testConnection()
        );

        buttAdd.setOnClickListener((view) ->
                addShape()
        );

        buttDelete.setOnClickListener((view) ->
                deleteShape()
        );

        buttAddPoint.setOnClickListener((view) -> {
            Point point = new Point();
            double temp = 0.0;
            point.setLongitude(temp);
            point.setLatitude(temp);
            point.setAltitude(temp);
            mAdapter.addPoint(point);
        });

        buttDeletePoint.setOnClickListener((view) -> {
            if(points.size() > 2 ) {
                mAdapter.deleteLastPoint();
            } else{
                CustomDialogUtility.showDialog(CreateStoryBoardActionShapeActivity.this,
                        getResources().getString(R.string.delete_point));
            }
        });

        buttDeletePoints.setOnClickListener( (view) -> {
            if(points.size() > 2){
               mAdapter.deleteAllPoints();
            } else{
                CustomDialogUtility.showDialog(CreateStoryBoardActionShapeActivity.this,
                        getResources().getString(R.string.delete_point));
            }
        });
    }

    /**
     * Test the action and the connection to the liquid galaxy
     */
    private void testConnection() {
        String durationString = duration.getText().toString();
        if(durationString.equals("")){
            CustomDialogUtility.showDialog(CreateStoryBoardActionShapeActivity.this, getResources().getString(R.string.activity_create_missing_duration_field_error));
        }else{
            AtomicBoolean isConnected = new AtomicBoolean(false);
            LGConnectionTest.testPriorConnection(this, isConnected);
            SharedPreferences sharedPreferences = getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE);
            handler.postDelayed(() -> {
                if(isConnected.get()){
                    Shape shape = new Shape().setPoi(poi).setPoints(points).setExtrude(switchCompatExtrude.isChecked()).setDuration(Integer.parseInt(durationString));
                    ActionController.getInstance().sendShape(shape, null);
                    ActionController.getInstance().cleanFileKMLs((shape.getDuration())*1000 - 200);
                }
                loadConnectionStatus(sharedPreferences);
            }, 1200);
        }
    }

    /**
     * Send the action of adding the shape
     */
    private void addShape() {
        String durationString = duration.getText().toString();
        if(durationString.equals("")){
            CustomDialogUtility.showDialog(CreateStoryBoardActionShapeActivity.this, getResources().getString(R.string.activity_create_missing_duration_field_error));
        }else{
            Shape shape = new Shape().setPoi(poi).setPoints(points)
                    .setExtrude(switchCompatExtrude.isChecked()).setDuration(Integer.parseInt(durationString));
            Intent returnInfoIntent = new Intent();
            returnInfoIntent.putExtra(ActionIdentifier.SHAPES_ACTIVITY.name(), shape);
            returnInfoIntent.putExtra(ActionIdentifier.IS_SAVE.name(), isSave);
            returnInfoIntent.putExtra(ActionIdentifier.POSITION.name(),
                    Integer.parseInt(positionSave.getText().toString()) - 1);
            returnInfoIntent.putExtra(ActionIdentifier.LAST_POSITION.name(), lastPosition);
            setResult(Activity.RESULT_OK, returnInfoIntent);
            finish();
        }
    }

    /**
     * Send the action of deleting the shape
     */
    private void deleteShape() {
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
        locationName.setVisibility(View.VISIBLE);
        locationNameTitle.setVisibility(View.VISIBLE);
        locationNameTitle.setText(poi.getPoiLocation().getName());
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

    /**
     * Initiate the recycleview
     */
    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false) {
            @Override
            public boolean requestChildRectangleOnScreen(@NonNull RecyclerView parent, @NonNull View child, @NonNull Rect rect, boolean immediate, boolean focusedChildVisible) {

                if (((ViewGroup) child).getFocusedChild() instanceof EditText) {
                    return false;
                }

                return super.requestChildRectangleOnScreen(parent, child, rect, immediate, focusedChildVisible);
            }
        };
        mRecyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new PointRecyclerAdapter(points);
        mRecyclerView.setAdapter(mAdapter);
    }

}
