package com.lglab.goutam.simple_cms.import_google_drive;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.lglab.goutam.simple_cms.create.CreateStoryBoardActivity;
import com.lglab.goutam.simple_cms.create.utility.model.StoryBoard;
import com.lglab.goutam.simple_cms.dialog.CustomDialogUtility;
import com.lglab.goutam.simple_cms.my_storyboards.StoryBoardConstant;
import com.lglab.goutam.simple_cms.top_bar.TobBarActivity;
import com.lglab.goutam.simple_cms.utility.ConstantPrefs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class is in charge of importing the information of google drive
 */
public class ImportGoogleDriveActivity extends TobBarActivity implements
        GoogleDriveStoryBoardRecyclerAdapter.OnNoteListener{

    private static final String TAG_DEBUG = "ImportGoogleDriveActivity";

    Button buttGoogleDrive, buttImportGoogleDrive, buttUpdateGoogleDrive;

    private RecyclerView mRecyclerView;
    List<StoryBoard> storyBoards = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_drive);

        buttGoogleDrive = findViewById(R.id.butt_connect_liquid_galaxy);
        mRecyclerView = findViewById(R.id.my_recycler_view);
        buttUpdateGoogleDrive = findViewById(R.id.butt_update);


        View topBar = findViewById(R.id.top_bar);
        buttImportGoogleDrive = topBar.findViewById(R.id.butt_import_google_drive);

        buttGoogleDrive.setOnClickListener( (view) -> requestSignIn());
        buttUpdateGoogleDrive.setOnClickListener( (view) -> requestSignIn());

        changeButtonClickableBackgroundColor();
    }


    @Override
    protected void onResume() {
        super.onResume();
        initRecyclerView();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecyclerView.setVisibility(View.GONE);
        buttUpdateGoogleDrive.setVisibility(View.GONE);
        buttGoogleDrive.setVisibility(View.VISIBLE);
        disconnect();
    }

    /**
     * Initiate the recycleview
     */
    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.Adapter mAdapter = new GoogleDriveStoryBoardRecyclerAdapter(storyBoards, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Change the background color and the option clickable to false of the button_google_drive
     */
    private void changeButtonClickableBackgroundColor() {
        changeButtonClickableBackgroundColor(getApplicationContext(), buttImportGoogleDrive);
    }

    /**
     * Request a sign and if it is not sign it starts a process to sign into google
     */
    public void requestSignIn() {
        if (isSignedIn()) {
            readGoogleDrive();
            return;
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE))
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
                    Dialog dialog = CustomDialogUtility.getDialog(ImportGoogleDriveActivity.this,"Signed in as " + googleAccount.getEmail());
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
                    readGoogleDrive();
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
    }

    /**
     * Read all the storyboards that are in google drive
     */
    public void readGoogleDrive() {
        if(GoogleDriveManager.DriveServiceHelper.files == null) {
            updateLocalData();
        } else {
            updateLocalData();
        }
    }

    /**
     * Update the recycler view with the information in google drive
     */
    public void updateLocalData() {
        Dialog dialog = CustomDialogUtility.getDialog(ImportGoogleDriveActivity.this, getResources().getString(R.string.message_google_drive_success_log_in));
        dialog.show();
        GoogleDriveManager.DriveServiceHelper.searchForAppFolderID(() -> {

            List<StoryBoard> storyBoardsGoogleDrive = new ArrayList<>();
            Map<String, String> files = GoogleDriveManager.DriveServiceHelper.files;
            for(String key : files.keySet()){
                StoryBoard storyBoard = new StoryBoard();
                storyBoard.setStoryBoardFileId(key);
                String name = files.get(key);
                if (name != null) {
                   name =  name.substring(0, name.length() - 5);
                }
                storyBoard.setName(name);
                storyBoardsGoogleDrive.add(storyBoard);
            }

            storyBoards = storyBoardsGoogleDrive;

            mRecyclerView.setVisibility(View.VISIBLE);
            buttUpdateGoogleDrive.setVisibility(View.VISIBLE);
            buttGoogleDrive.setVisibility(View.GONE);


            RecyclerView.Adapter mAdapter = new GoogleDriveStoryBoardRecyclerAdapter(storyBoards, this);
            mRecyclerView.setAdapter(mAdapter);

            dialog.dismiss();

        }, null);
    }

    @Override
    public void onNoteClick(int position) {
        StoryBoard selected = storyBoards.get(position);
        if(isSignedIn()){
            Dialog dialog = CustomDialogUtility.getDialog(ImportGoogleDriveActivity.this,
                    getResources().getString(R.string.message_downloading_file));
            dialog.show();
            GoogleDriveManager.DriveServiceHelper.readFile(selected.getStoryBoardFileId()).addOnSuccessListener((result) -> {
                SharedPreferences.Editor editor = getSharedPreferences(ConstantPrefs.SHARED_PREFS.name(), MODE_PRIVATE).edit();
                editor.putString(ConstantPrefs.STORY_BOARD_JSON.name(), result.second);
                editor.apply();
                Intent intent = new Intent(getApplicationContext(), CreateStoryBoardActivity.class);
                intent.putExtra(StoryBoardConstant.STORY_BOARD_JSON_ID.name(), selected.getStoryBoardFileId());
                startActivity(intent);
                dialog.dismiss();
            }).addOnFailureListener( (result -> {
                dialog.dismiss();
                CustomDialogUtility.showDialog(ImportGoogleDriveActivity.this, "It was not possible to download the file");
            }));
        }else{
            requestSignIn();
        }

    }
}
