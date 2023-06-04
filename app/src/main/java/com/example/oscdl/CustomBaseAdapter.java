package com.example.oscdl;

import android.content.Context;
import android.graphics.Bitmap;
import android.print.PrintDocumentAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Locale;

public class CustomBaseAdapter extends BaseAdapter
{
    Context context;
    Bitmap[] imgs;
    String[] desc;
    ArrayList<Package> pkgs;
    String[] appName;
    LayoutInflater inflater;
    String filter;

    public CustomBaseAdapter(Context context, Bitmap[] kimgs, ArrayList<Package> kpkgs)
    {
        this.context = context;
        filter = "";
        pkgs = kpkgs;
        imgs = kimgs;
        inflater = LayoutInflater.from(context);


        int count = 0;
        desc = new String[pkgs.size()];
        appName = new String[pkgs.size()];

        for (Package pkg : pkgs) {
            desc[count] = pkg.getShort_description();
            appName[count] = pkg.getDisplay_name();
            count = count + 1;
        }




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


        if (filter.isEmpty() == true)
        {
            return convertView;
        }
        else
        {
          if (pkgs.get(position).getDisplay_name().toLowerCase(Locale.ROOT).startsWith(filter.toLowerCase(Locale.ROOT)) == true)
          {
              return convertView;
          }
          else
          {
              View v = new View(context);
              return v;
          }
        }


        //return convertView;
    }

    public void setFilter(String s)
    {
        filter = s;
    }


}
