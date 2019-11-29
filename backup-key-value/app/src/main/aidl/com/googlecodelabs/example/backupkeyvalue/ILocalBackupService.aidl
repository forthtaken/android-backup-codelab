// ILocalBackupService.aidl
package com.googlecodelabs.example.backupkeyvalue;

// Declare any non-default types here with import statements

interface ILocalBackupService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

    boolean isExternalMediaMounted();

    String getExternalMediaFolder(String packageName);
}
