package com.android.sms;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

class LocationListenerImpl implements LocationListener {
 

    LocationListenerImpl( final SmsReceiver mainActivity ) {
    	
    }

    public void onStatusChanged( final String provider, final int status, final Bundle extras ) {
        // do nothing;
    }

    public void onProviderEnabled(final String provider ) {
        // do nothing;
    }

    public void onProviderDisabled(  final String provider ) {
        // do nothing;
    }

    public void onLocationChanged( final Location location ) {
     /*   if ( !_SmsBroadCast.isPaused() && location != null ) {
        	_SmsBroadCast.updateLocation( location.getLatitude(), location.getLongitude() );
        }*/
    }

}