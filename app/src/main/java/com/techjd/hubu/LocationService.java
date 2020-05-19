package com.techjd.hubu;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import com.google.android.gms.location.LocationResult;
import com.techjd.hubu.fragments.CardsFragment;

public class LocationService extends BroadcastReceiver {

    public static final String ACTIO_PROCESS_UPDATE = "com.techjd.hubu.UPDATE_LOCATION";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null)
        {
            final String ac = intent.getAction();
            if (ACTIO_PROCESS_UPDATE.equals(ac))
            {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null)
                {
                    Location location = result.getLastLocation();
                    String locationstring = new StringBuilder(""+location.getLatitude())
                            .append("/")
                            .append(location.getLongitude())
                            .toString();
                    try {
                        //CardsFragment.getInstance().updateTextView(locationstring);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}
