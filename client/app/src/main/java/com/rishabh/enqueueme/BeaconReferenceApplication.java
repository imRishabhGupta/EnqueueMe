package com.rishabh.enqueueme;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

/**
 * Created by lenovo on 29/12/2016.
 */

public class BeaconReferenceApplication extends Application implements BootstrapNotifier {
    private static final String TAG = BeaconReferenceApplication.class.getSimpleName();
    private RegionBootstrap regionBootstrap;
    private static final Identifier nameSpaceId = Identifier.parse("0x5dc33487f02e477d4058");
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "App started up");
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        new BackgroundPowerSaver(this);
        // wake up the app when any beacon is seen (you can specify specific id filers in the parameters below)
        Region region =new Region("Python Room",nameSpaceId, Identifier.parse("0x0117c555c65f"),null);
        regionBootstrap = new RegionBootstrap(this, region);
    }



    @Override
    public void didEnterRegion(org.altbeacon.beacon.Region region) {
        Log.d(TAG, "Got a didEnterRegion call");
        // This call to disable will make it so the activity below only gets launched the first time a beacon is seen (until the next time the app is launched)
        // if you want the Activity to launch every single time beacons come into view, remove this call.
        regionBootstrap.disable();
        Intent intent = new Intent(this, MainActivity.class);
        // IMPORTANT: in the AndroidManifest.xml definition of this activity, you must set android:launchMode="singleInstance" or you will get two instances
        // created when a user launches the activity manually and it gets launched from here.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    @Override
    public void didExitRegion(org.altbeacon.beacon.Region region) {

    }

    @Override
    public void didDetermineStateForRegion(int i, org.altbeacon.beacon.Region region) {

    }
}
