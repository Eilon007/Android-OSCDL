package com.example.oscdl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.oscdl.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.jackandphantom.carouselrecyclerview.CarouselRecyclerview;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link oscfragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class oscfragment extends Fragment {



    String jsonStr = "";
    boolean key = false;
    ArrayList<Package> pkgs = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    Bitmap[] imgs;

    ListView lv;
    CustomBaseAdapter cba;
    CarouselAdapter ca;

    int lastpos = -1;
    Context context;
    boolean isInitialized = false;
    private EditText et;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public oscfragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment createfragment.
     */
    // TODO: Rename and change types and number of parameters
    public static oscfragment newInstance(String param1, String param2) {
        oscfragment fragment = new oscfragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        context = getContext();;



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_oscfragment, container, false);

    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!isInitialized) {

            isInitialized = true;

            pkgs = getJSON();
            imgs = new Bitmap[pkgs.size()];


            setArr();

            for (int i = 0; i < pkgs.size(); i++)
            {
                names.add(pkgs.get(i).getDisplay_name());
            }


            ArrayList<String> filteredNames = new ArrayList<>(names);

            CarouselRecyclerview carouselRecyclerview = view.findViewById(R.id.carouselRecyclerview);
            ca = new CarouselAdapter(context, imgs, pkgs);
            carouselRecyclerview.setAdapter(ca);
            carouselRecyclerview.setAlpha(true);
            carouselRecyclerview.setInfinite(false);

            lv = (ListView) view.findViewById(R.id.sus);
            lv.setDivider(null);
            cba = new CustomBaseAdapter(context, imgs, pkgs);


            lv.setAdapter(cba);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (lastpos == -1) {
                        lastpos = position;
                    } else if (lastpos == position) {
                        lastpos = -1;

                        Intent intent = new Intent(context, infoPage.class);
                        intent.putExtra("appname", pkgs.get(position).getDisplay_name());
                        intent.putExtra("version", pkgs.get(position).getVersion());
                        intent.putExtra("category", pkgs.get(position).getCategory());
                        intent.putExtra("devname", pkgs.get(position).getCoder());
                        intent.putExtra("description", pkgs.get(position).getLong_description());
                        intent.putExtra("zip_url", pkgs.get(position).getZip_url());

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        imgs[position].compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();

                        intent.putExtra("img", byteArray);
                        intent.putExtra("size", pkgs.get(position).getZip_size());

                        startActivity(intent);
                    }


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            carouselRecyclerview.scrollToPosition(position);
                        }
                    }, 200);

                }
            });

            et = (EditText) view.findViewById(R.id.search_bar);
            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    String str = s.toString();

                    cba.setFilter(str);
                    cba.notifyDataSetChanged();

                    ca.setFilter(str);
                    ca.notifyDataSetChanged();



                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            downloadIMGS();

        }
    }





    public ArrayList<Package> getJSON() {
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

        try {
            apps = mapper.readTree(jsonStr);
            for (JsonNode app : apps) {
                String str = app.toString();
                packages.add(mapper.readValue(str, Package.class));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            Log.d("Reader", "Error reading!");
        }

        Log.d("Reader", "Done!");

        return packages;
    }

    public void setArr() // sets the image, desc and name arrays
    {

        int count = 0;

        for (Package pkg : pkgs) {
            imgs[count] = null;
            count = count + 1;
        }


        key = false;

        Thread trd = new Thread(new Runnable() {
            @Override
            public void run() {

                for (int count = 0; count < 5; count++) {
                    Package pkg = pkgs.get(count);

                    //get bitmaps

                    URL url = null;
                    try {
                        url = new URL(pkg.getIcon_url());
                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        imgs[count] = bmp;
                        Log.d("IMGS", "Download first 5!");

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

        while (key != true) {

        }

    }

    public void downloadIMGS() {
        Handler handler = new Handler();

        Thread trd = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int count = 5; count < pkgs.size(); count++) {
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
                        public void run() {
                            cba.notifyDataSetChanged();
                            ca.notifyDataSetChanged();
                        }
                    });

                }
            }
        });

        trd.start();
    }
}