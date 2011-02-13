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
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
	ScrollTextView tv1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_item);

		lv1 = (ListView)findViewById(R.id.listview);
	//	tv1 = (TextView)findViewById(R.id.tvLocation);
		tv1 = (ScrollTextView) findViewById(R.id.tvLocation);
	
		tv1.setRndDuration(10000);
		
		 List<DirectionsAPIStep> allPharm = teste.getInstructions();
		 
		 
		 runOnUiThread(new Runnable() {
				@Override
				public void run() {
					tv1.setText("Destino:  "+ DirectionsAPILeg.getEndAddress());
			    	tv1.startScroll();
			    
				}
			});
		 
		// DirectionsAPILeg a = new DirectionsAPILeg();
	
		
		 
		 
		 ArrayList<HashMap<String, String>> percursos = new ArrayList<HashMap<String, String>>();
			for (DirectionsAPIStep p : allPharm) {
				HashMap<String, String> percurso = new HashMap<String, String>();
			
				percurso.put("distance", "Distancia: " + p.getDistance());
				percurso.put("duration", "Duracao: " +p.getDuration());
				percurso.put("instructions", Html.fromHtml(p.getInstructions()).toString());
			
				//Log.d("dfedf",p.getInstructions());
				percursos.add(percurso);
			}
			
			
			
			String[] from = new String[] {"instructions","duration","distance"};
			int[] to = new int[] { R.id.txtTexto, R.id.tvDuracao,R.id.tvDistancia};
			
			  SimpleAdapter adapter = new SimpleAdapter(this, percursos, R.layout.list_row, from, to);
	
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
