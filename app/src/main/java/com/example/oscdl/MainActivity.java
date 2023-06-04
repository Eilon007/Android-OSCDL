package com.example.oscdl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Carousel;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.example.oscdl.databinding.ActivityMainBinding;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.jackandphantom.carouselrecyclerview.CarouselRecyclerview;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
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

import me.ibrahimsn.lib.OnItemSelectedListener;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    oscfragment oscfragment;
    createfragment createfragment;
    hbbfragment hbbfragment;

    FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        oscfragment = new oscfragment();
        createfragment = new createfragment();
        hbbfragment = new hbbfragment();

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.frlayout, oscfragment)
                .add(R.id.frlayout, createfragment)
                .add(R.id.frlayout, hbbfragment)
                .hide(createfragment)
                .hide(hbbfragment)
                .commit();

        binding.bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public boolean onItemSelect(int i) {
                switch (i){

                    case 0:
                        showFragment(oscfragment);
                        break;
                    case 1:
                        showFragment(createfragment);
                        break;
                    case 2:
                        showFragment(hbbfragment);
                        break;

                }

                return true;
            }

        });





    }


    private void showFragment(Fragment fragment)
    {
        fragmentManager.beginTransaction()
                .hide(oscfragment)
                .hide(createfragment)
                .hide(hbbfragment)
                .show(fragment)
                .commit();

    }
}


