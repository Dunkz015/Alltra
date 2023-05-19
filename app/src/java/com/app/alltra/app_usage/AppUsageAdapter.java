package com.app.alltra.app_usage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.alltra.R;

import java.util.Collections;
import java.util.List;

public class AppUsageAdapter extends ArrayAdapter<AppItem> {

    private final List<AppItem> appItemList;
    private final String suffix;

    public AppUsageAdapter(Context context, List<AppItem> appItemList, String suffix) {
        super(context, 0, appItemList);
        this.appItemList = appItemList;
        this.suffix = suffix;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_apps_elements, parent, false);
        }

        AppItem appItem = appItemList.get(position);

        ImageView iconImageView = convertView.findViewById(R.id.icon);
        TextView nameTextView = convertView.findViewById(R.id.name);
        ProgressBar progressBar = convertView.findViewById(R.id.progress_bar);
        TextView valueTextView = convertView.findViewById(R.id.value);

        iconImageView.setImageDrawable(appItem.getIconResource());
        nameTextView.setText(appItem.getName());
        progressBar.setProgress(appItem.getProgress());
        valueTextView.setText(appItem.getValue() + suffix);

        return convertView;
    }

    public void sortByValueDescending() {
        Collections.sort(appItemList, (item1, item2) -> Double.compare(item2.getValue(), item1.getValue()));
        notifyDataSetChanged();
    }
}

