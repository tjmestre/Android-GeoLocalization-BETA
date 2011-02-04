package com.android.sms;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;



import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.jsambells.directions.StepAbstract;
import com.jsambells.directions.LegAbstract;
import com.jsambells.directions.google.DirectionsAPI;
import com.jsambells.directions.google.DirectionsAPIStep;
import com.jsambells.directions.google.DirectionsAPILeg;
import com.jsambells.directions.google.teste;


public class InstructionsList extends Activity {
	
	ListView lv1;
	TextView tv1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_item);

		lv1 = (ListView)findViewById(R.id.listview);
		tv1 = (TextView)findViewById(R.id.tvLocation);
		
		
		 List<DirectionsAPIStep> allPharm = teste.getInstructions();
	
		 DirectionsAPILeg a = new DirectionsAPILeg();
		 
		 tv1.setText(a.getEndAddress());
		 
		 ArrayList<HashMap<String, String>> pharmacies = new ArrayList<HashMap<String, String>>();
			for (DirectionsAPIStep p : allPharm) {
				HashMap<String, String> pharmacy = new HashMap<String, String>();
			//	pharmacy.put("name", p.getDistance());
				pharmacy.put("distance", p.getDistance());
				pharmacy.put("duration", p.getDuration());
				pharmacy.put("instructions", Html.fromHtml(p.getInstructions()).toString());
				//Log.d("dfedf",p.getInstructions());
				pharmacies.add(pharmacy);
			}

			
			String[] from = new String[] {"instructions","duration","distance"};
			int[] to = new int[] { R.id.txtTexto, R.id.tvDuracao,R.id.tvDistancia};
			
			  SimpleAdapter adapter = new SimpleAdapter(this, pharmacies, R.layout.list_row, from, to);
	
			          lv1.setAdapter(adapter);

			
			/*SimpleAdapter adapter = new SimpleAdapter(this, pharmacies, R.layout.list_item,
	                new String[] {"instructions"},
	                new int[] {R.id.txtTexto}
	        );*/

	//setListAdapter(adapter);
			
		//	lv1.setAdapter(adapter);
			//Log.d("ahaha",pharmacies.toString());
		
		//Log.d("aaaaaaaaaa",instruct.getInstructions().toString());
		//ArrayList<String> t = new ArrayList<String>();
		//Log.d("LISTA:",instruct.getInstructions().toString());
		
			
		
		//setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, pharmacies));
	};

}
