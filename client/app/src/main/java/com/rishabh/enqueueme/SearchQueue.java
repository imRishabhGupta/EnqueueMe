package com.rishabh.enqueueme;

/**
 * Created by rohanagarwal94 on 29/12/16.
 */
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by rohanagarwal94 on 30/7/16.
 */
public class SearchQueue extends DialogFragment {
    private RecyclerView mRecyclerView;
    private ArrayList<Beacon> beaconItems;
    private Context context;
    // this method create view for your Dialog

    public SearchQueue(Context context){
        this.context=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate layout with recycler view
        View v = inflater.inflate(R.layout.queue_dialog, container, false);
        getDialog().getWindow().getAttributes().alpha = 0.8f;
        beaconItems=this.getArguments().getParcelableArrayList("beacons");
//        Log.d("comments size",String.valueOf(beaconItems.size()));
        mRecyclerView = (RecyclerView)v.findViewById(R.id.recycler_view_comment);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        BeaconListAdapter adapter = new BeaconListAdapter(beaconItems, new BeaconListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Beacon item) {
//                Toast.makeText(context,"ccwe",Toast.LENGTH_SHORT).show();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://35.164.180.109:1236/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                QueueApi stackOverflowAPI = retrofit.create(QueueApi.class);
                stackOverflowAPI.insertUser(
                        item.getId2().toString(), Utils.getUserId(context),
                        new Callback<Response>() {
                            @Override
                            public void onResponse(Response<Response> response, Retrofit retrofit) {

                                try {
                                    JSONObject jsonObject= new JSONObject(response.toString());
                                    Boolean success = jsonObject.getBoolean("success");
                                    if(success)
                                    {
                                        Toast.makeText(context,"You are queued",Toast.LENGTH_SHORT).show();


                                    }
                                    else
                                    {
                                        Toast.makeText(context,"You were already in queue",Toast.LENGTH_SHORT).show();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onFailure(Throwable t) {

                                Toast.makeText(context,"failed response",Toast.LENGTH_SHORT).show();
                            }
                        }
                );

            }
        });
        mRecyclerView.setAdapter(adapter);
        return v;
    }
}