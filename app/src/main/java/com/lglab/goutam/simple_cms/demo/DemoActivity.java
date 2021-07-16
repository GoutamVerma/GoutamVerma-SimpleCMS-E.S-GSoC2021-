package com.lglab.goutam.simple_cms.demo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.lglab.goutam.simple_cms.R;
import com.lglab.goutam.simple_cms.create.utility.connection.LGConnectionTest;
import com.lglab.goutam.simple_cms.create.utility.model.Action;
import com.lglab.goutam.simple_cms.create.utility.model.StoryBoard;
import com.lglab.goutam.simple_cms.dialog.CustomDialogUtility;
import com.lglab.goutam.simple_cms.top_bar.TobBarActivity;
import com.lglab.goutam.simple_cms.utility.ConstantPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DemoActivity extends TobBarActivity {

    private static final String TAG_DEBUG = "DemoActivity";

    private Dialog dialog;
    private DemoThread demoThread = null;
    private Handler handler = new Handler();
    private TextView connectionStatus;
    private List<Action> actionsSaved = new ArrayList<>();
    private Button buttDemo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        View topBar = findViewById(R.id.top_bar);
        buttDemo = topBar.findViewById(R.id.butt_demo);
        connectionStatus = findViewById(R.id.connection_status);

        changeButtonClickableBackgroundColor();
        SharedPreferences sharedPreferences = getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE);
        loadConnectionStatus(sharedPreferences);
    }

    /**
     * Run the demo that is pre saved
     */
    private void runDemo() {
        getDialog();
        if (actionsSaved.isEmpty()) {
            try {
                String string = readDemoFile();
                StoryBoard storyBoard = new StoryBoard();
                JSONObject jsonStoryBoard =  new JSONObject(string);
                storyBoard.unpack(jsonStoryBoard);
                actionsSaved = storyBoard.getActions();
                testStoryboard(actionsSaved);
            } catch (JSONException e) {
                Log.w(TAG_DEBUG, "ERROR CONVERTING FILE: " + e.getMessage());
            }
        }else{
            testStoryboard(actionsSaved);
        }
    }

    private String readDemoFile() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("demo.txt"), StandardCharsets.UTF_8));

            StringBuilder string = new StringBuilder();
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                string.append(mLine);
            }
            return string.toString();
        } catch (IOException e) {
            Log.w(TAG_DEBUG, "ERROR READING FILE: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.w(TAG_DEBUG, "ERROR CLOSING: " + e.getMessage());
                }
            }
        }
        return "";
    }

    /**
     * It gives a dialog with a cancel button
     */
    private void getDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DemoActivity.this);
        @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(R.layout.dialog_fragment, null);
        v.getBackground().setAlpha(220);
        Button ok = v.findViewById(R.id.ok);
        ok.setText(getResources().getString(R.string.stop_demo));
        ok.setOnClickListener(view -> {
            stopDemo();
            CustomDialogUtility.showDialog(DemoActivity.this, getResources().getString(R.string.stop_message));
        });
        TextView textMessage = v.findViewById(R.id.message);
        textMessage.setText(getResources().getString(R.string.start_demo));
        textMessage.setTextSize(23);
        textMessage.setGravity(View.TEXT_ALIGNMENT_CENTER);
        builder.setView(v);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
    }


    /**
     * Test the connection and then do the tour
     *
     * @param actionsSaved The list of actions or null if is a test
     */
    private void testStoryboard(List<Action> actionsSaved) {
        AtomicBoolean isConnected = new AtomicBoolean(false);
        LGConnectionTest.testPriorConnection(this, isConnected);
        SharedPreferences sharedPreferences = getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE);
        handler.postDelayed(() -> {
            if (isConnected.get()) {
                dialog.show();
                demoThread = new DemoThread(actionsSaved, DemoActivity.this, dialog);
                demoThread.start();
            }
            loadConnectionStatus(sharedPreferences);
        }, 1200);
    }

    /**
     * Stop the demo
     */
    private void stopDemo() {
        demoThread.stop();
        dialog.dismiss();
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
     * Change the background color and the option clickable to false of the button_connect
     */
    private void changeButtonClickableBackgroundColor() {
        changeButtonClickableBackgroundColor(getApplicationContext(), buttDemo);
    }

    public void buttDemoRun(View view) {
        runDemo();
    }

    @Override
    protected void onPause() {
        if (demoThread != null) stopDemo();
        demoThread = null;
        super.onPause();
    }
}
