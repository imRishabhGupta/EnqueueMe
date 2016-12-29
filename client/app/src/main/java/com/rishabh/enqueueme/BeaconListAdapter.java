package com.rishabh.enqueueme;

/**
 * Created by rohanagarwal94 on 29/12/16.
 */
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;


public class BeaconListAdapter extends RecyclerView.Adapter<BeaconListAdapter.MyViewHolder> {

    private ArrayList<Beacon> beaconItems;
    private final OnItemClickListener listener;
    private Beacon beaconItem;

    public interface OnItemClickListener {
        void onItemClick(Beacon item);
    }
//    private OnItemClickListener mOnItemClickListener;

//
//    public interface OnItemClickListener {
//        public void onItemClick(View view, int position);
//    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView commentTime, from, comment;

        public MyViewHolder(View view) {
            super(view);
            view.bringToFront();
            from = (TextView) view
                    .findViewById(R.id.from);
            comment = (TextView) view.findViewById(R.id.comment);

        }


    }


    public BeaconListAdapter(ArrayList<Beacon> beaconItems, OnItemClickListener listener) {
        this.beaconItems=beaconItems;
//        mOnItemClickListener = onItemClickListener;
        this.listener = listener;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.beacon_list_view, parent, false);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(beaconItem);
            }
        });

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        beaconItem = beaconItems.get(position);


        if (!TextUtils.isEmpty(beaconItem.getId1().toString())) {
            holder.comment.setText(beaconItem.getId1().toString());
            holder.comment.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            holder.comment.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(beaconItem.getId2().toString())) {
            holder.from.setText(beaconItem.getId2().toString());
            holder.from.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            holder.from.setVisibility(View.GONE);
        }



    }

    @Override
    public int getItemCount() {
        return beaconItems.size();
    }

}
