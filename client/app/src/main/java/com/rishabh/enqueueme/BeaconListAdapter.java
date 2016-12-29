package com.rishabh.enqueueme;

/**
 * Created by rohanagarwal94 on 29/12/16.
 */
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class BeaconListAdapter extends RecyclerView.Adapter<BeaconListAdapter.MyViewHolder> {

    private ArrayList<BeaconItem> beaconItems;


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


    public BeaconListAdapter(ArrayList<BeaconItem> beaconItems) {
        this.beaconItems=beaconItems;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.beacon_list_view, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final BeaconItem beaconItem = beaconItems.get(position);


        if (!TextUtils.isEmpty(beaconItem.getNamespaceId())) {
            holder.comment.setText(beaconItem.getNamespaceId());
            holder.comment.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            holder.comment.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(beaconItem.getInstanceId())) {
            holder.from.setText(beaconItem.getInstanceId());
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
