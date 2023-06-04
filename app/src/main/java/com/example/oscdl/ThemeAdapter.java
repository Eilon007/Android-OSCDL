package com.example.oscdl;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ThemeAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> zipFilePaths;
    private ArrayList<String> names;
    private ArrayList<Bitmap> icons;

    public ThemeAdapter(Context context, ArrayList<String> zipFilePaths, ArrayList<String> names, ArrayList<Bitmap> icons) {
        this.context = context;
        this.zipFilePaths = zipFilePaths;
        this.names = names;
        this.icons = icons;
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Object getItem(int position) {
        return null; // Not used in this example
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_theme_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.iconImageView = convertView.findViewById(R.id.themeIcon);
            viewHolder.nameTextView = convertView.findViewById(R.id.themeName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Set the data for the current item
        viewHolder.iconImageView.setImageBitmap(icons.get(position));
        viewHolder.nameTextView.setText(names.get(position));

        ImageView deleteItem = convertView.findViewById(R.id.deleteItem);

        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Removing item from list
                DatabaseHelper db = new DatabaseHelper(context);
                db.deleteTheme(names.get(position));

                updateListViewData();

                // Notifying the adapter
                notifyDataSetChanged();
            }
        });


        return convertView;
    }

    private void updateListViewData() {

        DatabaseHelper dbHelper = new DatabaseHelper(context);

        ArrayList<ThemeItem> items = dbHelper.getThemeItems();

        zipFilePaths = new ArrayList<String>();
        names = new ArrayList<String>();
        icons = new ArrayList<Bitmap>();

        for (ThemeItem item : items)
        {
            zipFilePaths.add(item.getZipFilePath());
            names.add(item.getName());
            icons.add(item.getIcon());
        }

    }

    static class ViewHolder {
        ImageView iconImageView;
        TextView nameTextView;
    }
}