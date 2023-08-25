package com.drprog.sjournal.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.drprog.sjournal.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Romka on 10.08.2014.
 */
public class IOFiles {
    public static final String DIR_BACKUP = "backup";
    public static final String DIR_EXPORT_IMG = "export/img";
    public static final String FILE_BACKUP_EXT = ".dbk";
    private Context mainContext;
    private static String rootDirName = "SJournal_Data";

    public IOFiles(Context mainContext) {
        this.mainContext = mainContext;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null) { inChannel.close(); }
            if (outChannel != null) { outChannel.close(); }
        }
    }

    public static List<String> readFileEx(Context context, Uri uri, String charsetName) throws IOException {
        List<String> stringList = new ArrayList<String>();

        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringList.add(line);
            }
        }
        return stringList;
    }
    public static List<File> searchFile(File folder, String fileExt) {
        List<File> list = new ArrayList<File>();
        for (final File file : folder.listFiles()) {
            if ((!file.isDirectory()) && (file.getName().endsWith(fileExt))) {
                list.add(file);
            }
        }
        return list;
    }

    public static List<String> extractFileNames(List<File> fileList) {
        List<String> list = new ArrayList<String>();
        for (File file : fileList) {
            list.add(file.getName());
        }
        return list;
    }

    public static File getExternalPath(String dirName) {
        if (!isExternalStorageWritable()) {
            return null;
        }

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        if (rootDirName != null && !rootDirName.equals("")) path = path + "/" + rootDirName;
        if (dirName != null && !dirName.equals("")) path = path + "/" + dirName;
        File file = new File(path);
        file.mkdirs();
        return file;
    }

    public void writeFileEx(String dirName, String fileName, String str) {
        if (!isExternalStorageWritable()) {
            return;
        }
        try {
            File dir = getExternalPath(dirName);
            File file = new File(dir, fileName);
            if (file.exists()) {
                RunUtils.showToast(mainContext, R.string.error_file_exists);
            } else {
                file.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(str);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void copyTestDB(String dbName, String fileName) throws IOException {
        InputStream myInput = mainContext.getAssets().open(fileName);
        File currentDB = mainContext.getDatabasePath(dbName);
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

    public String saveDbToInternalDir(String dbName, String fileName) {
        File currentDB = mainContext.getDatabasePath(dbName);
        File exportDir = mainContext.getFilesDir();
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
        return toFile.getAbsolutePath();
    }

    public String exportDB(String dbName, String dirName, String fileName) {
        File currentDB = mainContext.getDatabasePath(dbName);
        File exportDir = getExternalPath(dirName);
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
        return toFile.getAbsolutePath();
    }

    public boolean isFileExists(String dirName, String fileName) {
        File dir = getExternalPath(dirName);
        File file = new File(dir, fileName);
        return !file.exists();
    }

    public String restoreDB(String dbName, String dirName, String fileName) {
        File currentDB = mainContext.getDatabasePath(dbName);
        File importDir = getExternalPath(dirName);
        File fromFile = new File(importDir, fileName);
        if (!importDir.exists()) {
            return null;
        }
        try {
            currentDB.createNewFile();
            copyFile(fromFile, currentDB);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return fromFile.getName();
    }

    public List<File> searchFile(String dirName, String fileExt) {
        File dir = getExternalPath(dirName);
        if (!dir.exists()) return null;
        return searchFile(dir, fileExt);
    }

    public String saveImage(Bitmap finalBitmap, String dirName, String fileName) {
        fileName += ".png";
        File exportDir = getExternalPath(dirName);
        exportDir.mkdirs();
        File file = new File (exportDir, fileName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }
}
