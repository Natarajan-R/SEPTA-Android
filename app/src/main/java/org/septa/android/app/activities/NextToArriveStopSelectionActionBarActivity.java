/*
 * NextToArriveStopSelectionActionBarActivity.java
 * Last modified on 06-04-2014 16:14-0400 by brianhmayo
 *
 * Copyright (c) 2014 SEPTA.  All rights reserved.
 */

package org.septa.android.app.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.septa.android.app.BuildConfig;
import org.septa.android.app.R;
import org.septa.android.app.adapters.RegionalRail_StopSelection_ListViewItem_ArrayAdapter;
import org.septa.android.app.fragments.StopSelectionListFragment;
import org.septa.android.app.models.ObjectFactory;
import org.septa.android.app.models.StopModel;
import org.septa.android.app.models.StopsModel;

import java.util.ArrayList;

public class NextToArriveStopSelectionActionBarActivity extends BaseAnalyticsActionBarActivity {
    public static final String TAG = NextToArriveStopSelectionActionBarActivity.class.getName();
    RegionalRail_StopSelection_ListViewItem_ArrayAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onCreate");
        }

        super.onCreate(savedInstanceState);

        String resourceName = getString(R.string.actionbar_iconimage_imagename_base).concat("nexttoarrive");

        int id = getResources().getIdentifier(resourceName, "drawable", getPackageName());

        // get the start or destination string from the extra intent string if it exists
        String nexttoarriveStopSelectionStartOrDestinationString = getIntent().getStringExtra(getString(R.string.regionalrail_stopselection_startordestination));
        if (nexttoarriveStopSelectionStartOrDestinationString != null) {
            getSupportActionBar().setTitle("Select "+nexttoarriveStopSelectionStartOrDestinationString);
        } else {
            getSupportActionBar().setTitle("| Select Start");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(id);

        setContentView(R.layout.nexttoarrive_stopselection);

        adapter = new RegionalRail_StopSelection_ListViewItem_ArrayAdapter(this, new ArrayList<StopModel>(0));
        StopSelectionListFragment stopsListFragment = (StopSelectionListFragment)getSupportFragmentManager().findFragmentById(R.id.stopselection_list_fragment);
        stopsListFragment.setListAdapter(adapter);
        stopsListFragment.setStartOrDestinationSelectionMode(nexttoarriveStopSelectionStartOrDestinationString);

        StopsLoader stopsLoader = new StopsLoader();
        stopsLoader.execute();
    }

    private class StopsLoader extends AsyncTask<Void, Integer, Boolean> {
        ArrayList<StopModel> stopsList = new ArrayList<StopModel>();

        private void loadStops() {
            StopsModel stopsModel = ObjectFactory.getInstance().getStopsModel();
            stopsModel.loadStops(NextToArriveStopSelectionActionBarActivity.this);

            stopsList = stopsModel.getStopModelArrayList();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            loadStops();

            return false;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);

            StopSelectionListFragment stopsListFragment = (StopSelectionListFragment)getSupportFragmentManager().findFragmentById(R.id.stopselection_list_fragment);
            stopsListFragment.setStopList(stopsList);
        }
    }
}