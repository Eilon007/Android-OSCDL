package com.example.oscdl;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import androidx.core.app.NotificationCompat;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadFileFromURL extends AsyncTask<String, Integer, String> {
    private static final String CHANNEL_ID = "file_download_channel";
    private Context context;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private int notificationId;
    public String pathToFile;
    public static final String DOWNLOAD_COMPLETE_ACTION = "com.example.oscdl.DOWNLOAD_COMPLETE";

    private Handler progressUpdateHandler;
    private Runnable progressUpdateRunnable;
    private int currentProgress;

    public DownloadFileFromURL(Context context, int notificationId) {
        this.context = context;
        this.notificationId = notificationId;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        progressUpdateHandler = new Handler(); // Handler and Runnable for updating the notification bar
        progressUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                updateNotification(currentProgress);
                progressUpdateHandler.postDelayed(this, 200);
            }
        };
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        createNotificationChannel();
        setupNotification();
        progressUpdateHandler.postDelayed(progressUpdateRunnable, 200);
    }

    @Override
    protected String doInBackground(String... urlString) {
        pathToFile = null;

        try {
            // Establish connection to the URL
            URL url = new URL(urlString[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // Get the file length
            int fileLength = connection.getContentLength();
            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            String fileName = Uri.parse(urlString[0]).getLastPathSegment();

            // Set the path to the app's internal storage
            pathToFile = context.getFilesDir().getAbsolutePath() + File.separator + fileName;
            FileOutputStream fileOutputStream = new FileOutputStream(pathToFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            long total = 0;
            int progress;

            // Read and write the file in chunks
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                total += bytesRead;
                progress = (int) (total * 100 / fileLength);
                currentProgress = progress;
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            // Read and write the file in chunks
            fileOutputStream.flush();
            fileOutputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pathToFile;
    }

    @Override
    protected void onPostExecute(String pathToFile) {
        super.onPostExecute(pathToFile);
        progressUpdateHandler.removeCallbacks(progressUpdateRunnable);

        // send a broadcast message indicating that the file download is complete
        Intent intent = new Intent();
        intent.setAction(DOWNLOAD_COMPLETE_ACTION);
        intent.putExtra("pathToFile", pathToFile);
        context.sendBroadcast(intent);

        completeNotification(pathToFile);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "File Download";
            String description = "File download progress and completion";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setupNotification() {
        notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Downloading File")
                .setContentText("Download in progress")
                .setProgress(100, 0, false)
                .setOngoing(true);

        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    private void updateNotification(int progress) {
        notificationBuilder.setProgress(100, progress, false);
        notificationBuilder.setContentText("Download in progress: " + progress + "%");
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    private boolean completeNotification(String pathToFile) {
        if (pathToFile == null || pathToFile.isEmpty()) {
            return false; // or handle the case appropriately
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(pathToFile), "resource/folder");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        notificationBuilder.setContentTitle("Download Complete")
                .setContentText("File downloaded to " + pathToFile)
                .setProgress(0, 0, false)
                .setOngoing(false)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(notificationId, notificationBuilder.build());
        notificationManager.cancel(notificationId);

        return true;
    }

    public void deleteDownloadedFile() {
        try {
            File file = new File(pathToFile);
            if (file.exists()) {
                file.delete();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}


