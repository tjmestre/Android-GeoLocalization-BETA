package com.android.sms;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;



import android.app.ListActivity;
import android.text.Html;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.jsambells.directions.StepAbstract;
import com.jsambells.directions.google.DirectionsAPI;
import com.jsambells.directions.google.DirectionsAPIStep;
import com.jsambells.directions.google.teste;


public class InstructionsList extends ListActivity {
	
	ListView lv1;
	
	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		

	
		 List<DirectionsAPIStep> allPharm = teste.getInstructions();
	
		 ArrayList<HashMap<String, String>> pharmacies = new ArrayList<HashMap<String, String>>();
			for (DirectionsAPIStep p : allPharm) {
				HashMap<String, String> pharmacy = new HashMap<String, String>();
				pharmacy.put("name", p.getDistance());
				pharmacy.put("instructions", Html.fromHtml(p.getInstructions()).toString());
				Log.d("dfedf",p.getInstructions());
				pharmacies.add(pharmacy);
			}

			
			SimpleAdapter adapter = new SimpleAdapter(this, pharmacies, R.layout.list_item,
	                new String[] {"instructions"},
	                new int[] {R.id.txtTexto}
	        );

	setListAdapter(adapter);
			//Log.d("ahaha",pharmacies.toString());
		
		//Log.d("aaaaaaaaaa",instruct.getInstructions().toString());
		//ArrayList<String> t = new ArrayList<String>();
		//Log.d("LISTA:",instruct.getInstructions().toString());
		
			
		
		//setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, pharmacies));
	};

}
