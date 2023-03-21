package com.example.oscdl;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class MainActivity extends AppCompatActivity {

    String jsonStr = "";
    boolean key = false;
    ArrayList<Package> pkgs = new ArrayList<>();
    Bitmap[] imgs;
    String[] desc;
    String[] appName;
    ListView lv;
    CustomBaseAdapter cba;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //here because i need it for the splash screen later on
        pkgs = getJSON();
        imgs = new Bitmap[pkgs.size()];
        desc = new String[pkgs.size()];
        appName = new String[pkgs.size()];

        setArr();

        setContentView(R.layout.activity_main);




        lv = (ListView) findViewById(R.id.sus);

        cba = new CustomBaseAdapter(this, imgs, appName, desc, pkgs);

        lv.setAdapter(cba);

        downloadIMGS();

    }

    public ArrayList<Package> getJSON()
    {
        ArrayList<Package> packages = new ArrayList<Package>();


        //retrieve JSON file from API

        Thread trd = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    URL url = new URL("https://api.oscwii.org/v2/primary/packages");

                    URLConnection connection = url.openConnection();
                    connection.connect();

                    StringBuilder stringBuilder = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    jsonStr = stringBuilder.toString();

                    key = true;

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        trd.start();


        while (!key) // Check when to start the ui thread / when the JSON is fetched
        {

        }

        Log.d("Key", "Got Key");


        ObjectMapper mapper = new ObjectMapper();
        JsonNode apps = JsonNodeFactory.instance.arrayNode();
        int i = 0;

        try
        {
            apps = mapper.readTree(jsonStr);
            for (JsonNode app : apps)
            {
                String str = app.toString();
                packages.add(mapper.readValue(str, Package.class));
            }
        }

        catch (JsonProcessingException e)
        {
            e.printStackTrace();
            Log.d("Err", "Error reading!");
        }

        Log.d("Reader", "Done!");

        return packages;
    }

    public void setArr() // sets the image, desc and name arrays
    {

        int count = 0;

        for (Package pkg : pkgs)
        {
            desc[count] = pkg.getShort_description();
            appName[count] = pkg.getDisplay_name();
            imgs[count] = null;
            count = count + 1;
        }


        key = false;

        Thread trd = new Thread(new Runnable() {
            @Override
            public void run()
            {

                for (int count = 0; count < 5; count++)
                {
                    Package pkg = pkgs.get(count);

                    //get bitmaps

                    URL url = null;
                    try {
                        url = new URL(pkg.getIcon_url());
                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        imgs[count] = bmp;
                        Log.d("IMGS", "downloded image");

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                key = true;
            }
        });

        trd.start();

        while(key != true)
        {

        }

    }

    public void downloadIMGS()
    {
        Handler handler = new Handler();

        Thread trd = new Thread(new Runnable() {
            @Override
            public void run()
            {
                for (int count = 5; count < pkgs.size(); count++)
                {
                    Package pkg = pkgs.get(count);

                    //get bitmaps

                    URL url = null;
                    try {
                        url = new URL(pkg.getIcon_url());
                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        imgs[count] = bmp;
                        Log.d("IMGS", "downloded image");

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    handler.post(new Runnable() {
                        @Override
                        public void run()
                        {
                            cba.notifyDataSetChanged();
                        }
                    });

                }
            }
        });

        trd.start();
    }
}


