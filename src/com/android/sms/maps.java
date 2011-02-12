package com.android.sms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import android.content.Context;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.jsambells.directions.RouteAbstract;
import com.jsambells.directions.ParserAbstract.Mode;
import com.jsambells.directions.RouteAbstract.RoutePathSmoothness;
import com.jsambells.directions.google.DirectionsAPI;
import com.jsambells.directions.google.DirectionsAPIRoute;

public class maps extends MapActivity implements com.jsambells.directions.ParserAbstract.IDirectionsListener, OnPositionChangeListener {
	
	protected MapView map;
	int maxLatitude;
	int minLatitude;
	int maxLongitude;
	int minLongitude;
	double hpadding = 0.1;
	double vpadding = 0.2;	
	Drawable drawable;
	ItemOverlay itemizedOverlay;
	Positioner localization;
	private final static int MENU_NAVIGATE = 2;
	private final static int MENU_WIFI_GPS= 3;
	List<GeoPoint> waypoints;
	List<Overlay> mapOverlays;

	TextView txt;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);
        
        txt = (TextView)findViewById(R.id.txtTexto);
        
        
        
        map = (MapView)findViewById(R.id.main);
     
        map.setBuiltInZoomControls(true);
        
        mapOverlays = map.getOverlays();
        
        drawable = this.getResources().getDrawable(R.drawable.icon);
        itemizedOverlay = new ItemOverlay(drawable);
        
        checkLocation();
        
		// Find a route
		waypoints = new ArrayList<GeoPoint>();
		 double fromLat = 38.67928, fromLon = -9.31932, toLat = 38.61994, toLon = -9.11321;
		 
		//double fromLat = 38.67928, fromLon = -9.31932, toLat = GpsDataLocation.getTOlatitude(), toLon = GpsDataLocation.getTOlatitude();
		
		 int latitude = (int)(fromLat*1e6);
		 int longitude = (int)(fromLon *1e6);
		 
		// int latitude = (int) (GpsDataLocation.getFROMlatitude() * 1e6);
		// int longitude = (int) (GpsDataLocation.getFROMlongitude() * 1e6);
		 int tolatitude = (int)(toLat * 1e6);
		 int toLongetitude = (int)(toLon * 1e6);
    
		waypoints.add(new GeoPoint(latitude,longitude)); // Inicio
		
		OverlayItem overlayitem = new OverlayItem(new GeoPoint(latitude,longitude), "", "");
		itemizedOverlay.addOverlay(overlayitem);
		
		
		waypoints.add(new GeoPoint(tolatitude,toLongetitude));
		OverlayItem overlayitem2 = new OverlayItem(new GeoPoint(tolatitude,toLongetitude), "", "");
		itemizedOverlay.addOverlay(overlayitem2);
		 mapOverlays.add(itemizedOverlay);
		// Fim
		//waypoints.add(new GeoPoint(37802341,-122405811));
		
		DirectionsAPI directions = new DirectionsAPI();
		directions.getDirectionsThruWaypoints(
			waypoints, 
			DirectionsAPI.Mode.DRIVING,
			
			this,
			this
		);
		
			
		
		this.setMapBoundsToPois(waypoints, hpadding, vpadding);
		
	
		
		/*for (GeoPoint item : waypoints) {
            int lat = item.getLatitudeE6(); 
            int lon = item.getLongitudeE6();

            maxLatitude = Math.max(lat, maxLatitude);
            minLatitude = Math.min(lat, minLatitude);
            maxLongitude = Math.max(lon, maxLongitude);
            minLongitude = Math.min(lon, minLongitude);
        }

		
		
		
		maxLatitude = maxLatitude + (int)((maxLatitude-minLatitude)*hpadding);
        minLatitude = minLatitude - (int)((maxLatitude-minLatitude)*hpadding);

        maxLongitude = maxLongitude + (int)((maxLongitude-minLongitude)*vpadding);
        minLongitude = minLongitude - (int)((maxLongitude-minLongitude)*vpadding);
        
        // Calculate the lat, lon spans from the given pois and zoom
        //map.getController().zoomToSpan(Math.abs(maxLatitude - minLatitude), Math.abs(maxLongitude - minLongitude));
        map.getController().zoomToSpan(100000, 100000); 
        map.setBuiltInZoomControls(true);
        // Animate to the center of the cluster of points
        map.getController().animateTo(new GeoPoint(
              (maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2));
        
       // map.getController().setZoom(12);*/
		

    }

	/* MapActivity */
    
    
    public void checkLocation(){
    	
    	
    	LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        
        
        //Var procurar qual é que é o melhor provider de Network se Gps
        Criteria locationCritera = new Criteria();
        locationCritera.setAccuracy(Criteria.ACCURACY_FINE);
        locationCritera.setAltitudeRequired(false);
        locationCritera.setBearingRequired(false);
        locationCritera.setCostAllowed(true);
        locationCritera.setPowerRequirement(Criteria.NO_REQUIREMENT);

        String providerName = locationManager.getBestProvider(locationCritera, true);

        if (providerName != null && locationManager.isProviderEnabled(providerName)) {
            Log.d("adf","ENABLE PROVIDER");
            locationManager.requestLocationUpdates(providerName, 20000, 100, networkLocationListener);
            
            try{
            	
            	if(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null){
		            GpsDataLocation.setFROMlatitude(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude());
		            GpsDataLocation.setFROMlongitude(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude());
            	}else if(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!= null){
            		GpsDataLocation.setFROMlatitude(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude());
		            GpsDataLocation.setFROMlongitude(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude());            		
            	}
            }catch (Exception e) {
				// TODO: handle exception
			}
            
            
            if(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) == null && locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) == null ) {
            	Intent a = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				 a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(a);
            }
        
        } else {
            // Provider not enabled, prompt user to enable it
            Toast.makeText(getApplicationContext(), "NOT" ,Toast.LENGTH_LONG).show();
            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(myIntent);
        }
    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_NAVIGATE,0,"Percurso"); 
        menu.add(1, MENU_WIFI_GPS,0,"Refresh Localização"); 
    	return true;
    }
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	       
	        case MENU_NAVIGATE:
	        //	openNavigation();
	        	Intent i = new Intent(getApplicationContext(), InstructionsList.class);
	            startActivity(i);
	        	break;
	        case MENU_WIFI_GPS:
	        	checkLocation();
    			break;
	    }
        	
        return true;
    }
	
	    @Override
	    public void onResume() {
	    	//localization.enableMyLocation();
	    	//localization.registerNotification(this);
	    	//localization.requestUpdates();
	    	super.onResume();
	    	
	    //	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, networkLocationListener);
	    	
	    }
	    
	    @Override
	    protected void onPause() {
	    	// TODO Auto-generated method stub
	    	super.onPause();
	    
	    }
	
    @Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	/* IDirectionsListener */
    
	public void onDirectionsAvailable(RouteAbstract route, Mode mode) {
		// Add it to a map
		
		// Add a directions overlay to the map.
		// This is just a quick example to draw the line on the map.
		DirectionsOverlay directions = new DirectionsOverlay();
		directions.setRoute((DirectionsAPIRoute)route);
		map.getOverlays().add(directions);
		map.requestLayout();
		
	}
	public void onDirectionsNotAvailable() {
		// TODO Auto-generated method stub
		// Show an error?
	}
	
	public class DirectionsOverlay extends Overlay {

		static final String TAG = "DirectionsOverlay";

		// The route to draw
		private DirectionsAPIRoute mRoute;

		// Our Paint
		Paint pathPaint = new Paint();
		
		public DirectionsOverlay() {
			this.pathPaint.setAntiAlias(true);
		}

		public DirectionsOverlay setRoute( DirectionsAPIRoute route) {
			this.mRoute = route;
			return this;
		}
		
		// This function does some fancy drawing
		public void draw(android.graphics.Canvas canvas, MapView mapView, boolean shadow) {
			
			// This method will be called twice. Once in the 
			// shadow phase, skewed and darkened, then again in 
			// the non-shadow phase. 
			
			if (this.mRoute != null && !shadow) {

				Path thePath = new Path();

				/* Reset our paint. */
				this.pathPaint.setStrokeWidth(3);
				this.pathPaint.setARGB(200, 255, 0, 0);
				// holders of mapped coords...
				Point screen = new Point();

				/* This drawing code needs some work to filter the path to the portion 
				 * that is on the screen. The FINE line really slows things down
				 */
				
				RoutePathSmoothness smoothenss = (mapView.getZoomLevel() < 5) ? RoutePathSmoothness.ROUGH : RoutePathSmoothness.FINE;
				List<GeoPoint> drawPoints = mRoute.getGeoPointPath(smoothenss);
				
				Iterator<GeoPoint> itr = drawPoints.listIterator();

				if (itr.hasNext()) { 
					// convert the start point.
					mapView.getProjection().toPixels( (GeoPoint)itr.next(), screen );
					thePath.moveTo(screen.x, screen.y);
					
					while( itr.hasNext() ) {
						GeoPoint p = (GeoPoint)itr.next();
						map.getProjection().toPixels( p, screen);
					    thePath.lineTo(screen.x, screen.y);
					}
					
					this.pathPaint.setStyle(Paint.Style.STROKE);
					canvas.drawPath(thePath, this.pathPaint);
				}

			}

			super.draw(canvas, mapView, shadow);
			
		}
		
	}
	
	
	public void setMapBoundsToPois(List<GeoPoint> items, double hpadding, double vpadding) {

	    MapController mapController = map.getController();
	    // If there is only on one result
	    // directly animate to that location

	    if (items.size() == 1) { // animate to the location
	        mapController.animateTo(items.get(0));
	    } else {
	        // find the lat, lon span
	        int minLatitude = Integer.MAX_VALUE;
	        int maxLatitude = Integer.MIN_VALUE;
	        int minLongitude = Integer.MAX_VALUE;
	        int maxLongitude = Integer.MIN_VALUE;

	        // Find the boundaries of the item set
	        for (GeoPoint item : items) {
	            int lat = item.getLatitudeE6(); int lon = item.getLongitudeE6();

	            maxLatitude = Math.max(lat, maxLatitude);
	            minLatitude = Math.min(lat, minLatitude);
	            maxLongitude = Math.max(lon, maxLongitude);
	            minLongitude = Math.min(lon, minLongitude);
	        }

	        // leave some padding from corners
	        // such as 0.1 for hpadding and 0.2 for vpadding
	        maxLatitude = maxLatitude + (int)((maxLatitude-minLatitude)*hpadding);
	        minLatitude = minLatitude - (int)((maxLatitude-minLatitude)*hpadding);

	        maxLongitude = maxLongitude + (int)((maxLongitude-minLongitude)*vpadding);
	        minLongitude = minLongitude - (int)((maxLongitude-minLongitude)*vpadding);

	        // Calculate the lat, lon spans from the given pois and zoom
	        mapController.zoomToSpan(Math.abs(maxLatitude - minLatitude), Math
	.abs(maxLongitude - minLongitude));

	        // Animate to the center of the cluster of points
	        mapController.animateTo(new GeoPoint(
	              (maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2));
	    }
	}

	@Override
	public void onLocationChange(GeoPoint currentPos, float accuracy) {
		// TODO Auto-generated method stub
		
	}

}