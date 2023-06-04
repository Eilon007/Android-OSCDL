package com.example.oscdl;

import android.content.Context;
import android.util.Log;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.apache.commons.compress.archivers.zip.*;
import org.apache.commons.compress.utils.IOUtils;

public class WiiLoad {
    public static final int WIILOAD_VER_MAJOR = 0;
    public static final int WIILOAD_VER_MINOR = 5;
    public static final int CHUNK_SIZE = 1024;

    public int fileSize;
    public byte[] cData;
    public int compressedSize;
    public byte[][] chunks;
    public Socket conn = new Socket();
    Context context;

    private static final String TAG = "WiiLoad";


    public WiiLoad(Context c)
    {
        context = c;
    }

    public void organizeZip(ByteArrayInputStream zipped_app, ByteArrayOutputStream zip_buf) throws IOException {
        // First pass to get the app name
        String appname = "";
        String dirname = "";

        ZipArchiveInputStream originalZip = new ZipArchiveInputStream(zipped_app);
        ZipArchiveEntry entry;
        while ((entry = originalZip.getNextZipEntry()) != null) {
            String name = entry.getName();
            if (name.startsWith("apps/") && !name.equals("apps/")) {
                appname = name.split("/")[1];
                dirname = appname + "/";
                break;
            }
        }
        originalZip.close();

        // Reset input stream
        zipped_app.reset();

        // Second pass to create the new zip
        ZipArchiveOutputStream organizedZip = new ZipArchiveOutputStream(zip_buf);
        originalZip = new ZipArchiveInputStream(zipped_app);
        while ((entry = originalZip.getNextZipEntry()) != null) {
            String name = entry.getName();
            String new_name;

            if (name.startsWith("apps/")) {
                new_name = name.replace("apps/", "");
            } else {
                new_name = dirname + "../../" + name;
                if ((dirname + "../../read").equalsIgnoreCase(name.split("\\.")[0])) {
                    new_name = new_name.replace(name.split("\\.")[0], name.split("\\.")[0] + '_' + appname);
                }
            }

            // Create a new entry
            ZipArchiveEntry new_entry = new ZipArchiveEntry(new_name);
            organizedZip.putArchiveEntry(new_entry);

            // Write the entry data
            if (!entry.isDirectory()) {
                IOUtils.copy(originalZip, organizedZip);
            }
            organizedZip.closeArchiveEntry();

            // Add content to 0-byte files
            if (!entry.isDirectory() && entry.getSize() == 0) {
                ZipArchiveEntry zeroSizeFileEntry = new ZipArchiveEntry(new_name);
                organizedZip.putArchiveEntry(zeroSizeFileEntry);
                organizedZip.write(".".getBytes(StandardCharsets.UTF_8));
                organizedZip.closeArchiveEntry();
            }
        }
        originalZip.close();
        organizedZip.finish();
        organizedZip.close();
    }




    public void prepare(ByteArrayInputStream zipBuf) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        try {
            int bytesRead;
            while ((bytesRead = zipBuf.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] byteArray = outputStream.toByteArray();

        fileSize = byteArray.length;
        cData = byteArray;
        compressedSize = cData.length;
        chunks = splitDataIntoChunks(cData);
    }




    public byte[][] splitDataIntoChunks(byte[] data) {
        int totalChunks = (int) Math.ceil((double) data.length / CHUNK_SIZE);
        byte[][] chunks = new byte[totalChunks][];

        for (int i = 0; i < totalChunks; i++) {
            int start = i * CHUNK_SIZE;
            int end = Math.min(start + CHUNK_SIZE, data.length);
            chunks[i] = Arrays.copyOfRange(data, start, end);
        }

        return chunks;
    }

    public Socket connect(String ip) {
        conn = new Socket();

        try
        {
            conn.connect(new InetSocketAddress(ip, 4299), 2000);

            return conn;
        }

        catch (IOException e)
        {
            Log.e(TAG + " -Connect", "Error connecting to Wii: " + e.getMessage());
            return null; // Return null if connection fails
        }
    }

    public void handshake() {
        try {
            OutputStream outputStream = conn.getOutputStream();
            Thread.sleep(1);
            outputStream.write("HAXX".getBytes());
            Thread.sleep(1);
            outputStream.write(WIILOAD_VER_MAJOR); // WIILOAD_VER_MAJOR
            Thread.sleep(1);
            outputStream.write(WIILOAD_VER_MINOR); // WIILOAD_VER_MINOR
            Thread.sleep(1);
            outputStream.write(ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN).array());  // big endian short
            Thread.sleep(1);
            writeBigEndianInt(outputStream, compressedSize);
            Thread.sleep(1);
            writeBigEndianInt(outputStream, fileSize);
            Thread.sleep(1);
            Log.d(TAG + " -Handshake", "Done!");
            outputStream.flush();

        } catch (IOException e) {
            Log.e(TAG + " -Handshake", "Error during handshake: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void writeBigEndianInt(OutputStream outputStream, int value) throws IOException {
        outputStream.write((value >> 24) & 0xFF);
        outputStream.write((value >> 16) & 0xFF);
        outputStream.write((value >> 8) & 0xFF);
        outputStream.write(value & 0xFF);
    }

    public void send(String appName)
    {


        try {
            OutputStream outputStream = conn.getOutputStream();

            for (byte[] chunk : chunks) {
                outputStream = conn.getOutputStream();
                outputStream.write(chunk);
                Thread.sleep(1);
                outputStream.flush();
            }


            String fileName = appName + ".zip";

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            byteStream.write(fileName.getBytes("UTF-8"));
            byteStream.write(0x00);
            byte[] combinedBytes = byteStream.toByteArray();
            outputStream.write(combinedBytes);
            outputStream.write(0);
            outputStream.flush();



        } catch (SocketException e) {
            Log.e(TAG + " -Send", "Broken pipe: Connection closed by the receiving end. " + e.getMessage());

        } catch (IOException e) {
            Log.e(TAG + " -Send", "Error sending data: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally
        {
            try {
                conn.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing socket: " + e.getMessage());
            }
        }
    }
}

