package com.android.sms;

import com.google.android.maps.GeoPoint;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver implements OnPositionChangeListener {

	/** TAG used for Debug-Logging */
	private static final String LOG_TAG = "MySmsReceiver";
	private static int SmsReturn = 1;
	private LocationManager _locationManager;
	private static double latitude;
	private static String phoneNumber; 
	String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
	 PendingIntent sentPI;
	 PendingIntent deliverPI;

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
	

	public void checkLocation(Context context){
    	
    	
		_locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        
        
        //Var procurar qual é que é o melhor provider de Network se Gps
        Criteria locationCritera = new Criteria();
        locationCritera.setAccuracy(Criteria.ACCURACY_FINE);
        locationCritera.setAltitudeRequired(false);
        locationCritera.setBearingRequired(false);
        locationCritera.setCostAllowed(true);
        locationCritera.setPowerRequirement(Criteria.NO_REQUIREMENT);

        String providerName = _locationManager.getBestProvider(locationCritera, true);

        if (providerName != null && _locationManager.isProviderEnabled(providerName)) {
            Log.d("adf","ENABLE PROVIDER");
            _locationManager.requestLocationUpdates(providerName, 20000, 100, networkLocationListener);
            
            
        	//GpsDataLocation.setTOlatitude(_locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude());
            //GpsDataLocation.setTOlongitude(_locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude());            		
            try{
            	
            	if(_locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null){
		            SmsReceiver.setLatitude(_locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude());
		            SmsReceiver.setLongitude(_locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude());
            	}else if(_locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!= null){
            		SmsReceiver.setLatitude(_locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude());
		            SmsReceiver.setLongitude(_locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude());            		
            	}
            }catch (Exception e) {
				// TODO: handle exception
			}
            
            
            if(_locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) == null && _locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) == null ) {
            	Intent a = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				 a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(a);
            }
        
        } else {
            // Provider not enabled, prompt user to enable it
            Toast.makeText(context.getApplicationContext(), "NOT" ,Toast.LENGTH_LONG).show();
            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(myIntent);
        }
    	
    }

	/**
	 * The Action fired by the Android-System when a SMS was received. We are
	 * using the Default Package-Visibility
	 */

	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

	@Override
	public void onReceive(final Context context, Intent intent) {
		
		 sentPI= PendingIntent.getBroadcast(context, 0, new Intent(SENT),PendingIntent.FLAG_UPDATE_CURRENT);
		 deliverPI= PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED),PendingIntent.FLAG_UPDATE_CURRENT);

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
					if(currentMessage.getDisplayMessageBody().contains("gpsdata")){
						
						String content = currentMessage.getDisplayMessageBody().replace("gpsdata", "");
						String[] list  = content.split(":");
						
						
						GpsDataLocation.setTOlatitude(Double.parseDouble(list[0]));
						GpsDataLocation.setTOlongitude(Double.parseDouble(list[1]));
						
					    Intent visualizeInfo = new Intent(context,maps.class);
						
					
					    visualizeInfo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(visualizeInfo);
					}
					
					
					
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
										SmsReceiver.this.SmsNo();
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
		smsMgr.sendTextMessage(address, null, message, sentPI, deliverPI);

	}

	
	
	public String SmsYes(Context context, StringBuilder sb){

		/* Show the Notification containing the Message. */
		Toast.makeText(context, sb.toString(), Toast.LENGTH_SHORT)
				.show();
		/* Start the Main-Activity */
		// Intent i = new Intent(context, LocationSms.class);
		// context.startActivity(i);

		// Location loc =
		// _locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		
		checkLocation(context);

			
		Log.d("LATITUDE", Double.toString(SmsReceiver.getLatitude()));
		Log.d("Longitude", Double.toString(SmsReceiver.getLongitude()));
		

		try {
			sendSmsMessage(SmsReceiver.getPhoneNumber(), "gpsdata"+ SmsReceiver.getLatitude()+ ":"
					+ SmsReceiver.getLongitude());

			Toast.makeText(context, "SMS Sent", Toast.LENGTH_SHORT)
					.show();

		} catch (Exception e) {
			Toast.makeText(context, e.getStackTrace().toString(),
					Toast.LENGTH_LONG).show();
			Log.d("DEBUG", e.toString());
		}
	
		return null;
	}
	
	public void SmsNo(){
		try {
			sendSmsMessage(SmsReceiver.getPhoneNumber(), "De momento nao e possivel receber os dados do gps");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private final LocationListener networkLocationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
         
        }

        @Override
        public void onProviderEnabled(String provider) {
        

        }

        @Override
        public void onProviderDisabled(String provider) {
         
        }

        @Override
        public void onLocationChanged(Location location) {
        

        }

    };

	@Override
	public void onLocationChange(GeoPoint currentPos, float accuracy) {
		// TODO Auto-generated method stub
		
	}
	

}
