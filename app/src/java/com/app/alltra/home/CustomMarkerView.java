package com.app.alltra.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.app.alltra.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

@SuppressLint("ViewConstructor")
public class CustomMarkerView extends MarkerView {

    private final TextView tvContent;

    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        // find your layout components
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
// content (user-interface)
    @SuppressLint("SetTextI18n")
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        tvContent.setText((int) e.getY() + "MB");

        // this will perform necessary layouting
        super.refreshContent(e, highlight);
    }

    private MPPointF mOffset;

    @Override
    public MPPointF getOffset() {

        if(mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }

        return mOffset;
    }}