package com.android.sms;



import java.util.HashMap;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.provider.ContactsContract;


public class Sms extends Activity {
    /** Called when the activity is first created. */
	
	Button btnsendMsg;
//	EditText txtPhonenumb;
	EditText txtMessage;
	String phoneNumber;
	 AutoCompleteTextView txtPhonenumb;
	 Cursor cursor;
	 SimpleCursorAdapter myAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
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
				//	Toast.makeText(getApplicationContext(), "Insira o numero e a mensagem", Toast.LENGTH_LONG).show();
					  Intent i = new Intent(Sms.this, com.android.sms.maps.class);
			           startActivity(i);
				}
				
			}
		});
        
        ContentResolver content = getContentResolver();
        Cursor cursor = content.query(ContactsContract.Contacts.CONTENT_URI,
                PEOPLE_PROJECTION, null, null, null);
        Autocomplete.ContactListAdapter adapter =
                new Autocomplete.ContactListAdapter(this, cursor);

        AutoCompleteTextView textView = (AutoCompleteTextView)
                findViewById(R.id.txtPhoneNo);
       
       

        textView.setThreshold(1);
        textView.setAdapter(adapter);
        
       
        
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