package com.webapps.cell;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

//
public class WebMapActivity extends Activity implements LocationListener, EasyPermissions.PermissionCallbacks {

    private WebView webView;
    private Location mostRecentLocation = null;
    private String centerURL;

    @Override
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webmain);


        if (isOnline() == true) {
            getLocation();
            setupWebView();
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



        } else {

            // Alert

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    WebMapActivity.this);
            // set title
            alertDialogBuilder.setTitle("NO INTERNET CONNECTION  ");
            // set dialog message
            alertDialogBuilder
                    .setMessage("PLEASE TRY LATER ")
                    .setCancelable(false)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
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

    //NEW
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    //NEW
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.mailus:
                sendmail("cellaroundus@gmail.com");


                break;
            case R.id.near_me:
                setupCenterUrl(1);

                webView.loadUrl(centerURL);
                break;
            case R.id.about:


                // Alert

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        WebMapActivity.this);
                //	alertDialogBuilder.setContentView(R.layout.cu);
                // set title
                alertDialogBuilder.setTitle(R.string.about_title);
                // set dialog message
                alertDialogBuilder
                        .setMessage(R.string.about_msg)
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close dialog

                                dialog.cancel();

                            }
                        });
                //create the dialog
                AlertDialog alertdialog = alertDialogBuilder.create();
                //show the dialog
                alertdialog.show();

                break;

            default:
                break;
        }

        return true;
    }

    private void setupCenterUrl(int choise) {

        getLocation();

        if (mostRecentLocation == null) {
            centerURL = "javascript:centerMe(-31.000 , -34.000 ,1)";
        } else {
            centerURL = "javascript:centerMe(" +
                    mostRecentLocation.getLatitude() + "," +
                    mostRecentLocation.getLongitude() + "," + choise + ")";
            // Log.i("thready","before setup");


        }


    }

    /** Sets up the WebView object and loads the URL of the page **/

    @SuppressLint("JavascriptInterface")
    public void setupWebView() {

        final Context con = getApplicationContext();

        if (mostRecentLocation == null) {
            centerURL = "javascript:centerMe(-31.000 , -34.000 )";
        } else {
            final String centerURL = "javascript:centerMe(" +
                    mostRecentLocation.getLatitude() + "," +
                    mostRecentLocation.getLongitude() + "," + 1 +")";
            //Log.i("thready","before setup");
        }
        webView = (WebView) findViewById(R.id.webview1);


        webView.getSettings().setJavaScriptEnabled(true);
        /** Allows JavaScript calls to access application resources **/
        webView.addJavascriptInterface(new JavaScriptInterface(this), "Android");

        //Wait for the page to load then send the location information
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                setupCenterUrl(1);
                webView.loadUrl(centerURL);
            }
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                Context con = getApplicationContext();


                if (url.startsWith("http:") || url.startsWith("https:")) {
                    String[] Splits = url.split("//", 2);
                    // Context con= getApplicationContext();
                    //int duration = Toast.LENGTH_LONG;
                    if (Splits[1].startsWith("geo:") || Splits[1].startsWith("tel:")) {
                        url = Splits[1];
                    } else
                        return false;
                }

                ;

                // Otherwise allow the OS to handle it
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        });
        // webView.loadUrl(MAP_URL5);
      //  webView.loadUrl("file:///android_asset/cellulartower.html");
          webView.loadUrl("https://hapatuach.github.io/cellulartower/");


        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(con, "Loading Map  ", duration);
        toast.show();
        toast = Toast.makeText(con, "Please Wait ...", duration);
        toast.show();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        setupCenterUrl(1);

        webView.loadUrl(centerURL);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        new AppSettingsDialog.Builder(this).build().show();

    }



    /**
     * Check User Location
     **/
    @AfterPermissionGranted(123)
    private void getLocation() {

        LocationManager locationManager = null;
        String provider="";

        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {

            locationManager =
                    (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            provider = locationManager.getBestProvider(criteria, true);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Toast.makeText(this, "In A Minute", Toast.LENGTH_SHORT).show();
                //return;
            }

            locationManager.requestLocationUpdates(provider, 1, 0, this);
            mostRecentLocation = locationManager.getLastKnownLocation(provider);
		  }
		  else {
			  EasyPermissions.requestPermissions(this, "נדרשת הרשאה על מנת להראות מה יש סביב המקום שאתה נמצא בו",
					  123, perms);
		  }





		  }
	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

	  /**
	   * Check Connection to the Internet  
	   **/
	public boolean isOnline() {
		    ConnectivityManager cm =
		        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		    return cm.getActiveNetworkInfo() != null && 
		       cm.getActiveNetworkInfo().isConnectedOrConnecting();
		}
	
public void  sendmail (String address) {
		
	 
		
		   String emailAddressList  =  address ;
		   
		   Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
		   intent.setType("text/plain");
		   intent.putExtra(Intent.EXTRA_SUBJECT,R.string.app_name);
		   intent.putExtra(Intent.EXTRA_TEXT, "HI");
		   intent.setData(Uri.parse("mailto:"+emailAddressList)); // or just "mailto:" for blank
		   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
		   startActivity(intent);
    }




    /** Sets up the interface for getting access to Latitude and Longitude data from device
     **/
     public class JavaScriptInterface
    {
    public Context mContext;
    JavaScriptInterface(Context c)
    {
        mContext = c;
            }
        @JavascriptInterface
        public double getLatitude() {
            getLocation();
           return mostRecentLocation.getLatitude();


        }
        @JavascriptInterface
        public double getLongitude() {
            return mostRecentLocation.getLongitude();
        }
        @JavascriptInterface
        public void showToast(String msg) {
            Toast.makeText(mContext, "Received Msg :" + msg,Toast.LENGTH_SHORT).show();
        }

    }
}
