package com.googlecodelabs.example.backupkeyvalue;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.SharedPreferencesBackupHelper;
import android.app.backup.FileBackupHelper;
import android.os.ParcelFileDescriptor;

import java.io.IOException;

// https://developer.android.com/guide/topics/data/keyvaluebackup

public class MyBackupAgent extends BackupAgentHelper {
    // The name of the SharedPreferences file
    static final String PREFS = "user_preferences";

    // A key to uniquely identify the set of backup data
    static final String PREFS_BACKUP_KEY = "prefs";

    // The name of the file
    static final String BACKUP_FILE = "backup_file";

    // A key to uniquely identify the set of backup data
    static final String FILES_BACKUP_KEY = "myfiles";


    // Allocate a helper and add it to the backup agent
    @Override
    public void onCreate() {
        SharedPreferencesBackupHelper helper =
                new SharedPreferencesBackupHelper(this, PREFS);
        addHelper(PREFS_BACKUP_KEY, helper);


        FileBackupHelper fileBackupHelper = new FileBackupHelper(this, BACKUP_FILE);
        addHelper(FILES_BACKUP_KEY, fileBackupHelper);
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
                         ParcelFileDescriptor newState) throws IOException {
        // Hold the lock while the FileBackupHelper performs backup
        synchronized (LoggedInActivity.sDataLock) {
            super.onBackup(oldState, data, newState);
        }
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode,
                          ParcelFileDescriptor newState) throws IOException {
        // Hold the lock while the FileBackupHelper restores the file
        synchronized (LoggedInActivity.sDataLock) {
            super.onRestore(data, appVersionCode, newState);
        }
    }
}
