package com.rishabh.enqueueme;

import android.Manifest;
import android.app.Application;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageFilter;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.MessagesOptions;
import com.google.android.gms.nearby.messages.NearbyPermissions;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.rishabh.enqueueme.R;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;

import static android.R.attr.data;
import static android.R.attr.gestureColor;


public class MainActivity extends AppCompatActivity implements BeaconConsumer{//}, Callback<Queues> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BeaconManager beaconManager;
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private Message mActiveMessage;
    private MessageListener mMessageListener;
    private RecyclerView mRecyclerView;
    static View.OnClickListener myOnClickListener;
    public RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Queue> myDataset;
    private PulsatorLayout pulsator;
    private int flag=1;
    private ArrayList<Beacon> beaconList;
    private static final Identifier nameSpaceId = Identifier.parse("0x5dc33487f02e477d4058");
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static String[] mPermissions = { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH};
    public CopyOnWriteArrayList<String> regionNameList;
    public CopyOnWriteArrayList<Region> regionList;
    public HashMap<String,Region> ssnRegionMap;
    public RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        progressBar = (ProgressBar) findViewById(R.id.progressBardce);
//        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
//        progressBar.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://35.164.180.109:1236/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        // prepare call in Retrofit 2.0
//        QueueApi stackOverflowAPI = retrofit.create(QueueApi.class);
//
//        Call<Queues> call = stackOverflowAPI.getQueues(Utils.getUserId(this));
//        //asynchronous call
//        call.enqueue(this);

        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                Log.i(TAG, "Message found: " + message);
                Log.i(TAG, "Message string: " + new String(message.getContent()));
                Log.i(TAG, "Message namespaced type: " + message.getNamespace() +
                        "/" + message.getType());
                Toast.makeText(MainActivity.this, ""+new String(message.getContent()), Toast.LENGTH_SHORT).show();
                //Create a dialog to let user connect to that beacon or join the queue
            }

            @Override
            public void onLost(Message message) {
                String messageAsString = new String(message.getContent());
                Log.d(TAG, "Lost sight of message: " + messageAsString);
            }
        };

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        myDataset=new ArrayList<>();
        //Queue queue=new Queue("Queue Name - Bank","Your Queue Number - 5","Current Queue Number - 1","moto");
        //myDataset.add(queue);
        mAdapter = new QueueAdapter(myDataset, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
        myOnClickListener = new MyOnClickListener(this);

        ssnRegionMap = new HashMap<>();
        regionList = new CopyOnWriteArrayList<>();
        regionNameList = new CopyOnWriteArrayList<>();

        ssnRegionMap.put("0x0117c59825E9",new Region("Test Room",nameSpaceId, Identifier.parse("0x0117c59825E9"),null));
        ssnRegionMap.put("0x0117c55be3a8",new Region("Git Room",nameSpaceId,Identifier.parse("0x0117c55be3a8"),null));
        ssnRegionMap.put("0x0117c552c493",new Region("Android Room",nameSpaceId,Identifier.parse("0x0117c552c493"),null));
        ssnRegionMap.put("0x0117c55fc452",new Region("iOS Room",nameSpaceId,Identifier.parse("0x0117c55fc452"),null));
        ssnRegionMap.put("0x0117c555c65f",new Region("Python Room",nameSpaceId,Identifier.parse("0x0117c555c65f"),null));
        ssnRegionMap.put("0x0117c55d6660",new Region("Office",nameSpaceId,Identifier.parse("0x0117c55d6660"),null));
        ssnRegionMap.put("0x0117c55ec086",new Region("Ruby Room",nameSpaceId,Identifier.parse("0x0117c55ec086"),null));

        beaconManager = BeaconManager.getInstanceForApplication(getApplicationContext());
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        new BackgroundPowerSaver(this);
        requestQueue = Volley.newRequestQueue(MainActivity.this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                Log.i(TAG,"No of beacons are "+beacons.size());

                if(flag==1 && beacons.size()>0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pulsator.stop();
                            pulsator.setVisibility(View.GONE);
                        }
                    });
                    beaconList=new ArrayList<>(beacons);
                    flag=0;
                    Log.d(TAG,"size is "+beaconList.size());
                    beaconManager.removeAllRangeNotifiers();
                    beaconManager.removeAllMonitorNotifiers();
                    beaconManager.unbind(MainActivity.this);
                    FragmentManager fm = getFragmentManager();
                    SearchQueue newFragment = new SearchQueue(MainActivity.this);
                    Bundle b=new Bundle();
                    b.putParcelableArrayList("beacons",beaconList);
                    newFragment.setArguments(b);
                    newFragment.show(fm,"Comments");
                }
                for (Beacon beacon: beacons) {

                    if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x00) {
                        // This is a Eddystone-UID frame
                        Identifier namespaceId = beacon.getId1();
                        Identifier instanceId = beacon.getId2();

                        Log.d(TAG, "I see a beacon transmitting namespace id: "+namespaceId+
                                " and instance id: "+instanceId+
                                " approximately "+beacon.getDistance()+" meters away.");

                        // Do we have telemetry data?
                        if (beacon.getExtraDataFields().size() > 0) {
                            long telemetryVersion = beacon.getExtraDataFields().get(0);
                            long batteryMilliVolts = beacon.getExtraDataFields().get(1);
                            long pduCount = beacon.getExtraDataFields().get(3);
                            long uptime = beacon.getExtraDataFields().get(4);

                            Log.d(TAG, "The above beacon is sending telemetry version "+telemetryVersion+
                                    ", has been up for : "+uptime+" seconds"+
                                    ", has a battery level of "+batteryMilliVolts+" mV"+
                                    ", and has transmitted "+pduCount+" advertisements.");

                        }
//>>>>>>> 23904c63eb9c79a5d7749059e7442fe39aa87343

                    }
                }
            }
        });

        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw an beacon for the first time!");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see an beacon");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: "+state);
                try {
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            for(String key:ssnRegionMap.keySet()) {
                Region region = ssnRegionMap.get(key);
                //beaconManager.startMonitoringBeaconsInRegion(region);
                beaconManager.startRangingBeaconsInRegion(region);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }



//        @Override
//        public void onResponse(Response<Queues> response, Retrofit retrofit) {
//            myDataset.clear();
//            myDataset.addAll(response.body().items);
//            Log.d("response",String.valueOf(response));
//            Log.d("userid",String.valueOf(Utils.getUserId(this)));
//            mAdapter.notifyDataSetChanged();
//        }
//
//        @Override
//        public void onFailure(Throwable t) {
//            Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//            Log.d("error",t.getLocalizedMessage());
//
//    }

    private static class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            String url="http://35.164.180.109:1236/user/getStatus/"+((TextView)v.findViewById(R.id.your_number)).getText().toString();
            //Toast.makeText(context, ((TextView)v.findViewById(R.id.your_number)).getText().toString()+" id", Toast.LENGTH_SHORT).show();
//            RequestQueue requestQueue = Volley.newRequestQueue(context);
//            JsonObjectRequest jor = new JsonObjectRequest(url, null,
//                    new com.android.volley.Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            Toast.makeText(context, "got response "+response.toString(), Toast.LENGTH_LONG).show();
//                            //hideProgress();
//                            Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
//
//                        }
//                    },
//                    new com.android.volley.Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Toast.makeText(context,"Error in internet connection.",Toast.LENGTH_LONG).show();
//                            Log.e("Volley",error.toString());
//                        }
//                    }
//            ){
//                @Override
//                protected Map<String,String> getParams(){
//                    Map<String,String> params = new HashMap<String, String>();
//                    return params;
//                }
//
//            };
//
//            Toast.makeText(context, "Item clicked.", Toast.LENGTH_SHORT).show();
//            requestQueue.add(jor);
            String id=((TextView)v.findViewById(R.id.your_number)).getText().toString();

            char ch=id.charAt(id.length()-1);
            int id1=(int)ch;
            id1=id1-48;
            Log.d(TAG,id1+"");
            int randomNum = ThreadLocalRandom.current().nextInt(1, id1+1);

            Toast.makeText(context, "Current queue no is "+randomNum, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isBlueEnable() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        return bluetoothAdapter.isEnabled();

    }

    private boolean havePermissions() {
        for(String permission:mPermissions){
            if(ActivityCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED){
                return  false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                mPermissions, PERMISSIONS_REQUEST_CODE);
    }

    public void start(View view){
        if (!havePermissions()) {
            Log.i(TAG, "Requesting permissions needed for this app.");
            requestPermissions();
        }

        if(!isBlueEnable()){
            Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(bluetoothIntent);
        }

        pulsator = (PulsatorLayout) findViewById(R.id.pulsator);

        if(!beaconManager.isBound(this)){
            beaconManager.bind(this);
            flag=1;
            pulsator.setVisibility(View.VISIBLE);
            pulsator.start();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://35.164.180.109:1236/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        // prepare call in Retrofit 2.0
//        QueueApi stackOverflowAPI = retrofit.create(QueueApi.class);
//
//        Call<Queues> call = stackOverflowAPI.getQueues(Utils.getUserId(this));
//        //asynchronous call
//        call.enqueue(this);


        // synchronous call would be with execute, in this case you
        // would have to perform this outside the main thread
        // call.execute()

        // to cancel a running request
        // call.cancel();
        // calls can only be used once but you can easily clone them
        //Call<StackOverflowQuestions> c = call.clone();
        //c.enqueue(this);
        String url="http://35.164.180.109:1236/user/getQueue/"+Utils.getUserId(this);

        JsonObjectRequest jor = new JsonObjectRequest(url, null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast.makeText(getApplicationContext(), "got response "+response.toString(), Toast.LENGTH_LONG).show();
                        //hideProgress();
                        try {
                                Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                                JSONObject json=new JSONObject(response.toString());
                                JSONArray queues=json.getJSONArray("queues");
                                String id=json.getString("id");
                                myDataset.clear();
                                for(int i=0;i<queues.length();i++){
                                    JSONObject queue1=queues.getJSONObject(i);
                                    Queue queue=new Queue(queue1.getString("beconId"),id,"Click to see Current queue No",queue1.getString("devId"));
                                    myDataset.add(queue);
                                }
                                mAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Error in internet connection.",Toast.LENGTH_LONG).show();
                        Log.e("Volley",error.toString());
                    }
                }
        ){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                return params;
            }

        };
        requestQueue.add(jor);

        return true;

    }

//    private void subscribe() {
//        Log.i(TAG, "Subscribing.");
//        SubscribeOptions options = new SubscribeOptions.Builder()
//                .setStrategy(Strategy.BLE_ONLY)
//                .build();
//        Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener,options);
//    }
//
//    private void unsubscribe() {
//        Log.i(TAG, "Unsubscribing.");
//        Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener);
//    }
//
//    private void publish(String message) {
//        Log.i(TAG, "Publishing message: " + message);
//        mActiveMessage = new Message(message.getBytes());
//        Log.i(TAG, mActiveMessage.getNamespace());
//        Nearby.Messages.publish(mGoogleApiClient, mActiveMessage);
//    }
//
//    private void unpublish() {
//        Log.i(TAG, "Unpublishing.");
//        if (mActiveMessage != null) {
//            Nearby.Messages.unpublish(mGoogleApiClient, mActiveMessage);
//            mActiveMessage = null;
//        }
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        Toast.makeText(getApplicationContext(), "Connected to nearby", Toast.LENGTH_SHORT).show();
////        publish("Hello");
//        subscribe();
//    }
//
//    @Override
//    public void onConnectionSuspended(int cause) {
//        Log.e(TAG, "GoogleApiClient disconnected with cause: " + cause);
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult result) {
//        if (result.hasResolution()) {
//            try {
//                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
//            } catch (IntentSender.SendIntentException e) {
//                e.printStackTrace();
//            }
//        } else {
//            Log.e(TAG, "GoogleApiClient connection failed");
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                Log.e(TAG, "GoogleApiClient connection failed. Unable to resolve.");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != PERMISSIONS_REQUEST_CODE) {
            return;
        }
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,permission)) {
                    Toast.makeText(this, "Permission denied without 'NEVER ASK AGAIN': " + permission, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission denied with 'NEVER ASK AGAIN': " + permission, Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.i(TAG, "Permission granted, building GoogleApiClient");
            }
        }
    }

    public static void leaveQueue(String instanceId, final Context context){
        String url="http://35.164.180.109:1236/user/removeQueue/"+instanceId;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jor = new JsonObjectRequest(url, null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, "got response "+response.toString(), Toast.LENGTH_LONG).show();
                        //hideProgress();
                        Toast.makeText(context, "successfully removed", Toast.LENGTH_SHORT).show();


                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,"Error in internet connection.",Toast.LENGTH_LONG).show();
                        Log.e("Volley",error.toString());
                    }
                }
        ){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                return params;
            }

        };
        requestQueue.add(jor);
    }
}
