package com.example.oscdl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselHolderView>
{

    Context context;
    Bitmap[] imgs;
    String[] version;
    Integer[] size;
    String[] devName;
    ArrayList<Package> pkgs;
    LayoutInflater inflater;
    String filter;

    int lastpos = -1;

    public CarouselAdapter(Context context, Bitmap[] imgs, ArrayList<Package> pkgs) {
        this.context = context;
        this.imgs = imgs;
        this.pkgs = pkgs;
        this.inflater = LayoutInflater.from(context);
        filter = "";

        int count = 0;
        version = new String[pkgs.size()];
        devName = new String[pkgs.size()];
        size = new Integer[pkgs.size()];


        for (Package pkg : pkgs) {
            version[count] = pkg.getVersion();
            devName[count] = pkg.getCoder();
            size[count] = pkg.getExtracted();

            count = count + 1;
        }
    }

    public void setFilter(String s)
    {
        filter = s;
    }


    @NonNull
    @Override
    public CarouselHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = inflater.inflate(R.layout.carousel_item, parent, false);
        return new CarouselHolderView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselAdapter.CarouselHolderView holder, int position)
    {
        holder.devName_tv.setText("Dev Name: " + devName[position]);
        holder.version_tv.setText("Version: " + version[position]);
        holder.size_tv.setText("Size: " + bytesToMegabytes(size[position]) + "MB");




        Glide.with(holder.img_iv).load(imgs[position]).into(holder.img_iv);



        holder.img_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    lastpos = -1;

                    Intent intent = new Intent(context, infoPage.class);
                    intent.putExtra("appname", pkgs.get(position).getDisplay_name());
                    intent.putExtra("version", pkgs.get(position).getVersion());
                    intent.putExtra("category", pkgs.get(position).getCategory());
                    intent.putExtra("devname", pkgs.get(position).getCoder());
                    intent.putExtra("description", pkgs.get(position).getLong_description());

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    imgs[position].compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    intent.putExtra("img", byteArray);
                    intent.putExtra("size", pkgs.get(position).getZip_size());

                    context.startActivity(intent);

            }
        });

        if (filter.isEmpty() == true)
        {
            holder.showView();
        }
        else
        {
            if (pkgs.get(position).getDisplay_name().toLowerCase(Locale.ROOT).startsWith(filter.toLowerCase(Locale.ROOT)) == true)
            {
                holder.showView();
            }
            else
            {
                holder.hideView();
            }
        }


    }

    @Override
    public int getItemCount()
    {
        return pkgs.size();
    }

    private static String bytesToMegabytes(long bytes)
    {
        double megabytes = (bytes / 1024f) / 1024f;
        return String.format("%.2f", megabytes);
    }

    public void setGone()
    {
        View view = inflater.inflate(R.layout.app_item, null);

        view.setVisibility(View.GONE);
    }

    public void setVisible()
    {
        View view = inflater.inflate(R.layout.app_item, null);

        view.setVisibility(View.VISIBLE);
    }

    public int dpToPx(Context context, float dp)
    {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    class CarouselHolderView extends RecyclerView.ViewHolder
    {
        private TextView devName_tv;
        private TextView version_tv;
        private TextView size_tv;
        private ImageView img_iv;
        View view;
        View temp;


        public CarouselHolderView(final View view) {
            super(view);
            this.view = view;
            temp = view;
            devName_tv = view.findViewById(R.id.devname);
            version_tv = view.findViewById(R.id.version);
            size_tv = view.findViewById(R.id.size);
            img_iv = view.findViewById(R.id.img_carousel);

        }

        public void hideView()
        {
            //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, dpToPx(context, 150));
            //view.setLayoutParams(params);
            view.setVisibility(View.GONE);
        }

        public void showView()
        {
            view.setVisibility(View.VISIBLE);
        }


    }

}
