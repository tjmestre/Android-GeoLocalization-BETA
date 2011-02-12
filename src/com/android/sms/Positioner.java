package com.android.sms;

import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.LinkedList;


public class Positioner implements LocationListener {
	private Location currentLocation;
	private GeoPoint currentGeoPoint;
	private float currentAccuracy;
	private LinkedList<OnPositionChangeListener> toNotify;
	private LocationManager locationManager;
	private boolean isUpdating;
	private boolean hadGPSFix;
	private boolean isFixedLocation;
	private boolean isGPSFix;
	Context c;
	
	public Positioner(Context context) {
		toNotify = new LinkedList<OnPositionChangeListener>();
		locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		isUpdating = false;
		hadGPSFix = false;
		isFixedLocation = false;
		isGPSFix = false;
		c =  context;
		initLocation();
		requestUpdates();
	}
	
	private void initLocation() {
		if (!isLocalizationActive()) return;
		Location loc_net = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		Location loc_gps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		Location loc = loc_gps == null ? loc_net : loc_gps;
		

		if (loc == null) return;
		
		
		
		currentLocation = loc_net;
		currentGeoPoint = new GeoPoint((int)(loc_net.getLatitude() * 1E6), 
				(int)(loc_net.getLongitude() * 1E6));
		currentAccuracy = loc_net.getAccuracy();
		notifyAllClients();
	}
	
	private String getBestLocationProvider() {
		if (!isLocalizationActive())
			return LocationManager.GPS_PROVIDER;
		else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
				return LocationManager.GPS_PROVIDER;
		else return LocationManager.NETWORK_PROVIDER;
	}
	
	public void requestUpdates() {
		if (isUpdating) return;

		if (getBestLocationProvider().equals(LocationManager.GPS_PROVIDER) && !hadGPSFix){
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		locationManager.requestLocationUpdates(getBestLocationProvider(), 0, 0, this);
	
		}
		
		if(getBestLocationProvider().equals(LocationManager.NETWORK_PROVIDER)){
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
			locationManager.requestLocationUpdates(getBestLocationProvider(), 0, 0, this);
		}
	
		isUpdating = true;
	}
	
	public void cancelUpdates() {
		if (!isUpdating) return;

		locationManager.removeUpdates(this);
		
		isUpdating = false;
	}
	
	public boolean isLocalizationActive() {
    	return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || 
    		locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}
	
	public void registerNotification(OnPositionChangeListener object) {
		toNotify.add(object);
	}
	
	public void unregisterNotification(OnPositionChangeListener object) {
		toNotify.remove(object);
	}
	
	
	
	public Location getLastLocation() {
		return currentLocation;
	}
	
	public GeoPoint getLastGeoPoint() {
		return currentGeoPoint;
	}
	
	public float getLastAccuracy() {
		return currentAccuracy;
	}

	@Override
	public synchronized void onLocationChanged(Location location) {
		/* If we already have a GPS fix, discard Network locations */
		if (hadGPSFix && location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) 
			return;
		
		/* Save location */
		currentLocation = location;
		currentGeoPoint = new GeoPoint((int)(location.getLatitude() * 1E6), 
				(int)(location.getLongitude() * 1E6));
		currentAccuracy = location.getAccuracy();
		
		/* Did it come from GPS or Network? */
		if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
			hadGPSFix = true;
			isGPSFix = true;
		} else {
			hadGPSFix = false;
			isGPSFix = false;
		}

		/* Notify the clients if there is any */
		if (toNotify.size() > 0)
			notifyAllClients();
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
		initLocation();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	
	private synchronized void notifyAllClients() {
		for (OnPositionChangeListener l : toNotify)
			l.onLocationChange(currentGeoPoint, currentAccuracy);
	}
	
	public synchronized void setFixedLocation(Location location) {
		if (!isFixedLocation) {
			cancelUpdates();
			isFixedLocation = true;
		}
		onLocationChanged(location);
	}
	
	public void cancelFixedLocation() {
		if (!isFixedLocation) return;
		hadGPSFix = false;
		requestUpdates();
		isFixedLocation = false;
	}
	
	public boolean isFixedLocation() {
		return isFixedLocation;
	}
	
	public boolean isGPSFix() {
		return isGPSFix;
	}
}
