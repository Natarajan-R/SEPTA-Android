/*
 * RealtimeMenuFragment.java
 * Last modified on 02-04-2014 07:53-0500 by brianhmayo
 *
 * Copyright (c) 2014 SEPTA.  All rights reserved.
 */

package org.septa.android.app.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.septa.android.app.BuildConfig;
import org.septa.android.app.R;
import org.septa.android.app.activities.FindNearestLocationActionBarActivity;
import org.septa.android.app.activities.NextToArriveActionBarActivity;
import org.septa.android.app.activities.SystemStatusActionBarActivity;
import org.septa.android.app.activities.TipsActionBarActivity;
import org.septa.android.app.activities.TrainViewActionBarActivity;
import org.septa.android.app.activities.TransitViewActionBarActivity;
import org.septa.android.app.events.EventsConstants;
import org.septa.android.app.events.model.GsonObject;
import org.septa.android.app.events.model.Message;
import org.septa.android.app.events.network.EventsNetworkService;
import org.septa.android.app.events.util.PopeUtils;
import org.septa.android.app.managers.AlertManager;
import org.septa.android.app.models.servicemodels.AlertModel;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class RealtimeMenuFragment extends Fragment implements
        AlertManager.IAlertListener,
        OnClickListener {
    private static final String TAG = RealtimeMenuFragment.class.getName();

    private static final String KEY_ARG_MESSAGE_JSON = "KEY_ARG_MESSAGE_JSON";
    private static final String URI_PASS_PERKS = "http://www.iseptaphilly.com/perks";

    private ImageView mFindNearestLocationImage;
    private ImageView mNextToArriveImage;
    private ImageView mSystemStatusImage;
    private ImageView mTipsImage;
    private ImageView mTrainViewImage;
    private ImageView mTransitViewImage;
    private ImageView mPassPerksImageView;
    private TextView mSpecialEventMessage;

    private boolean isPopeVisitingToday;
    private Message mMessage;
    private String mSpecialEventUrl;

    private BroadcastReceiver mSpecialEventReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (BuildConfig.DEBUG) {
                Log.v(TAG, "onReceive");
            }

            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String result = intent.getStringExtra(EventsConstants.KEY_RESULT);

                if (result != null) {
                    if (result.equals(EventsConstants.VALUE_EVENTS_NETWORK_ERROR)) {
                        if (BuildConfig.DEBUG) {
                            Log.v(TAG, "error");
                        }

                        // Set the default message
                        mSpecialEventMessage.setText(R.string.realtime_menu_default_special_event_message);
                        mSpecialEventMessage.setVisibility(isPopeVisitingToday ? View.VISIBLE : View.GONE);
                    }

                    else if (result.equals(EventsConstants.VALUE_EVENTS_NETWORK_SUCCESS)) {
                        if (BuildConfig.DEBUG) {
                            Log.v(TAG, "success");
                        }

                        // Get response object
                        String messageJson = intent.getStringExtra(EventsConstants.KEY_EVENTS_JSON_RESPONSE);

                        if (!TextUtils.isEmpty(messageJson)) {
                            mMessage = GsonObject.fromJson(messageJson, Message.class);
                        }

                        if (mMessage != null) {

                            // Get the start and end dates from the response object
                            String endDate = mMessage.getSpecialEventEndDate();
                            String startDate = mMessage.getSpecialEventStartDate();

                            boolean updateEndDate = PopeUtils.updatePopeVisitEndDate(getActivity(), endDate);
                            boolean updateStartDate = PopeUtils.updatePopeVisitStartDate(getActivity(), startDate);

                            // If start or end date is different than date saved to device, update the date and check if pope is visiting today
                            if (updateEndDate || updateStartDate) {

                                isPopeVisitingToday = PopeUtils.isPopeVisitingToday(getActivity());
                            }

                            // Set image resources
                            mNextToArriveImage.setImageResource(isPopeVisitingToday ? R.drawable.realtime_menu_nexttoarrive_selector_disabled : R.drawable.realtime_menu_nexttoarrive_selector);
                            mFindNearestLocationImage.setImageResource(isPopeVisitingToday ? R.drawable.realtime_menu_findnearestlocation_selector_disabled : R.drawable.realtime_menu_findnearestlocation_selector);
                            mTrainViewImage.setImageResource(isPopeVisitingToday ? R.drawable.realtime_menu_trainview_selector_disabled : R.drawable.realtime_menu_trainview_selector);
                            mTransitViewImage.setImageResource(isPopeVisitingToday ? R.drawable.realtime_menu_transitview_selector_disabled : R.drawable.realtime_menu_transitview_selector);

                            mSpecialEventUrl = mMessage.getSpecialEventUrl();

                            // Set the event message and its visibility
                            String specialEventMessage = mMessage.getSpecialEventMessage();
                            mSpecialEventMessage.setText(!TextUtils.isEmpty(specialEventMessage) ? specialEventMessage : getString(R.string.realtime_menu_default_special_event_message));
                            mSpecialEventMessage.setVisibility(isPopeVisitingToday && !TextUtils.isEmpty(specialEventMessage) ? View.VISIBLE : View.GONE);
                        }
                    }

                    else {
                        if (BuildConfig.DEBUG) {
                            Log.v(TAG, "unknown result");
                        }
                    }
                }
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (BuildConfig.DEBUG) {
            Log.v(TAG, "onAttach");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            Log.v(TAG, "onCreate");
        }

        // Default parameters
        Bundle args = getArguments();
        if (args == null) {

        }
        // Otherwise, set incoming parameters
        else {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (BuildConfig.DEBUG) {
            Log.v(TAG, "onCreateView: savedInstanceState" + (savedInstanceState != null ? " != " : " == ") + "null");
        }

        View view = inflater.inflate(R.layout.realtime_menu_fragment, container, false);

        // Setup views
        mSpecialEventMessage = (TextView) view.findViewById(R.id.realtime_menu_papal_message);
        mFindNearestLocationImage = (ImageView) view.findViewById(R.id.realtime_menu_find_nearest_location_image_view);
        mNextToArriveImage = (ImageView) view.findViewById(R.id.realtime_menu_next_to_arrive_image_view);
        mSystemStatusImage = (ImageView) view.findViewById(R.id.realtime_menu_system_status_image_view);
        mTipsImage = (ImageView) view.findViewById(R.id.realtime_menu_tips_image_view);
        mTrainViewImage = (ImageView) view.findViewById(R.id.realtime_menu_trainview_image_view);
        mTransitViewImage = (ImageView) view.findViewById(R.id.realtime_menu_transitview_image_view);
        mPassPerksImageView = (ImageView) view.findViewById(R.id.realtime_menu_pass_perks_image_view);

        isPopeVisitingToday = PopeUtils.isPopeVisitingToday(getActivity());

        // Set image resources
        mNextToArriveImage.setImageResource(isPopeVisitingToday ? R.drawable.realtime_menu_nexttoarrive_selector_disabled : R.drawable.realtime_menu_nexttoarrive_selector);
        mFindNearestLocationImage.setImageResource(isPopeVisitingToday ? R.drawable.realtime_menu_findnearestlocation_selector_disabled : R.drawable.realtime_menu_findnearestlocation_selector);
        mTrainViewImage.setImageResource(isPopeVisitingToday ? R.drawable.realtime_menu_trainview_selector_disabled : R.drawable.realtime_menu_trainview_selector);
        mTransitViewImage.setImageResource(isPopeVisitingToday ? R.drawable.realtime_menu_transitview_selector_disabled : R.drawable.realtime_menu_transitview_selector);

        // Set click listeners
        mFindNearestLocationImage.setOnClickListener(this);
        mNextToArriveImage.setOnClickListener(this);
        mSpecialEventMessage.setOnClickListener(this);
        mSystemStatusImage.setOnClickListener(this);
        mTipsImage.setOnClickListener(this);
        mTrainViewImage.setOnClickListener(this);
        mTransitViewImage.setOnClickListener(this);
        mPassPerksImageView.setOnClickListener(this);

        // If this is the first creation, default state variables
        if (savedInstanceState == null) {

        }

        // Otherwise, restore state
        else {

            // Get the message json string
            if (isPopeVisitingToday) {
                String messageJson = savedInstanceState.getString(KEY_ARG_MESSAGE_JSON);
                if (!TextUtils.isEmpty(messageJson)) {
                    mMessage = GsonObject.fromJson(messageJson, Message.class);
                    if (mMessage != null) {
                        String specialMessage = mMessage.getSpecialEventMessage();
                        mSpecialEventMessage.setText(specialMessage != null ? specialMessage : "");
                        mSpecialEventMessage.setVisibility(!TextUtils.isEmpty(specialMessage) ? View.VISIBLE : View.GONE);
                    }
                }
            }
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (BuildConfig.DEBUG) {
            Log.v(TAG, "onViewCreated: savedInstanceState " + (savedInstanceState == null ? "==" : "!=") + " null");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (BuildConfig.DEBUG) {
            Log.v(TAG, "onActivityCreated: savedInstanceState " + (savedInstanceState == null ? "==" : "!=") + " null");
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (BuildConfig.DEBUG) {
            Log.v(TAG, "onViewStateRestored: savedInstanceState " + (savedInstanceState == null ? "==" : "!=") + " null");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (BuildConfig.DEBUG) {
            Log.v(TAG, "onStart");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        AlertManager.getInstance().addListener(this);
        AlertManager.getInstance().fetchGlobalAlert();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mSpecialEventReceiver, new IntentFilter(EventsNetworkService.NOTIFICATION));

        // Per design requirement, call network every time user resumes page
        Intent intent = new Intent(getActivity(), EventsNetworkService.class);
        getActivity().startService(intent);
    }

    @Override
    public void onPause() {
        AlertManager.getInstance().removeListener(this);
        super.onPause();

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mSpecialEventReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (BuildConfig.DEBUG) {
            Log.v(TAG, "onSaveInstanceState");
        }

        if (mMessage != null) {

            // Convert forecast object to json string
            String messageJson = GsonObject.convertObjectToJsonString(mMessage, false);

            // Add json string to bundle
            outState.putString(KEY_ARG_MESSAGE_JSON, messageJson);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (BuildConfig.DEBUG) {
            Log.v(TAG, "onStop");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (BuildConfig.DEBUG) {
            Log.v(TAG, "onDestroyView");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (BuildConfig.DEBUG) {
            Log.v(TAG, "onDestroy");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (BuildConfig.DEBUG) {
            Log.v(TAG, "onDetach");
        }
    }

    @Override
    public void alertsDidUpdate() {
        AlertModel alert = AlertManager.getInstance().getGlobalAlert();
        if (alert != null && !alert.getCurrentMessage().isEmpty()) {
            Crouton crouton = Crouton.makeText(getActivity(), Html.fromHtml(alert.getCurrentMessage()),
                    Style.ALERT, Configuration.DURATION_LONG);
            crouton.setConfiguration(new Configuration.Builder().setDuration(Configuration.DURATION_SHORT).build());
            crouton.show();
        }

    }

    @Override
    public void onClick(View v) {

        Intent intent = null;

        switch (v.getId()) {

            case R.id.realtime_menu_find_nearest_location_image_view:

                if (!isPopeVisitingToday) {

                    intent = new Intent(getActivity(), FindNearestLocationActionBarActivity.class);

                    intent.putExtra(getString(R.string.actionbar_titletext_key), getString(R.string.page_title_find_nearest_location));
                    intent.putExtra(getString(R.string.actionbar_iconimage_imagenamesuffix_key), getString(R.string.page_icon_id_find_nearest_location));

                }

                else {
                    mSpecialEventMessage.setVisibility(View.VISIBLE);
                }

                break;

            case R.id.realtime_menu_next_to_arrive_image_view:

                if (!isPopeVisitingToday) {

                    intent = new Intent(getActivity(), NextToArriveActionBarActivity.class);

                    intent.putExtra(getString(R.string.actionbar_titletext_key), getString(R.string.page_title_next_to_arrive));
                    intent.putExtra(getString(R.string.actionbar_iconimage_imagenamesuffix_key), getString(R.string.page_icon_id_next_to_arrive));

                }

                else {
                    mSpecialEventMessage.setVisibility(View.VISIBLE);
                }

                break;

            case R.id.realtime_menu_system_status_image_view:

                    intent = new Intent(getActivity(), SystemStatusActionBarActivity.class);

                    intent.putExtra(getString(R.string.actionbar_titletext_key), getString(R.string.page_title_system_status));
                    intent.putExtra(getString(R.string.actionbar_iconimage_imagenamesuffix_key), getString(R.string.page_icon_id_system_status));

                break;

            case R.id.realtime_menu_tips_image_view:

                    intent = new Intent(getActivity(), TipsActionBarActivity.class);

                    intent.putExtra(getString(R.string.actionbar_titletext_key), getString(R.string.page_title_special_events));
                    intent.putExtra(getString(R.string.actionbar_iconimage_imagenamesuffix_key), getString(R.string.page_icon_id_tips));

                break;

            case R.id.realtime_menu_trainview_image_view:

                if (!isPopeVisitingToday) {

                    intent = new Intent(getActivity(), TrainViewActionBarActivity.class);

                    intent.putExtra(getString(R.string.actionbar_titletext_key), getString(R.string.page_title_train_view));
                    intent.putExtra(getString(R.string.actionbar_iconimage_imagenamesuffix_key), getString(R.string.page_icon_id_trainview));

                }

                else {
                    mSpecialEventMessage.setVisibility(View.VISIBLE);
                }

                break;

            case R.id.realtime_menu_transitview_image_view:

                if (!isPopeVisitingToday) {

                    intent = new Intent(getActivity(), TransitViewActionBarActivity.class);

                    intent.putExtra(getString(R.string.actionbar_titletext_key), getString(R.string.page_title_transit_view));
                    intent.putExtra(getString(R.string.actionbar_iconimage_imagenamesuffix_key), getString(R.string.page_icon_id_transitview));

                }

                else {
                    mSpecialEventMessage.setVisibility(View.VISIBLE);
                }

                break;

            // Papal Visit Special Message
            case R.id.realtime_menu_papal_message:

                Uri uri = Uri.parse(!TextUtils.isEmpty(mSpecialEventUrl) ? mSpecialEventUrl : EventsConstants.VALUE_POPE_VISIT_DEFAULT_URL);
                intent = new Intent(Intent.ACTION_VIEW, uri);

                break;

            // Pass perks
            case R.id.realtime_menu_pass_perks_image_view:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URI_PASS_PERKS));
                break;

            default:

                if (BuildConfig.DEBUG) {
                    Log.w(TAG, "onClick: unknown view clicked");
                }

                break;
        }

        if (intent != null) {
            startActivity(intent);
        }
    }
}
