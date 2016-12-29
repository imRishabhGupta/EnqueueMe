package com.rishabh.enqueueme;

/**
 * Created by rohanagarwal94 on 29/12/16.
 */
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;

/**
 * Created by rohanagarwal94 on 30/7/16.
 */
public class SearchQueue extends DialogFragment {
    private RecyclerView mRecyclerView;
    private ArrayList<Beacon> beaconItems;
    // this method create view for your Dialog

    public SearchQueue(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate layout with recycler view
        View v = inflater.inflate(R.layout.queue_dialog, container, false);
        getDialog().getWindow().getAttributes().alpha = 0.8f;
//        beaconItems=this.getArguments().getParcelableArrayList("comments");
//        Log.d("comments size",String.valueOf(beaconItems.size()));
        mRecyclerView = (RecyclerView)v.findViewById(R.id.recycler_view_comment);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        BeaconListAdapter adapter = new BeaconListAdapter(beaconItems);
        mRecyclerView.setAdapter(adapter);
        return v;
    }
}