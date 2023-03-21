package com.example.oscdl;

import android.content.Context;
import android.graphics.Bitmap;
import android.print.PrintDocumentAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomBaseAdapter extends BaseAdapter
{

    Context context;
    Bitmap[] imgs;
    String[] desc;
    ArrayList<Package> pkgs;
    String[] appName;
    LayoutInflater inflater;

    public CustomBaseAdapter(Context context, Bitmap[] kimgs, String[] knames, String[] kdescs, ArrayList<Package> kpkgs)
    {
        this.context = context;
        imgs = kimgs;
        desc = kdescs;
        appName = knames;
        pkgs = kpkgs;
        inflater = LayoutInflater.from(context);

    }


    @Override
    public int getCount() {
        return imgs.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.app_item, null);
        TextView name  = (TextView) convertView.findViewById(R.id.appname);
        TextView descr  = (TextView) convertView.findViewById(R.id.desc);

        ImageView img = (ImageView) convertView.findViewById(R.id.img);
        img.setImageBitmap(imgs[position]);


        name.setText(appName[position]);
        descr.setText(desc[position]);


        return convertView;
    }

}
