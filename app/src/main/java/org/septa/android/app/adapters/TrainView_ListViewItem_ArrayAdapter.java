/*
 * TrainView_ListViewItem_ArrayAdapter.java
 * Last modified on 04-18-2014 10:15-0400 by brianhmayo
 *
 * Copyright (c) 2014 SEPTA.  All rights reserved.
 */

package org.septa.android.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.septa.android.app.R;
import org.septa.android.app.models.servicemodels.TrainViewModel;

import java.util.List;

public class TrainView_ListViewItem_ArrayAdapter extends ArrayAdapter<TrainViewModel> {
    public static final String TAG = TrainView_ListViewItem_ArrayAdapter.class.getName();

    private final Context context;
    private final List<TrainViewModel> values;

    public TrainView_ListViewItem_ArrayAdapter(Context context, List<TrainViewModel> values) {
        super(context, R.layout.trainview_listview_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = null;

        TrainViewModel trainInformation = values.get(position);

        rowView = inflater.inflate(R.layout.trainview_listview_item, parent, false);

        TextView trainNumberTextView = (TextView) rowView.findViewById(R.id.trainview_listview_item_train_number_textview);
        TextView trainTimingTextView = (TextView) rowView.findViewById(R.id.trainview_listview_item_train_timing_textview);

        trainNumberTextView.setText(trainInformation.getTrainNumber());

        if (trainInformation.isLate()) {
            if (trainInformation.getLate() > 1) {
                trainTimingTextView.setText(""+trainInformation.getLate()+" mins");
            } else {
                trainTimingTextView.setText(""+trainInformation.getLate()+" min");
            }
            trainTimingTextView.setTextColor(context.getResources().getColor(R.color.text_late));
        } else {
            trainTimingTextView.setText("On-time");
            trainTimingTextView.setTextColor(context.getResources().getColor(R.color.text_on_time));
        }

        TextView trainStart = (TextView) rowView.findViewById(R.id.trainview_listview_item_start_textview);
        trainStart.setText(trainInformation.getSource());

        TextView trainEnd = (TextView) rowView.findViewById(R.id.trainview_listview_item_end_textview);
        trainEnd.setText(trainInformation.getDestination());

        return rowView;
    }

    @Override
    public boolean areAllItemsEnabled() {

        return true;
    }

    @Override
    public boolean isEnabled(int position) {

        return false;
    }
}
