package com.lglab.goutam.simple_cms.import_google_drive;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.lglab.goutam.simple_cms.R;
import com.lglab.goutam.simple_cms.dialog.CustomDialogUtility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This class is a helper to connect to google drive
 */
public class DriveServiceHelper {

    private static final String TAG_DEBUG = "DriveServiceHelper";

    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive mDriveService;
    public Map<String, String> files;

    private static final String FOLDER_MIME_TYPE = "application/vnd.google-apps.folder";
    private static final String JSON_MIME_TYPE = "application/json";
    private static final String FOLDER_ID = "1mcopksgc9VZicFTnK5zsPEIJRDegksRw";
    private static final long MAX_SIZE = 5242880;

    public DriveServiceHelper(Drive driveService) {
        mDriveService = driveService;
    }

    /**
     * Create a file in google Drive
     *
     * @param title The tittle of the new file
     * @return the id of the file
     */
    public Task<String> createFile(String title) {
        return Tasks.call(mExecutor, () -> {

            //FOLDER IN LIQUID GALAXY
            try{

                File metadata2 = new File()
                        .setParents(Collections.singletonList(FOLDER_ID))
                        .setMimeType(JSON_MIME_TYPE)
                        .setName(title);

                File googleFileLiquidGalaxy = mDriveService.files().create(metadata2).setFields("id").execute();
                if (googleFileLiquidGalaxy == null) {
                    Log.w(TAG_DEBUG, "Null result when requesting file creation.");
                }else{
                    Log.w(TAG_DEBUG, "FILE googleFileLiquidGalaxy ID:" + googleFileLiquidGalaxy.getId());
                }

                Log.w(TAG_DEBUG, "FILE CREATED ID:" + googleFileLiquidGalaxy.getId());
                return googleFileLiquidGalaxy.getId();

            }catch (Exception e){
                Log.w(TAG_DEBUG, "EXCEPTION: " +e.getMessage());
                throw new Exception();
            }

        });
    }

    /**
     * Read a file in google drive
     *
     * @param fileId The file id that is going to be read
     * @return The id and the file in a string
     */
    public Task<Pair<String, String>> readFile(String fileId) {
        return Tasks.call(mExecutor, () -> {
            // Retrieve the metadata as a File object.
            File metadata = mDriveService.files().get(fileId).execute();
            String name = metadata.getName();

            // Stream the file contents to a String.
            try (InputStream is = mDriveService.files().get(fileId).executeMediaAsInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                String contents = stringBuilder.toString();

                return Pair.create(name, contents);
            }
        });
    }

    /**
     * Updates the file identified by {@code fileId} with the given {@code name} and {@code
     * content}.
     */
    public Task<Void> saveFile(String fileId, String name, String content, AppCompatActivity activity) {
        return Tasks.call(mExecutor, () -> {
            // Create a File containing any metadata changes.
            File metadata = new File().setName(name);

           // Convert content to an AbstractInputStreamContent instance.
            ByteArrayContent contentStream = ByteArrayContent.fromString("application/json", content);

            long length = contentStream.getLength();

            if(length < MAX_SIZE){
                // Update the metadata and contents.
                mDriveService.files().update(fileId, metadata, contentStream).execute();
                Log.w(TAG_DEBUG, "SIZE: " +  length);
            }else{
                CustomDialogUtility.showDialog(activity, activity.getResources().getString(R.string.message_file_less_5MB));
            }
            return null;
        });
    }

    /**
     * It search for the app folder
     *
     * @param onSuccess Runnable
     * @param onFailure Runnable
     */
     public void searchForAppFolderID(Runnable onSuccess, Runnable onFailure) {
        Tasks.call(mExecutor, () -> mDriveService.files().list().setQ("mimeType = '" + FOLDER_MIME_TYPE + "' and name = 'CMS' and parents in 'root' ").setSpaces("drive").execute())
                .addOnSuccessListener(fileList -> {
                        searchForFilesInsideAppFolderID(onSuccess, onFailure);
                })
                .addOnFailureListener(exception -> {
                    Log.w(TAG_DEBUG, "Unable to search for appfolder", exception);
                    if (onFailure != null)
                        onFailure.run();
                });
    }

    /**
     * It search all the files inside the app folder
     *
     * @param onSuccess Runnable
     * @param onFailure Runnable
     */
    private void searchForFilesInsideAppFolderID(Runnable onSuccess, Runnable onFailure) {
        queryFiles()
                .addOnSuccessListener(fileList -> {
                    files = new HashMap<>();
                    for (File file : fileList) {
                        files.put(file.getId(), file.getName());
                    }
                    onSuccess.run();
                })
                .addOnFailureListener(exception -> {
                    Log.w(TAG_DEBUG, "Unable to search for for files inside appfolder", exception);
                    if (onFailure != null)
                        onFailure.run();
                });
    }

    /**
     * Get all the files
     *
     * @return The list of files
     */
    private Task<List<File>> queryFiles() {
        return Tasks.call(mExecutor, () -> {
            List<File> fileList = new ArrayList<>();
            String pageToken = null;
            do {
                FileList result = mDriveService.files().list()
                        .setQ("'"+ FOLDER_ID + "' in parents and mimeType = 'application/json' and trashed = false")
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageToken(pageToken)
                        .execute();

                fileList.addAll(result.getFiles());
                pageToken = result.getNextPageToken();
            } while (pageToken != null);
            return fileList;
        });
    }
}
