package com.drprog.sjournal.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Romka on 10.08.2014.
 */
public class IOFiles {
    public static void copyFile(File src, File dst) throws IOException {
        try (FileInputStream in = new FileInputStream(src);
             FileOutputStream out = new FileOutputStream(dst)) {
            FileChannel inChannel = in.getChannel();
            FileChannel outChannel = out.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
    }

    public static void copyFile(Context context, File src, Uri dst) throws IOException {
        try (FileInputStream source = new FileInputStream(src);
             OutputStream out = context.getContentResolver().openOutputStream(dst)) {
            if (out == null) throw new IOException("Can't open target file!");
            byte[] buf = new byte[8192];
            int n;
            while ((n = source.read(buf)) > 0) {
                out.write(buf, 0, n);
            }
        }
    }

    public static void copyFile(Context context, Uri src, File dst) throws IOException {
        try (InputStream source = context.getContentResolver().openInputStream(src);
             FileOutputStream out = new FileOutputStream(dst)) {
            if (source == null) throw new IOException("Can't open source file!");
            byte[] buf = new byte[8192];
            int n;
            while ((n = source.read(buf)) > 0) {
                out.write(buf, 0, n);
            }
        }
    }

    public static List<String> readFileEx(Context context, Uri uri, String charsetName) throws IOException {
        List<String> stringList = new ArrayList<String>();

        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream), charsetName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringList.add(line);
            }
        }
        return stringList;
    }

    public static void writeFileEx(Context context, Uri fileUri, String str) {
        try (OutputStream out = context.getContentResolver().openOutputStream(fileUri)) {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
            bw.write(str);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyTestDB(Context context, String dbName, String fileName) throws IOException {
        InputStream myInput = context.getAssets().open(fileName);
        File currentDB = context.getDatabasePath(dbName);
        String outFileName = currentDB.getAbsolutePath();
        if (currentDB.exists()) currentDB.delete();
        currentDB.createNewFile();
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public static File saveDbToInternalDir(Context context, String dbName, String fileName) {
        File currentDB = context.getDatabasePath(dbName);
        File exportDir = context.getFilesDir();
        File toFile = new File(exportDir, fileName);
        if (toFile.exists()) return null;
        exportDir.mkdirs();
        try {
            toFile.createNewFile();
            copyFile(currentDB, toFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return toFile;
    }

    public static boolean exportDB(Context context, String dbName, Uri exportFileUri) {
        File currentDB = context.getDatabasePath(dbName);
        try {
            copyFile(context, currentDB, exportFileUri);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void restoreDB(Context context, String dbName, Uri sourceFileUri) throws IOException {
        File currentDB = context.getDatabasePath(dbName);
        if (currentDB.exists()) currentDB.delete();
        currentDB.createNewFile();
        copyFile(context, sourceFileUri, currentDB);
    }

    public static void restoreDB(Context context, String dbName, File backupFile) throws IOException {
        File currentDB = context.getDatabasePath(dbName);
        if (currentDB.exists()) currentDB.delete();
        currentDB.createNewFile();
        copyFile(backupFile, currentDB);
    }

    public static boolean saveImage(Context context, Bitmap finalBitmap, Uri exportFileUri) {
        try (OutputStream out = context.getContentResolver().openOutputStream(exportFileUri)) {
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
