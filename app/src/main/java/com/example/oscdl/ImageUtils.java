package com.example.oscdl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ImageUtils {

    public static Bitmap resizeImage(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // create a matrix for the manipulation
        Matrix matrix = new Matrix();

        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }

    public static void saveBitmap(Bitmap bitmap, File file) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        out.flush(); // ensure all data is written to the file
        out.close();
    }


    public static void writeStringToFile(String string, File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write(string);
        writer.flush();
        writer.close();
    }


    public static void compressFolder(File srcFolder, File destZipFile) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(destZipFile);
             ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(fileOutputStream))) {

            compressDirectoryToZipFile(srcFolder, "", zipOutputStream);
        }
    }

    private static void compressDirectoryToZipFile(File currentDir, String path, ZipOutputStream out) throws IOException {
        File[] fileList = currentDir.listFiles();

        for (File file : fileList) {
            if (file.isDirectory()) {
                compressDirectoryToZipFile(file, path + file.getName() + "/", out);
            } else {
                try (FileInputStream in = new FileInputStream(currentDir + "/" + file.getName())) {

                    out.putNextEntry(new ZipEntry(path + file.getName()));

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }

                    out.closeEntry();
                }
            }
        }
    }


}


