package com.googlecodelabs.example.backupkeyvalue;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.io.InputStream;


/**
 * Main tasks:
 *
 * 1. Load backup config file when 1. system reboot, 2. SD is mounted
 * 2. Expose backup configuration to other processes, so backup behavior can be configured accordingly.
 *
 */
public class LocalBackupService extends Service {

    private static final String TAG = "LocalBackupService";

    private BackupConfig backupConfig;

    private boolean isExternalMediaMounted = false;
    private boolean USE_EXTERNAL_MEDIA_INTENT = true;
    private boolean USE_USB_DEVICE_ATTACHED_INTENT = true;


    /** BroadcastReceiver receive handler */
    private BroadcastReceiver mediaMountUnmountReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (USE_EXTERNAL_MEDIA_INTENT) {
                    if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
                        Log.d(TAG, "ACTION_MEDIA_MOUNTED");

                        // TODO: perform local backup configuration.
                        isExternalMediaMounted = true;

                        // load backup config whenever external media is attached
                        loadBackupConfig();
                    }

                    if (intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                        Log.w(TAG, "ACTION_MEDIA_UNMOUNTED");
                        isExternalMediaMounted = false;
                    }
                }

                if (USE_USB_DEVICE_ATTACHED_INTENT) {
                    if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                        Log.d(TAG, "ACTION_USB_DEVICE_ATTACHED");

                        // load backup config whenever external media is attached
                        loadBackupConfig();
                    }

                    if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                        Log.w(TAG, "ACTION_USB_DEVICE_DETACHED");
                    }
                }


            } catch (Exception e) {
                Log.e(TAG, "BroadcastReceiver onReceive exception: " + e.toString());
            }

        }
    };

    /**
     * ILocalBackupService implementation
     * */
    private final com.googlecodelabs.example.backupkeyvalue.ILocalBackupService.Stub binder = new com.googlecodelabs.example.backupkeyvalue.ILocalBackupService.Stub() {
        public String getExternalMediaFolder(String packageName) {
            // return packageName;
            if (backupConfig != null) {
                return backupConfig.getBackupFolder(packageName);
            }

            return packageName;
        }

        public boolean isExternalMediaMounted() {
            return isExternalMediaMounted;
        }
    };

    /**
     * register media mount/unmount events
     * */
    private void registerIntentReceiver() {
        IntentFilter intentFilter = new IntentFilter();

        if (USE_EXTERNAL_MEDIA_INTENT) {
            intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
            intentFilter.addDataScheme("file");
        }

        if (USE_USB_DEVICE_ATTACHED_INTENT) {
            intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        }

        registerReceiver(mediaMountUnmountReceiver, intentFilter);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate");

        // register media mount/unmount events
        registerIntentReceiver();

        // load backup config whenever service is created
        loadBackupConfig();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        // Return the interface
        return binder;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

        // unregister media mount/unmount events
        unregisterReceiver(mediaMountUnmountReceiver);
    }

    /**
     * load backup config file
     * */
    private void loadBackupConfig() {
        // TODO: perform this task using background thread
        try {
            InputStream is = this.getAssets().open("backup_config.xml");
            BackupConfigParser backupConfigParser = new BackupConfigParser();
            backupConfig = backupConfigParser.parse(is);
        } catch (Exception e) {
            Log.d(TAG, "loadBackupConfig exception: " + e);
        }
    }
}
