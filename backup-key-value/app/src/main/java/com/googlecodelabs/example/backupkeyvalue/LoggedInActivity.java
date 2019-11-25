// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlecodelabs.example.backupkeyvalue;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.googlecodelabs.example.backupkeyvalue.R;

import java.io.File;
import java.io.RandomAccessFile;

public class LoggedInActivity extends AppCompatActivity {

    private static final String TAG = "LoggedInActivity";

    // Object for intrinsic lock
    static final Object sDataLock = new Object();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // You do not need to edit this activity

        setContentView(R.layout.activity_logged_in);

        TextView prefsDetails = findViewById(R.id.prefs_details);
        prefsDetails.setText(PrefUtils.getDebugText(this));

        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrefUtils.logout(LoggedInActivity.this);
                Intent intent = new Intent(LoggedInActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // You do not need to edit this activity
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            synchronized (LoggedInActivity.sDataLock) {
                File dataFile = new File(getFilesDir(), MyBackupAgent.BACKUP_FILE);
                RandomAccessFile raFile = new RandomAccessFile(dataFile, "rw");
                raFile.writeBytes("Hello World");
                raFile.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Unable to write to file");
        }
    }
}
