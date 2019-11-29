package com.googlecodelabs.example.backupkeyvalue;


import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;


public class BackupConfigParser {
    // We don't use namespaces
    private static final String ns = null;

    private static final String TAG = "BackupConfigParser";


    public BackupConfig parse(InputStream in) throws XmlPullParserException, IOException {

        BackupConfig.BackupConfigBuilder builder = new BackupConfig.BackupConfigBuilder();

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            readConfig(parser, builder);

            BackupConfig config = builder.build();
            return config;

        } catch (Exception e) {
            Log.e(TAG, "parse exception: " + e.toString());
            throw e;
        } finally {
            in.close();
        }
    }


    private void readConfig(XmlPullParser parser, BackupConfig.BackupConfigBuilder builder) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Config");

        String enable = parser.getAttributeValue(null, "enable");
        if (enable != null && enable.equalsIgnoreCase("true")) {
            builder.setEnable(true);
        }

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            // Starts by looking for the Package tag
            if (name.equals("Backup")) {
                readBackup(parser, builder);
            } else {
                skip(parser);
            }

        }
    }

    private void readBackup(XmlPullParser parser, BackupConfig.BackupConfigBuilder builder) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Backup");

        String intent = parser.getAttributeValue(null, "Intent");
        builder.setStartIntent(intent);

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            // Starts by looking for the DefaultPackages tag
            if (name.equalsIgnoreCase("DefaultPackages")) {
                readDefaultPackages(parser, builder);
            } else if (name.equalsIgnoreCase("OverridePackages")) {
                readOverridePackages(parser, builder);
            } else {
                skip(parser);
            }

        }
    }



    private void readDefaultPackages(XmlPullParser parser, BackupConfig.BackupConfigBuilder builder) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "DefaultPackages");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            // Starts by looking for the Package tag
            if (name.equals("Package")) {
                builder.addPackageConfig(readPackage(parser, false));
            } else {
                skip(parser);
            }
        }
    }


    private void readOverridePackages(XmlPullParser parser, BackupConfig.BackupConfigBuilder builder) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "OverridePackages");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            // Starts by looking for the Package tag
            if (name.equals("Package")) {
                builder.addPackageConfig(readPackage(parser, true));
            } else {
                skip(parser);
            }
        }
    }

    private BackupConfig.PackageConfig readPackage(XmlPullParser parser, boolean override) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Package");
        String name;
        String folder;
        name = parser.getAttributeValue(null, "name");
        folder = parser.getAttributeValue(null, "folder");
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, ns, "Package");
        return new BackupConfig.PackageConfig(name, folder, override);
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
