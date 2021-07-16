package com.lglab.goutam.simple_cms.create;

import android.app.Dialog;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.lglab.goutam.simple_cms.R;
import com.lglab.goutam.simple_cms.dialog.CustomDialogUtility;
import com.lglab.goutam.simple_cms.import_google_drive.DriveServiceHelper;
import com.lglab.goutam.simple_cms.import_google_drive.GoogleDriveManager;
import com.lglab.goutam.simple_cms.top_bar.TobBarActivity;

import java.util.Collections;

/**
 * This class is in charge of exporting the information to google drive
 */
public class ExportGoogleDriveActivity extends TobBarActivity {

    private static final String TAG_DEBUG = "ExportGoogleDriveActivity";

    private String jsonToUpload;
    private String jsonNameToUpload;
    private String fileId;
    private AppCompatActivity activity;


    /**
     * Request a sign and if it is not sign it starts a process to sign into google
     * @param storyBoardJson the json to be upload
     * @param name the name of the storyboard
     * @param fileId the id of the storyboard
     */
    public void requestSignIn(String storyBoardJson, String name, String fileId, AppCompatActivity activity) {
        this.jsonToUpload = storyBoardJson;
        this.jsonNameToUpload = name;
        this.fileId = fileId;
        this.activity = activity;
        if (isSignedIn()) {
            setFileGoogleDrive();
            return;
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                //The scope should be changed in order to see other files... https://developers.google.com/drive/api/v3/about-auth https://www.googleapis.com/auth/drive
                .build();

        GoogleDriveManager.GoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        if (account != null)
            GoogleDriveManager.GoogleSignInClient.signOut();

        Intent signInIntent = GoogleDriveManager.GoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GoogleDriveManager.RC_SIGN_IN);
    }

    /**
     * check if the user is signIn
     * @return true if the user is sign in and false if not
     */
    public boolean isSignedIn() {
        return GoogleDriveManager.GoogleSignInClient != null && GoogleDriveManager.DriveServiceHelper != null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    /**
     * Disconnect the user of google drive
     */
    public void disconnect(){
        GoogleDriveManager.GoogleSignInClient = null;
        GoogleDriveManager.DriveServiceHelper = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (requestCode == GoogleDriveManager.RC_SIGN_IN) {
            if (resultCode == RESULT_OK && resultData != null) {
                handleSignInResult(resultData);
                return;
            }
            Log.w(TAG_DEBUG, "Sign-in failed with resultCode = " + resultCode);
            onFailedLogIn();
        }
    }

    /**
     * Creates a connection to google drive
     * @param result result of the sign in
     */
    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(googleAccount -> {
                    Log.w(TAG_DEBUG, "Signed in as " + googleAccount.getEmail());
                    Dialog dialog = CustomDialogUtility.getDialog(ExportGoogleDriveActivity.this,"Signed in as " + googleAccount.getEmail());
                    dialog.show();

                    // Use the authenticated account to sign in to the Drive service.
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    getApplicationContext(), Collections.singleton(DriveScopes.DRIVE_FILE)); //DRIVE
                    credential.setSelectedAccount(googleAccount.getAccount());
                    Drive googleDriveService = new Drive.Builder(new NetHttpTransport(), new GsonFactory(), credential)
                            .setApplicationName("SimpleCMS")
                            .build();

                    GoogleDriveManager.DriveServiceHelper = new DriveServiceHelper(googleDriveService);
                    dialog.dismiss();
                    setFileGoogleDrive();
                })
                .addOnFailureListener(exception ->  {
                    Log.w(TAG_DEBUG, "Unable to sign in.", exception);
                    onFailedLogIn();
                });
    }

    /**
     * Show a message to the user of a failed log in
     */
    public void onFailedLogIn() {
        CustomDialogUtility.showDialog(this, getResources().getString(R.string.message_google_drive_failed_log_in));
        this.jsonToUpload = null;
        this.jsonNameToUpload = null;
    }

    /**
     * Export the file and save it in google drive
     */
    public void setFileGoogleDrive(){
        Dialog dialog = CustomDialogUtility.getDialog(ExportGoogleDriveActivity.this,
                getResources().getString(R.string.message_uploading_file));
        dialog.show();
        if(fileId == null) {
            GoogleDriveManager.DriveServiceHelper.createFile(jsonNameToUpload)
                    .addOnSuccessListener((result) -> GoogleDriveManager.DriveServiceHelper.saveFile(result, jsonNameToUpload, jsonToUpload, activity)
                            .addOnFailureListener(exception -> {
                                dialog.dismiss();
                                CustomDialogUtility.showDialog(ExportGoogleDriveActivity.this,
                                        getResources().getString(R.string.message_failed_upload));
                                this.jsonToUpload = null;
                                this.jsonNameToUpload = null;
                            })
                            .addOnSuccessListener(result2 -> {
                                dialog.dismiss();
                                CustomDialogUtility.showDialog(ExportGoogleDriveActivity.this,
                                        getResources().getString(R.string.message_success_upload));
                                this.jsonToUpload = null;
                                this.jsonNameToUpload = null;
                            }))
                    .addOnFailureListener(exception -> {
                        dialog.dismiss();
                        CustomDialogUtility.showDialog(ExportGoogleDriveActivity.this,
                                getResources().getString(R.string.message_failed_upload));
                        this.jsonToUpload = null;
                        this.jsonNameToUpload = null;
                    });
        }else{
            GoogleDriveManager.DriveServiceHelper.saveFile(fileId, jsonNameToUpload, jsonToUpload, activity)
                    .addOnSuccessListener((result) -> {
                        dialog.dismiss();
                        CustomDialogUtility.showDialog(ExportGoogleDriveActivity.this,
                                getResources().getString(R.string.message_success_upload));
                        this.jsonToUpload = null;
                        this.jsonNameToUpload = null;
                    })
                    .addOnFailureListener((result) -> {
                        dialog.dismiss();
                        Log.w(TAG_DEBUG, "RESULT ERROR:" + result.getMessage());
                        CustomDialogUtility.showDialog(ExportGoogleDriveActivity.this,
                                getResources().getString(R.string.message_failed_upload));
                        this.jsonToUpload = null;
                        this.jsonNameToUpload = null;
                    });
        }
    }


}
