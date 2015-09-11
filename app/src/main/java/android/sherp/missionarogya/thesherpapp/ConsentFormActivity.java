package android.sherp.missionarogya.thesherpapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class ConsentFormActivity extends AppCompatActivity {
    InterviewDetails interviewDetails = InterviewDetails.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent_form);

        TextView txtuser = (TextView) findViewById(R.id.loginID);
        txtuser.setText(interviewDetails.getInterviewerID());

        String[] venues = (interviewDetails.getListOfVenues()).split(",");
        final List<String> venueList = Arrays.asList(venues);

        final CheckBox isConsentFormSigned = (CheckBox) findViewById(R.id.chkConsent);
        final ImageButton startInterview = (ImageButton) findViewById(R.id.buttonStartInterview);
        final TextView txtVenue = (TextView) findViewById(R.id.txtVenue);
        final Spinner spinVenue = (Spinner) findViewById(R.id.spinnerVenue);
        final ArrayAdapter<String> adp= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,venueList);


        isConsentFormSigned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConsentFormSigned.isChecked() == true) {
                    adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinVenue.setAdapter(adp);

                    txtVenue.setVisibility(View.VISIBLE);
                    txtVenue.setClickable(true);
                    spinVenue.setVisibility(View.VISIBLE);
                    spinVenue.setClickable(true);

                    GPSTracker gps = new GPSTracker(ConsentFormActivity.this);

                    // check if GPS enabled
                    if (gps.canGetLocation()) {
                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();
                        interviewDetails.setLatitude(Double.toString(latitude));
                        interviewDetails.setLongitude(Double.toString(longitude));
                        InterviewDetails.setInstance(interviewDetails);
                        //Toast.makeText(ConsentFormActivity.this, Double.toString(latitude), Toast.LENGTH_LONG).show();
                    } else {
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        gps.showSettingsAlert();
                        //Toast.makeText(ConsentFormActivity.this, "Unable to get location.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    txtVenue.setVisibility(View.INVISIBLE);
                    txtVenue.setClickable(false);
                    spinVenue.setVisibility(View.INVISIBLE);
                    spinVenue.setClickable(false);
                    startInterview.setVisibility(View.INVISIBLE);
                    startInterview.setClickable(false);
                }
            }
        });

        spinVenue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if((isConsentFormSigned.isChecked() == true) && ( spinVenue.getSelectedItemPosition() > 0 )  ){
                    startInterview.setVisibility(View.VISIBLE);
                    startInterview.setClickable(true);
                }
                else{
                    startInterview.setVisibility(View.INVISIBLE);
                    startInterview.setClickable(false);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                startInterview.setVisibility(View.INVISIBLE);
                startInterview.setClickable(false);
            }
        });

        startInterview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interviewDetails.setSelectedVenue(spinVenue.getSelectedItem().toString());
                InterviewDetails.setInstance(interviewDetails);

                Intent intent = new Intent(ConsentFormActivity.this, InterviewQuestionnaire.class);
                ConsentFormActivity.this.startActivity(intent);
                ConsentFormActivity.this.finish();
            }
        });

        final Button logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConsentFormActivity.this, LogoutActivity.class);
                ConsentFormActivity.this.startActivity(intent);
                ConsentFormActivity.this.finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_consent_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


class GPSTracker extends Service implements LocationListener {

    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            //locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}