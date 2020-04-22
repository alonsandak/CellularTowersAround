package com.webapps.cell;
 

 


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
 
 
public class WebLauncherActivity extends Activity {
    /** Called when the activity is first created. */

	  
	// Splash screen timer
		private static int SPLASH_TIME_OUT = 2000;	

	 
   @Override
   public void onCreate(Bundle savedInstanceState) {
	   
	   final Context context = this;
   
   	
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_splash);
        
           
  // check intrnet connectivity 
       
           if (isOnline()==true ) 
           {
        	   
        	   
        	   new Handler().postDelayed(new Runnable() {

       			/*
       			 * Showing splash screen with a timer. This will be useful when you
       			 * want to show case your app logo / company
       			 */

       			@Override
       			public void run() {
       				// This method will be executed once the timer is over
       				// Start your app main activity
    				Intent intent = new Intent (context,com.webapps.cell.WebMapActivity.class);
       				startActivity(intent);

       				// close this activity
       				finish();
       				}
        	   	}, SPLASH_TIME_OUT);
          
             
    
           }
           else
           {

   			// Alert 
   			
   			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
   					WebLauncherActivity.this);
   			// set title
   			alertDialogBuilder.setTitle("NO INTERNET CONNECTION  ");
   			// set dialog message
   			alertDialogBuilder
   				.setMessage("PLEASE TRY LATER " )
   				.setCancelable(false) 
   				.setPositiveButton("ok",new DialogInterface.OnClickListener() {
   					public void onClick(DialogInterface dialog,int id) {
   						// if this button is clicked, close dialog
   					 
   						dialog.cancel();
   						finish();
   					}
   				  });
   			//create the dialog 
   			AlertDialog alertdialog = alertDialogBuilder.create();
   			//show the dialog 
   			alertdialog.show();
           }
           
            
    }
       
   public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

	    return cm.getActiveNetworkInfo() != null && 
	       cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}
    
   
  
}
