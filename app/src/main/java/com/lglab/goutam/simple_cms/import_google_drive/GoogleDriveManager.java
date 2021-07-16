package com.lglab.goutam.simple_cms.import_google_drive;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

/**
 * This class is in charge of the drive manager to connect to google drive
 */
public class GoogleDriveManager {

    public static final int RC_SIGN_IN = 0;
    public static final int RC_OPEN_FILE = 1;

    public static GoogleSignInClient GoogleSignInClient;
    public static DriveServiceHelper DriveServiceHelper;

    private static String OpenFileId;
    private static String OpenFileName;
    private static String OpenFileContent;

    /**
     * set to read only
     * @param fileName File name
     * @param fileContent File content
     */
    public static void setReadOnlyMode(String fileName, String fileContent) {
        OpenFileId = null;
        OpenFileName = fileName;
        OpenFileContent = fileContent;
    }

    /**
     * Set to read and write mode
     * @param fileId file id
     * @param fileName file Name
     * @param fileContent file Content
     */
    public static void setReadWriteMode(String fileId, String fileName, String fileContent) {
        OpenFileId = fileId;
        OpenFileName = fileName;
        OpenFileContent = fileContent;
    }
}
