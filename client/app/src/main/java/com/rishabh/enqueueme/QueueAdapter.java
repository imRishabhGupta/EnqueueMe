package com.rishabh.enqueueme;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by lenovo on 27/12/2016.
 */
public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.MyViewHolder> {

    private ArrayList<Queue> dataSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView queueName;
        TextView yourQueueNumber;
        TextView currentNumber;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.queueName = (TextView) itemView.findViewById(R.id.name);
            this.yourQueueNumber = (TextView) itemView.findViewById(R.id.your_number);
            this.currentNumber = (TextView) itemView.findViewById(R.id.current_number);
        }
    }

    public QueueAdapter(ArrayList<Queue> data) {
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.queue, parent, false);

        view.setOnClickListener(MainActivity.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView queueName = holder.queueName;
        TextView yourQueueNumber = holder.yourQueueNumber;
        TextView currentNumber = holder.currentNumber;

        queueName.setText(dataSet.get(listPosition).getQueueName());
        yourQueueNumber.setText(dataSet.get(listPosition).getYourQueueNumber());
        currentNumber.setText(dataSet.get(listPosition).getCurrentNumber());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
