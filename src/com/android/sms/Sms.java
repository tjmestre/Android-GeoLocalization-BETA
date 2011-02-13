package com.android.sms;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.ContactsContract;
import android.provider.Settings;


public class Sms extends Activity {
    /** Called when the activity is first created. */
	
	Button btnsendMsg;
//	EditText txtPhonenumb;
	EditText txtMessage;
	String phoneNumber;
	 AutoCompleteTextView txtPhonenumb;
	 Cursor cursor;
	 SimpleCursorAdapter myAdapter;
	 private final static int MENU_ABOUT = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
       
        AlertDialog.Builder builder;
        
      
		builder = new AlertDialog.Builder(Sms.this);

		builder
				.setMessage("Para um melhor funcionamento da aplicação, é aconselhado que active o gps ou o wifi-fi. Pois a aplicação necessita de aceder à sua localização." +
		        		"Deseja fazer agora? \n Se já estiver activado uma dessas opções pode cancelar.");
		builder.setCancelable(false);

		builder.setPositiveButton("Sim",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int id) {

						try {
							
							Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				            startActivity(myIntent);
						
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
		alert.show();
		}catch (Exception e) {
			// TODO: handle exception
		}
        
        btnsendMsg = (Button)findViewById(R.id.btnSendSMS);
        txtPhonenumb = (AutoCompleteTextView)findViewById(R.id.txtPhoneNo);
       // txtMessage = (EditText)findViewById(R.id.txtMessage);
      //   textView = (AutoCompleteTextView)  findViewById(R.id.txtMessage);

         
        // ContentResolver content = getContentResolver();
         
        
         
        btnsendMsg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String phonen = txtPhonenumb.getText().toString();
				String phoneMessage = "gpslocation";
				
				if(phonen.length() > 0 && phoneMessage.length() > 0){
					sendSms(phonen,phoneMessage);
				}else{
					//Toast.makeText(getApplicationContext(), "Insira o numero ", Toast.LENGTH_LONG).show();
					  Intent i = new Intent(Sms.this, com.android.sms.maps.class);
			          startActivity(i);
				}
				
			}
		});
        
        
        //Query à base de dados dos contactos
        
        ContentResolver content = getContentResolver();
        Cursor cursor = content.query(ContactsContract.Contacts.CONTENT_URI,
                PEOPLE_PROJECTION, null, null, null);
        Autocomplete.ContactListAdapter adapter =
                new Autocomplete.ContactListAdapter(this, cursor);

        AutoCompleteTextView textView = (AutoCompleteTextView)
                findViewById(R.id.txtPhoneNo);
       
       

        textView.setThreshold(1);
        textView.setAdapter(adapter);
        
      // teste ip = new teste();
       
    }
    
    
    
    public void createAlertDialog(Context context){
    	
    	
			
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_ABOUT,0,"ABOUT"); 
    	return true;
    }
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	       
	        case MENU_ABOUT:
	        	openAbout();
	        	
	        	break;
        }
        return true;
    }
	
	public void openAbout(){
		 final Dialog dialog = new Dialog(Sms.this);
	        dialog.setContentView(R.layout.dialog);
	        dialog.setTitle("About");
	        dialog.setCancelable(true);
	        //there are a lot of settings, for dialog, check them all out!

	        //set up text
	        TextView text = (TextView) dialog.findViewById(R.id.TextView01);
	        text.setText("Aplicação desenvolvida por Tiago Mestre, para a Disciplina de " +
	        		"Introdução ao desenvolvimento de Software de Código Aberto" +
	        		"\n\n " +
	        		"#### Where is Bob? #########\n	"+
	        		"Esta aplicação baseia-se em descobrir onde é que um individuo se encontra, a partir do envio de uma sms,\n"+
	        		"e com a recepção automática das coordenadas gps do individuo a quem se enviou a mensagem.\n"+
	        		"A aplicação tem que estar instalada nos dois dispositivos android com versão igual ou superior a 2.1");


	        //set up button
	        Button button = (Button) dialog.findViewById(R.id.Button01);
	        button.setOnClickListener(new OnClickListener() {
	        @Override
	            public void onClick(View v) {
	              dialog.dismiss();
	            }
	        });
	        //now that the dialog is set up, it's time to show it    
	        dialog.show();
	}
    
    public void sendSms(String phonen, String Message) {
    	
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
 
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
            new Intent(SENT), 0);
      
 
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
            new Intent(DELIVERED), 0);
 
        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off", 
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));
 
      /*  //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;                        
                }
            }
        }, new IntentFilter(DELIVERED));        
 */		Log.d("sdss",phonen);
 
 		
 
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phonen,  null, Message, sentPI, deliveredPI);   
		
	}
    
    private static final String[] PEOPLE_PROJECTION = new String[] {
    	ContactsContract.Contacts.DISPLAY_NAME,
    	ContactsContract.Contacts._ID,
    	ContactsContract.Contacts.HAS_PHONE_NUMBER
    
   };
}