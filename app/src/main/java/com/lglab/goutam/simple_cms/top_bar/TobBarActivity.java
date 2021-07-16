package com.lglab.goutam.simple_cms.top_bar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.lglab.goutam.simple_cms.MainActivity;
import com.lglab.goutam.simple_cms.R;
import com.lglab.goutam.simple_cms.about.AboutActivity;
import com.lglab.goutam.simple_cms.account.ConstantsLogInLogOut;
import com.lglab.goutam.simple_cms.account.LogIn;
import com.lglab.goutam.simple_cms.create.CreateStoryBoardActivity;
import com.lglab.goutam.simple_cms.demo.DemoActivity;
import com.lglab.goutam.simple_cms.dialog.CustomDialogUtility;
import com.lglab.goutam.simple_cms.help.HelpActivity;
import com.lglab.goutam.simple_cms.import_google_drive.ImportGoogleDriveActivity;
import com.lglab.goutam.simple_cms.my_storyboards.MyStoryBoardActivity;
import com.lglab.goutam.simple_cms.utility.ConstantPrefs;
import com.lglab.goutam.simple_cms.web_scraping.WebScrapingActivity;


/**
 * This Activity is in charge of the flow between the activities. It is present in all the activities in the top.
 */
public class TobBarActivity extends AppCompatActivity {

    //private static final String TAG_DEBUG = "TobBarActivity";

    private static final int PERMISSION_CODE_UNPACK_NOT_LOCALLY = 1001;
    private static final int PERMISSION_CODE_UNPACK_LOCALLY = 1002;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Pass form the actual activity to the activity Create
     *
     * @param view The view which is call.
     */
    public void buttCreateMenu(View view) {
        Intent intent = new Intent(getApplicationContext(), CreateStoryBoardActivity.class);
        startActivity(intent);
    }

    /**
     * Pass form the actual activity to the activity My Sotry Board
     *
     * @param view The view which is call.
     */
    public void buttMyStoryboardsMenu(View view) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE_UNPACK_LOCALLY);
        } else {
            startMyStoryBoardsActivity();
        }
    }

    private void startMyStoryBoardsActivity() {
        Intent intent = new Intent(getApplicationContext(), MyStoryBoardActivity.class);
        startActivity(intent);
    }

    /**
     * Pass form the actual activity to the activity Import Google Drive
     *
     * @param view The view which is call.
     */
    public void buttImportGoogleDrive(View view) {
        if (isLogIn()) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE_UNPACK_NOT_LOCALLY);
            } else {
                startImportGoogleDriveActivity();
            }
        } else {
            CustomDialogUtility.showDialog(TobBarActivity.this,
                    getResources().getString(R.string.message_you_need_log_in));
        }
    }

    private void startImportGoogleDriveActivity() {
        Intent intent = new Intent(getApplicationContext(), ImportGoogleDriveActivity.class);
        startActivity(intent);
    }

    /**
     * Pass form the actual activity to the activity Main Activity (connect)
     *
     * @param view The view which is call.
     */
    public void buttConnectMenu(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    /**
     * Pass form the actual activity to the activity Scraping
     *
     * @param view The view which is call.
     */
    public void buttScraping(View view) {
        Intent intent = new Intent(getApplicationContext(), WebScrapingActivity.class);
        startActivity(intent);
    }

    /**
     * Pass form the actual activity to the activity LogIn
     *
     * @param view The view which is call.
     */
    public void buttAccount(View view) {
        Intent intent = new Intent(getApplicationContext(), LogIn.class);
        startActivity(intent);
    }

    /**
     * Pass form the actual activity to the activity About
     *
     * @param view The view which is call.
     */
    public void buttDemo(View view) {
        Intent intent = new Intent(getApplicationContext(), DemoActivity.class);
        startActivity(intent);
    }

    /**
     * Pass form the actual activity to the activity About
     *
     * @param view The view which is call.
     */
    public void buttAbout(View view) {
        Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
        startActivity(intent);
    }

    /**
     * Pass form the actual activity to the activity help
     *
     * @param view The view which is call.
     */
    public void buttHelp(View view) {
        Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
        startActivity(intent);
    }

    /**
     * Change the background color and the option clickable to false of the button_connect
     *
     * @param context The context which is the button
     * @param button  The button that need to be modify
     */
    public void changeButtonClickableBackgroundColor(Context context, Button button) {
        button.setBackgroundColor(ContextCompat.getColor(context, R.color.background));
        button.setTextColor(ContextCompat.getColor(context, R.color.textColorClick));
        button.setClickable(false);
    }

    /**
     * @return true if is log in and false is false
     */
    public boolean isLogIn() {
        SharedPreferences sharedPreferences = getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE);
        return sharedPreferences.getBoolean(ConstantsLogInLogOut.IS_LOGIN.name(), false);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      if (requestCode == PERMISSION_CODE_UNPACK_NOT_LOCALLY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startImportGoogleDriveActivity();
            } else {
                CustomDialogUtility.showDialog(this, getResources().getString(R.string.alert_permission_denied_import_google_drive));
            }
        } else if (requestCode == PERMISSION_CODE_UNPACK_LOCALLY){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startMyStoryBoardsActivity();
            } else {
                CustomDialogUtility.showDialog(this, getResources().getString(R.string.alert_permission_denied_import_locally));
            }
        }
    }

}
