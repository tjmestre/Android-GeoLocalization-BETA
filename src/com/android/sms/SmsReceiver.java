package com.android.sms;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {

	/** TAG used for Debug-Logging */
	private static final String LOG_TAG = "MySmsReceiver";
	private static int SmsReturn = 1;
	private LocationManager _locationManager;
	private static double latitude;
	private static String phoneNumber; 

	Location lastKnownLocation;

	public static double getLatitude() {
		return latitude;
	}

	public static void setLatitude(double latitude) {
		SmsReceiver.latitude = latitude;
	}

	public static double getLongitude() {
		return longitude;
	}

	public static void setLongitude(double longitude) {
		SmsReceiver.longitude = longitude;
	}

	private static double longitude;
	private final LocationListener _listener = new LocationListenerImpl(this);
	
	public static void setPhoneNumber(String phoneNumber) {
		SmsReceiver.phoneNumber = phoneNumber;
	}

	public static String getPhoneNumber() {
		return phoneNumber;
	}
	

	

	/**
	 * The Action fired by the Android-System when a SMS was received. We are
	 * using the Default Package-Visibility
	 */

	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

	@Override
	public void onReceive(final Context context, Intent intent) {
		
		
		

		if (intent.getAction().equals(ACTION)) {
			// if(message starts with SMStretcher recognize BYTE)
			final StringBuilder sb = new StringBuilder();

			/* The SMS-Messages are 'hiding' within the extras of the Intent. */
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				/*
				 * Get all messages contained in the Intent
				 * Telephony.Sms.Intents.getMessagesFromIntent(intent) does not
				 * work anymore
				 */
				Object[] pduObj = (Object[]) bundle.get("pdus");
				SmsMessage[] messages = new SmsMessage[pduObj.length];
				for (int i = 0; i < pduObj.length; i++)
					messages[i] = SmsMessage.createFromPdu((byte[]) pduObj[i]);
				/* Feed the StringBuilder with all Messages found. */
				for (SmsMessage currentMessage : messages) {
					sb.append("SMS Received From: ");
					/* Sender-Number */
					sb.append(currentMessage.getDisplayOriginatingAddress());
						
					SmsReceiver.setPhoneNumber(currentMessage.getDisplayOriginatingAddress());
					sb.append("\nMessage : ");
					/* Actual Message-Content */

					// See if the word is on the sms.
					if (currentMessage.getDisplayMessageBody().contains("gpslocation")) {

						// Toast.makeText(context, "teste",
						// Toast.LENGTH_SHORT).show();
						AlertDialog.Builder builder;

						builder = new AlertDialog.Builder(context
								.getApplicationContext());

						builder
								.setMessage("Aceita enviar os seus dados de gps para o numero: "
										+ currentMessage
												.getDisplayOriginatingAddress());
						builder.setCancelable(false);

						builder.setPositiveButton("Sim",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int id) {

										try {
											SmsReceiver.this.finalize();
											SmsReceiver.this.SmsYes(context, sb);
										} catch (Throwable e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

									}

									
								});

						builder.setNegativeButton("Não",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();

									}
								});
						try {
							AlertDialog alert = builder.create();

							alert.getWindow()
								 .setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
							
							alert.show();
						} catch (Exception e) {

							Log.d("ERROR", e.toString());
							// TODO: handle exception
						}
						SmsReturn = 2;
					} else {
						SmsReturn = 1;
					}

					sb.append(currentMessage.getDisplayMessageBody());
				}
			}
			/* Logger Debug-Output */
			Log.i(LOG_TAG, "[SMSApp] onReceive: " + sb);

		}


	}

	private void sendSmsMessage(String address, String message)
			throws Exception {
		SmsManager smsMgr = SmsManager.getDefault();
		smsMgr.sendTextMessage(address, null, message, null, null);

	}

	private void registerLocationListener() {
		_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000, 20, _listener);
	}

	private void useLastKnownLocation(final LocationManager manager,
			Context Context) {
		lastKnownLocation = manager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (lastKnownLocation == null) {
			// lastKnownLocation = manager.getLastKnownLocation(
			// LocationManager.NETWORK_PROVIDER );

			try {
				SmsReceiver.setLatitude(lastKnownLocation.getLatitude());
				SmsReceiver.setLongitude(lastKnownLocation.getLongitude());

			} catch (NullPointerException e) {
				Toast.makeText(Context, "Gps turned off", Toast.LENGTH_SHORT)
						.show();
					
			}

		}
		if (lastKnownLocation != null) {
			// latitude = lastKnownLocation.getLatitude();
			SmsReceiver.setLatitude(lastKnownLocation.getLatitude());
			SmsReceiver.setLongitude(lastKnownLocation.getLongitude());
		}
	}
	
	public void SmsYes(Context context, StringBuilder sb){

		/* Show the Notification containing the Message. */
		Toast.makeText(context, sb.toString(), Toast.LENGTH_SHORT)
				.show();
		/* Start the Main-Activity */
		// Intent i = new Intent(context, LocationSms.class);
		// context.startActivity(i);

		// Location loc =
		// _locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		_locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (!_locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Toast.makeText(context, "GPS Provider not enable",
					Toast.LENGTH_SHORT).show();

		} else {

			_locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			// Location loc =
			// _locationManager.getLastKnownLocation("gps");
			_locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 1000, 20, _listener);

			registerLocationListener();

			if (!_locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				// buildAlertMessageNoGps();
			}

			useLastKnownLocation(_locationManager, context);

		}

		try {
			sendSmsMessage(SmsReceiver.getPhoneNumber(), SmsReceiver.getLatitude() + " : "
					+ SmsReceiver.getLongitude());

			Toast.makeText(context, "SMS Sent", Toast.LENGTH_SHORT)
					.show();

		} catch (Exception e) {
			Toast.makeText(context, e.getStackTrace().toString(),
					Toast.LENGTH_LONG).show();
			Log.d("DEBUG", e.toString());
		}
	

	}
	
	public void SmsNo(){
		try {
			sendSmsMessage(SmsReceiver.getPhoneNumber(),"De momento não é possiel");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

}
