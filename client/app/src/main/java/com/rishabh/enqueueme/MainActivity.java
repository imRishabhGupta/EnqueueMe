package com.rishabh.enqueueme;

import android.Manifest;
import android.app.Application;
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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;

import static android.R.attr.data;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, BeaconConsumer {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BeaconManager beaconManager;
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private Message mActiveMessage;
    private MessageListener mMessageListener;
    private RecyclerView mRecyclerView;
    static View.OnClickListener myOnClickListener;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private PulsatorLayout pulsator;
    private static final Identifier nameSpaceId = Identifier.parse("0x5dc33487f02e477d4058");

    public CopyOnWriteArrayList<String> regionNameList;
    public CopyOnWriteArrayList<Region> regionList;
    public HashMap<String,Region> ssnRegionMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pulsator!=null&&pulsator.isStarted()){
                    pulsator.stop();
                    unpublish();
                    pulsator=null;
                }
            }
        });
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
        ArrayList<Queue> myDataset=new ArrayList<>();
        Queue queue=new Queue("Queue Name - Bank","Your Queue Number - 5","Current Queue Number - 1","moto");
        myDataset.add(queue);
        mAdapter = new QueueAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
        myOnClickListener = new MyOnClickListener(this);
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addApi(Nearby.MESSAGES_API, new MessagesOptions.Builder()
//                            .setPermissions(NearbyPermissions.BLE)
//                            .build())
//                    .addConnectionCallbacks(this)
//                    .enableAutoManage(this, this)
//                    .build();
//        }
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
        beaconManager.bind(this);

    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.i(TAG,"no of beacons i see are "+beacons.size());
                    Log.i(TAG, "The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
                    Log.i(TAG,region.getUniqueId());

                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {    }
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
            }
        });

        try {
            for(String key:ssnRegionMap.keySet()) {
                Region region = ssnRegionMap.get(key);
                beaconManager.startMonitoringBeaconsInRegion(region);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(context, "Item clicked.", Toast.LENGTH_SHORT).show();
        }
    }

    public void start(View view){
        pulsator = (PulsatorLayout) findViewById(R.id.pulsator);
        pulsator.start();
        publish("hello");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
       // unsubscribe();
//        unpublish();
    }

    private void subscribeFilter(){
//        MessageFilter messageFilter = new MessageFilter.Builder()
//                .includeEddystoneUids(MY_EDDYSTONE_UID_NAMESPACE, null /* any instance */)
//                .build();
//        SubscribeOptions options = new SubscribeOptions.Builder()
//                .setStrategy(Strategy.BLE_ONLY)
//                .setFilter(messageFilter)
//                .build();
//
//        MessageListener messageListener = new MessageListener() {
//            @Override
//            public void onFound(final Message message) {
//                // Note: Checking the type shown for completeness, but is unnecessary
//                // if your message filter only includes a single type.
//                if (Message.MESSAGE_NAMESPACE_RESERVED.equals(message.getNamespace())
//                        && Message.MESSAGE_TYPE_EDDYSTONE_UID.equals(message.getType())) {
//                    // Nearby provides the EddystoneUid class to parse Eddystone UIDs
//                    // that have been found nearby.
//                    EddystoneUid eddystoneUid = EddystoneUid.from(message);
//                    Log.i(TAG, "Found Eddystone UID: " + eddystoneUid);
//                }
//            }
//        };
//
//        Nearby.Messages.subscribe(googleApiClient, messageListener, options);
    }

    private void subscribe() {
        Log.i(TAG, "Subscribing.");
        SubscribeOptions options = new SubscribeOptions.Builder()
                .setStrategy(Strategy.BLE_ONLY)
                .build();
        Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener,options);
    }

    private void unsubscribe() {
        Log.i(TAG, "Unsubscribing.");
        Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener);
    }

    private void publish(String message) {
        Log.i(TAG, "Publishing message: " + message);
        mActiveMessage = new Message(message.getBytes());
        Log.i(TAG, mActiveMessage.getNamespace());
        Nearby.Messages.publish(mGoogleApiClient, mActiveMessage);
    }

    private void unpublish() {
        Log.i(TAG, "Unpublishing.");
        if (mActiveMessage != null) {
            Nearby.Messages.unpublish(mGoogleApiClient, mActiveMessage);
            mActiveMessage = null;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(getApplicationContext(), "Connected to nearby", Toast.LENGTH_SHORT).show();
//        publish("Hello");
        subscribe();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.e(TAG, "GoogleApiClient disconnected with cause: " + cause);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "GoogleApiClient connection failed");
        }
    }

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
}
