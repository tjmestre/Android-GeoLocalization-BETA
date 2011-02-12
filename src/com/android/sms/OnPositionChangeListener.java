package com.android.sms;

import com.google.android.maps.GeoPoint;

public interface OnPositionChangeListener {
	void onLocationChange(GeoPoint currentPos, float accuracy);
}
