package com.example.oscdl;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.oscdl.R;
import com.jackandphantom.carouselrecyclerview.CarouselRecyclerview;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link createfragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class createfragment extends Fragment implements CustomDialog.CustomDialogListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String ip = "";
    int position;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public createfragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment hbbfragment.
     */
    // TODO: Rename and change types and number of parameters
    public static createfragment newInstance(String param1, String param2) {
        createfragment fragment = new createfragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_createfragment, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = this.requireContext();

        ListView lv = view.findViewById(R.id.themeList);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                startDialog(position);

            }
        });



        updateListViewData();


        Button btn = view.findViewById(R.id.btnCreate);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, themeCreator.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        // Call updateListViewData() whenever the fragment becomes visible again
        updateListViewData();
    }

    private void updateListViewData() {
        // Retrieve themes from the database
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        ArrayList<ThemeItem> items = dbHelper.getThemeItems();
        ArrayList<String> zipFilePaths = new ArrayList<String>();
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<Bitmap> icons = new ArrayList<Bitmap>();

        for (ThemeItem item : items)
        {
            zipFilePaths.add(item.getZipFilePath());
            names.add(item.getName());
            icons.add(item.getIcon());
        }

        // Create a custom adapter
        ThemeAdapter themeAdapter = new ThemeAdapter(requireContext(), zipFilePaths, names, icons);

        ListView lv = requireView().findViewById(R.id.themeList);
        lv.setAdapter(themeAdapter);
    }

    public void transferToWii(String filepath, int pos) {
        ByteArrayInputStream zip_buf = writeZip(filepath);

        WiiLoad wii = new WiiLoad(requireContext());

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

                        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
                        ArrayList<String> names = dbHelper.getNames();

                        wii.handshake();
                        wii.send(names.get(pos));
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
                    // No need to close the socket here since it is closed in the 'send' method
                }
            }
        });

        trd.start();
    }


    public void startDialog(int position)
    {
        this.position = position;
        CustomDialog customDialog = new CustomDialog(requireActivity(), this);
        customDialog.show();
    }

    @Override
    public void onOkClicked(String ip) {
        if(isIPv4Address(ip))
        {
            this.ip = ip;

            DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
            ArrayList<String> zipFilePaths = dbHelper.getZipFilePaths();
            transferToWii(zipFilePaths.get(position), position);
        }
        else
        {
            Toast.makeText(requireContext(), "Please enter a valid IP address", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCancelClicked() {
        // Do something when cancel is clicked if you want
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

        return true;
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
}