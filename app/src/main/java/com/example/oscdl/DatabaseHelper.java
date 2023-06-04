package com.example.oscdl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "themeDatabase";

    private static final String TABLE_NAME = "themes";
    private static final String COLUMN_ZIP_PATH = "zipFilePath";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_ICON = "icon";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ZIP_PATH + " TEXT, " + COLUMN_NAME + " TEXT, " + COLUMN_ICON + " BLOB)";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop and recreate table if database version changes
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addTheme(ThemeItem theme) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ZIP_PATH, theme.getZipFilePath());
        cv.put(COLUMN_NAME, theme.getName());

        // convert Bitmap to byte array for storage
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        theme.getIcon().compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        cv.put(COLUMN_ICON, byteArray);

        long insert = db.insert(TABLE_NAME, null, cv);
        return insert != -1;
    }

    public void deleteTheme(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ZIP_PATH},
                COLUMN_NAME + "=?",
                new String[]{name},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(COLUMN_ZIP_PATH);
            if (columnIndex != -1) {
                String zipFilePath = cursor.getString(columnIndex);
                File file = new File(zipFilePath);
                if (file.exists()) {
                    boolean deleted = file.delete();
                    if (!deleted) {
                        Log.e("DatabaseHelper", "Failed to delete file: " + zipFilePath);
                    }
                }
            } else {
                Log.e("DatabaseHelper", "Column index not found for COLUMN_ZIP_PATH");
            }
        } else {
            Log.e("DatabaseHelper", "No matching entry found for theme with name: " + name);
        }

        if (cursor != null) {
            cursor.close();
        }

        db.delete(TABLE_NAME, COLUMN_NAME + "=?", new String[]{name});
    }



    public ArrayList<String> getZipFilePaths() {
        ArrayList<String> zipFilePaths = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ZIP_PATH + " FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                zipFilePaths.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return zipFilePaths;
    }

    public ArrayList<String> getNames() {
        ArrayList<String> names = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_NAME + " FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return names;
    }

    public ArrayList<Bitmap> getIcons() {
        ArrayList<Bitmap> icons = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ICON + " FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                byte[] iconBytes = cursor.getBlob(0);
                icons.add(BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.length));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return icons;
    }

    public ArrayList<ThemeItem> getThemeItems() {
        ArrayList<ThemeItem> themeItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                int zipFilePathIndex = cursor.getColumnIndex(COLUMN_ZIP_PATH);
                int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
                int iconIndex = cursor.getColumnIndex(COLUMN_ICON);

                if (zipFilePathIndex == -1 || nameIndex == -1 || iconIndex == -1) {
                    Log.e("DatabaseHelper", "Column not found in database");
                    continue;
                }

                String zipFilePath = cursor.getString(zipFilePathIndex);
                String name = cursor.getString(nameIndex);
                byte[] iconBytes = cursor.getBlob(iconIndex);
                Bitmap icon = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.length);

                ThemeItem themeItem = new ThemeItem(zipFilePath, name, icon);
                themeItems.add(themeItem);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return themeItems;
    }

}

