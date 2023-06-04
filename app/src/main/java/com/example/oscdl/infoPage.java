package com.example.oscdl;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class infoPage extends AppCompatActivity {


    SmallPackage smallPackage = new SmallPackage();
    boolean key = true;
    int downloadProgress = 0;
    Button btn;
    private BroadcastReceiver networkChangeReceiver;
    String ip = "";
    Context context = this;
    String pathfile = "";

    DownloadFileFromURL downloadTask;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_page);

        Intent intent = getIntent();

        byte[] byteArray = intent.getByteArrayExtra("img");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        ImageView exit = findViewById(R.id.backArrow);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endActivity();
            }
        });


        smallPackage = new SmallPackage(intent.getStringExtra("appname"), intent.getStringExtra("version"), intent.getStringExtra("devname"), intent.getStringExtra("category"), intent.getStringExtra("description"), bitmap, intent.getIntExtra("size", 0), intent.getStringExtra("zip_url"));

        TextView appname = findViewById(R.id.appname);
        appname.setText("App Name: " + smallPackage.getAppname());

        TextView version = findViewById(R.id.version);
        version.setText("Version: " + smallPackage.getVersion());

        TextView category = findViewById(R.id.category);
        category.setText("Category: " + smallPackage.getCategory());

        TextView description = findViewById(R.id.description);
        description.setText("Description: " + smallPackage.getDesc());

        TextView devname = findViewById(R.id.devname);
        devname.setText("Coder: " + smallPackage.getDevname());

        TextView size = findViewById(R.id.size);
        size.setText("Size: " + bytesToMegabytes(smallPackage.getSize()) + "MB");
        pathfile = smallPackage.zip_url;

        ImageView iv = findViewById(R.id.img);
        iv.setImageBitmap(smallPackage.getImg());

        btn = findViewById(R.id.download);

        btn.setOnClickListener(v ->
        {
            startDialog();
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadFileFromURL.DOWNLOAD_COMPLETE_ACTION);
        registerReceiver(downloadCompleteReceiver, intentFilter);

        networkChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isNetworkAvailable()) {
                    btn.setEnabled(true);
                } else {
                    btn.setEnabled(false);
                }
            }
        };
        IntentFilter networkIntentFilter = new IntentFilter();
        networkIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, networkIntentFilter);

        // Set initial button state
        if (isNetworkAvailable()) {
            btn.setEnabled(true);
        } else {
            btn.setEnabled(false);
        }

    }

    private String bytesToMegabytes(long bytes)
    {
        double megabytes = (bytes / 1024f) / 1024f;
        return String.format("%.2f", megabytes);
    }


    private void startFileDownload()
    {
        if (isNetworkAvailable())
        {
            //String fileURL = smallPackage.getZip_url();
            downloadTask = new DownloadFileFromURL(this, 1);
            downloadTask.execute(smallPackage.zip_url);
        }
        else
        {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
        }

    }


    BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String pathToFile = intent.getStringExtra("pathToFile");
            if (pathToFile != null) {
                //Toast.makeText(context, pathToFile, Toast.LENGTH_LONG).show();
                transferToWii(pathToFile);
            }
        }
    };

    protected void onDestroy() {
        super.onDestroy();

        // unregister the downloadCompleteReceiver
        unregisterReceiver(downloadCompleteReceiver);
        unregisterReceiver(networkChangeReceiver);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }


    public ByteArrayInputStream writeZip(String filePath)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayInputStream byteArrayInputStream = null;
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            byte[] content = outputStream.toByteArray();
            byteArrayInputStream = new ByteArrayInputStream(content);
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayInputStream;
    }


    public void startDialog()
    {
        CustomDialog customDialog = new CustomDialog(this, new CustomDialog.CustomDialogListener() {
            @Override
            public void onOkClicked(String ip) {
                if(isIPv4Address(ip))
                {
                    startFileDownload();
                }
                else
                {
                    Toast.makeText(context, "Please enter a valid IP address", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelClicked() {

            }
        });
        customDialog.show();
    }

    public boolean isIPv4Address(String input) {
        // Split the input string by dot (.) to get the individual address parts
        String[] parts = input.split("\\.");

        // IPv4 address should have exactly 4 parts
        if (parts.length != 4) {
            return false;
        }

        // Validate each part of the address
        for (String part : parts) {
            try {
                int value = Integer.parseInt(part);
                // Each part should be within the range 0-255
                if (value < 0 || value > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                // If any part is not a valid integer, it is not a valid IPv4 address
                return false;
            }
        }

        this.ip = input;

        return true;
    }

    public void transferToWii(String filepath) {
        ByteArrayInputStream zip_buf = writeZip(filepath);

        WiiLoad wii = new WiiLoad(context);

        Thread trd = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    // Create a new ByteArrayOutputStream
                    ByteArrayOutputStream organizedZipBuf = new ByteArrayOutputStream();

                    // Organize the ZIP file
                    wii.organizeZip(zip_buf, organizedZipBuf);

                    // Create a new ByteArrayInputStream for preparing the data
                    ByteArrayInputStream preparedZipBuf = new ByteArrayInputStream(organizedZipBuf.toByteArray());

                    // Prepare the data for sending
                    wii.prepare(preparedZipBuf);

                    Socket conn = wii.connect(ip); // Connect and obtain the socket

                    if (conn != null) {
                        wii.handshake();
                        wii.send(smallPackage.appname);
                    } else {
                        Log.e("WiiLoad", "Failed to establish connection with the Wii.");
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    downloadTask.deleteDownloadedFile();
                    // No need to close the socket here since it is closed in the 'send' method
                }
            }
        });

        trd.start();
    }

    public void endActivity() {
        this.finish();
    }





}