package com.rishabh.enqueueme;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by lenovo on 27/12/2016.
 */
public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.MyViewHolder> {

    private ArrayList<Queue> dataSet;
    public Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView queueName;
        Button leaveQueue;
        TextView yourQueueNumber;
        TextView currentNumber;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.queueName = (TextView) itemView.findViewById(R.id.name);
            this.yourQueueNumber = (TextView) itemView.findViewById(R.id.your_number);
            this.currentNumber = (TextView) itemView.findViewById(R.id.current_number);
            this.leaveQueue = (Button) itemView.findViewById(R.id.leave);
        }
    }

    public QueueAdapter(ArrayList<Queue> data,Context context) {
        this.dataSet = data;
        this.context=context;
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

        final TextView queueName = holder.queueName;
        TextView yourQueueNumber = holder.yourQueueNumber;
        TextView currentNumber = holder.currentNumber;
        Button leaveQueue = holder.leaveQueue;

        queueName.setText("Queue id - "+dataSet.get(listPosition).getQueueName());
        yourQueueNumber.setText("My queue no - "+dataSet.get(listPosition).getYourQueueNumber());
        currentNumber.setText(dataSet.get(listPosition).getCurrentNumber());
        leaveQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("here ","clicked item");
                MainActivity.leaveQueue(queueName.getText().toString(),context);
                dataSet.remove(listPosition);
                notifyItemRemoved(listPosition);
                notifyItemRangeChanged(listPosition,dataSet.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
