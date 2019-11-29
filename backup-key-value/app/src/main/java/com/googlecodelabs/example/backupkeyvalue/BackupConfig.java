package com.googlecodelabs.example.backupkeyvalue;

import android.util.Log;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class BackupConfig {

    static class BackupConfigBuilder {

        private boolean isEnabled = false;
        private String startIntent;
        private List packageConfigList = new ArrayList();

        public BackupConfig build() {
            BackupConfig instance = new BackupConfig();
            instance.isEnabled = this.isEnabled;
            instance.startIntent = this.startIntent;

            for (int i = 0; i < packageConfigList.size(); i++) {
                PackageConfig config = (PackageConfig)packageConfigList.get(i);
                instance.packageBackupConfigs.put(config.name, config);
            }

            return instance;
        }

        BackupConfigBuilder setEnable(boolean isEnabled) {
            this.isEnabled = isEnabled;
            return this;
        }

        BackupConfigBuilder setStartIntent(String intent) {
            this.startIntent = intent;
            return this;
        }

        BackupConfigBuilder addPackageConfig(PackageConfig config) {
            Log.d("BackupConfigBuilder", "addPackageConfig: " + config.name);
            packageConfigList.add(config);
            return this;
        }
    }


    static class PackageConfig {
        public final String name;
        public final String folder;
        public boolean isOverride;

        public PackageConfig(String name, String folder, boolean isOverride) {
            this.name = name;
            this.folder = folder;
            this.isOverride = isOverride;
        }
    }

    private boolean isEnabled = false;
    private String startIntent;
    private String rootFolder;
    Hashtable<String, PackageConfig> packageBackupConfigs = new Hashtable<String, PackageConfig>();


    private BackupConfig() {
    }

    /**
     *
     * */
    public static void reload() {

    }

    public boolean isEnable() {
        return isEnabled;
    }

    public String getStartIntent() {
        return startIntent;
    }

    public String getRootFolder() {
        return rootFolder;
    }

    /**
     *
     * @param packageName
     * @return
     */
    public String getBackupFolder(String packageName) {
        PackageConfig config = packageBackupConfigs.get(packageName);
        if (config == null) {
            return packageName;
        }

        return config.folder;
    }

    /**
     *
     * @param packageName
     * @return flag indicating
     */
    public boolean isPackageBackupOverride(String packageName) {
        PackageConfig config = packageBackupConfigs.get(packageName);
        if (config == null) {
            return false;
        }

        return config.isOverride;
    }
}
