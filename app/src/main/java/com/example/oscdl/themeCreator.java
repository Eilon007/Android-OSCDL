package com.example.oscdl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class themeCreator extends AppCompatActivity {

    private static final int REQUEST_IMAGE_SELECT = 0;
    private EditText nameEditText;
    private Button button;
    private Uri selectedImageUri;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_creator);

        // Request permissions
        ActivityCompat.requestPermissions(themeCreator.this,
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        nameEditText = findViewById(R.id.nameEditText);
        button = findViewById(R.id.button);
        Button createTheme = findViewById(R.id.create);

        createTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap != null && nameEditText.getText().toString().isEmpty() == false)
                {
                    createTheme();
                }
                else
                {
                    Toast.makeText(themeCreator.this, "Please fill in the required fields. ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_SELECT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_SELECT && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            try
            {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                ImageView chosen = findViewById(R.id.chosenImage);
                chosen.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void endActivity() {
        this.finish();
    }

    private void createTheme()
    {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
            Bitmap background = ImageUtils.resizeImage(bitmap, 640, 480);
            Bitmap backgroundWide = ImageUtils.resizeImage(bitmap, 852, 480);

            String name = nameEditText.getText().toString() + "theme";

            // Create the parent folder structure
            File parentFolder = new File(getFilesDir() + "/" + name);
            parentFolder.mkdirs();

            File appFolder = new File(parentFolder, "/apps/" + name);
            appFolder.mkdirs();

            File themeFolder = new File(appFolder, "theme");
            themeFolder.mkdir();

            // Create the files in the theme folder
            File iconFile = new File(appFolder, "icon.png");
            Bitmap resizedIcon = ImageUtils.resizeImage(bitmap, 128, 48);
            ImageUtils.saveBitmap(resizedIcon, iconFile);

            addtoTheme(themeFolder);

            File backgroundFile = new File(themeFolder, "background.png");
            ImageUtils.saveBitmap(background, backgroundFile);

            File backgroundWideFile = new File(themeFolder, "background_wide.png");
            ImageUtils.saveBitmap(backgroundWide, backgroundWideFile);

            File metaThemeFile = new File(themeFolder, "theme.xml");
            String xmlThemeContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<theme version=\"1\">\n<description>HBC theme by " + name + "</description>\n<font_color>\n<red>12</red>\n<green>56</green>\n<blue>105</blue>\n<alpha>255</alpha>\n</font_color>\n<progress_gradient>\n<upper_left>\n<red>131</red>\n<green>194</green>\n<blue>214</blue>\n<alpha>229</alpha>\n</upper_left>\n<upper_right>\n<red>131</red>\n<green>194</green>\n<blue>214</blue>\n<alpha>229</alpha>\n</upper_right>\n<lower_right>\n<red>12</red>\n<green>56</green>\n<blue>105</blue>\n<alpha>229</alpha>\n</lower_right>\n<lower_left>\n<red>12</red>\n<green>56</green>\n<blue>105</blue>\n<alpha>229</alpha>\n</lower_left>\n</progress_gradient>\n</theme>";
            ImageUtils.writeStringToFile(xmlThemeContent, metaThemeFile);


            File metaFile = new File(appFolder, "meta.xml");
            String date = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
            String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<app version=\"1.0\">\n<name>" + name + " HBC Theme</name>\n<coder>Eilon007</coder>\n<version>1.0</version>\n<release_date>"
                    + date + "</release_date>\n<short_description>OSCDL generated Theme</short_description>\n<long_description>" + name + " HBC Theme</long_description>\n</app>";
            ImageUtils.writeStringToFile(xmlContent, metaFile);

            // Compress the theme folder to a zip file
            File themeZip = new File(appFolder, "theme.zip");
            ImageUtils.compressFolder(themeFolder, themeZip);
            // Delete the theme folder after compression
            deleteRecursive(themeFolder);

            // Compress the parentFolder into the final zip
            String finalCompressedFolderPath = getFilesDir() + "/" + name + ".zip";
            File finalCompressedFolder = new File(finalCompressedFolderPath);
            ImageUtils.compressFolder(parentFolder, finalCompressedFolder);

            // Save to the database
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            ThemeItem theme = new ThemeItem(finalCompressedFolderPath, name, resizedIcon);
            dbHelper.addTheme(theme);

            // Delete the original parentFolder after everything is done
            deleteRecursive(parentFolder);

            endActivity();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addtoTheme(File themeFolder)
    {
        AssetManager assetManager = getAssets();

        try {
            // List all files and directories in the "images" folder
            String[] files = assetManager.list("themeDef");

            for (String filename : files) {
                InputStream inputStream = assetManager.open("themeDef/" + filename);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                File themeImageFile = new File(themeFolder, filename);
                ImageUtils.saveBitmap(bitmap, themeImageFile);


                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }
}