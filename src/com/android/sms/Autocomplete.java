package com.android.sms;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.TextView;

	
	public class Autocomplete extends Activity {
		
		
	    /** Called when the activity is first created. */
		AutoCompleteTextView textView;
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.main);
	        
	        ContentResolver content = getContentResolver();
	        Cursor cursor = content.query(ContactsContract.Contacts.CONTENT_URI,
	                PEOPLE_PROJECTION, null, null, null);
	        ContactListAdapter adapter = new ContactListAdapter(this, cursor);

	        textView  = (AutoCompleteTextView)
	                findViewById(R.id.txtPhoneNo);
	        textView.setThreshold(1);
	        textView.setAdapter(adapter);
	      
	    }
	    
	    
	    public static class ContactListAdapter extends CursorAdapter implements Filterable {
	    
	    	String n;
	    	String id;
	    	String texto;
	        public ContactListAdapter(Context context, Cursor c) {
	            super(context, c);
	            mContent = context.getContentResolver();
	        }

	        @Override
	        public View newView(Context context, Cursor cursor, ViewGroup parent) {
	            final LayoutInflater inflater = LayoutInflater.from(context);
	            final TextView view = (TextView) inflater.inflate(
	                    android.R.layout.simple_dropdown_item_1line, parent, false);
	            
	         //   int numb =    cursor.getColumnIndex(Phone.NUMBER);
	           // number = cursor.getString(numb);
	            //SetPhoneNumber(number);
	            
	            id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
	            
	            this.texto = this.getPhoneNumbers(id);
	            view.setText(cursor.getString(3));
	            return view;
	        }

	        @Override
	        public void bindView(View view, Context context, Cursor cursor) {
	            ((TextView) view).setText(cursor.getString(3));
	        }

	        @Override
	        public String convertToString(Cursor cursor) {
	            return this.texto;
	        }
	        
	      

	        @Override
	        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
	            if (getFilterQueryProvider() != null) {
	                return getFilterQueryProvider().runQuery(constraint);
	            }

	            StringBuilder buffer = null;
	            String[] args = null;
	            if (constraint != null) {
	                buffer = new StringBuilder();
	                buffer.append("UPPER(");
	                buffer.append(ContactsContract.Contacts.DISPLAY_NAME);
	                buffer.append(") LIKE ?");
	               
	                args = new String[] { "%"+constraint.toString().toUpperCase()+"%"};
	                Log.d("CRL",args[0]);
	            }

	            return mContent.query(ContactsContract.Contacts.CONTENT_URI, PEOPLE_PROJECTION,
	                    buffer == null ? null : buffer.toString(), args,
	                    null);
	        }
	        
	        
	        public String getPhoneNumbers(String id) {
		 		ArrayList<Phone> phones = new ArrayList<Phone>();
		 		
		 		Cursor pCur = this.mContent.query(
		 				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
		 				null, 
		 				ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
		 				new String[]{id}, null);
		 		while (pCur.moveToNext()) {
		 			
		 				 n = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		 				
		 			
		 
		 		} 
		 		pCur.close();
		 		return(n);
		 	}

	        private ContentResolver mContent;        
	    }
	    
	  
	    
	    
	    private static final String[] PEOPLE_PROJECTION = new String[] {
	    	ContactsContract.Contacts._ID,
	    	ContactsContract.Contacts.CONTACT_STATUS_LABEL,
	        ContactsContract.Contacts.HAS_PHONE_NUMBER,
	        ContactsContract.Contacts.DISPLAY_NAME	        
	        
	    };
	}

