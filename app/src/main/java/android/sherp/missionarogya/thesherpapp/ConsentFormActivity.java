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

        interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "Launching the consent form screen.\n\n");
        InterviewDetails.setInstance(interviewDetails);

        GPSTracker gps = new GPSTracker(ConsentFormActivity.this);

        // check if GPS enabled
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            interviewDetails.setLatitude(Double.toString(latitude));
            interviewDetails.setLongitude(Double.toString(longitude));
            InterviewDetails.setInstance(interviewDetails);
        } else {
            gps.showSettingsAlert();
        }

        TextView txtuser = (TextView) findViewById(R.id.loginID);
        txtuser.setText(interviewDetails.getInterviewerID());

        String[] venues = (interviewDetails.getListOfVenues()).split(",");
        List<String> venueList = Arrays.asList(venues);

        final CheckBox isConsentFormSigned = (CheckBox) findViewById(R.id.chkConsent);
        isConsentFormSigned.setText(interviewDetails.getConsentText());
        final TextView txtLatitude = (TextView) findViewById(R.id.latitude);
        final TextView txtLongitude = (TextView) findViewById(R.id.longitude);
        final ImageButton startInterview = (ImageButton) findViewById(R.id.buttonStartInterview);
        final TextView txtVenue = (TextView) findViewById(R.id.txtVenue);
        final Spinner spinVenue = (Spinner) findViewById(R.id.spinnerVenue);
        final ArrayAdapter<String> adp= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,venueList);

        if(interviewDetails.getSelectedVenueLocation() != 0){
            isConsentFormSigned.setChecked(true);
            txtVenue.setVisibility(View.VISIBLE);
            spinVenue.setVisibility(View.VISIBLE);
            spinVenue.setAdapter(adp);
            spinVenue.setClickable(true);
            spinVenue.setSelection(interviewDetails.getSelectedVenueLocation());
            startInterview.setVisibility(View.VISIBLE);
            startInterview.setClickable(true);
        }

        isConsentFormSigned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConsentFormSigned.isChecked()) {
                    adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinVenue.setAdapter(adp);
                    txtVenue.setVisibility(View.VISIBLE);
                    txtVenue.setClickable(true);
                    spinVenue.setVisibility(View.VISIBLE);
                    spinVenue.setClickable(true);
                } else {
                    txtLatitude.setVisibility(View.INVISIBLE);
                    txtLongitude.setVisibility(View.INVISIBLE);
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
                if((isConsentFormSigned.isChecked()) && ( spinVenue.getSelectedItemPosition() > 0 )  ){
                    interviewDetails.setSelectedVenueLocation(spinVenue.getSelectedItemPosition());
                    InterviewDetails.setInstance(interviewDetails);
                    startInterview.setVisibility(View.VISIBLE);
                    startInterview.setClickable(true);
                    if(!interviewDetails.getLatitude().equals("0.0")) {
                        txtLatitude.setVisibility(View.VISIBLE);
                        txtLatitude.setText("Latitude: " + interviewDetails.getLatitude());
                    }
                    else{
                        txtLatitude.setVisibility(View.INVISIBLE);
                    }
                    if(!interviewDetails.getLongitude().equals("0.0")) {
                        txtLongitude.setVisibility(View.VISIBLE);
                        txtLongitude.setText("Longitude: " + interviewDetails.getLongitude());
                    }
                    else{
                        txtLongitude.setVisibility(View.INVISIBLE);
                    }
                }
                else{
                    txtLatitude.setVisibility(View.INVISIBLE);
                    txtLongitude.setVisibility(View.INVISIBLE);
                    startInterview.setVisibility(View.INVISIBLE);
                    startInterview.setClickable(false);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                txtLatitude.setVisibility(View.INVISIBLE);
                txtLongitude.setVisibility(View.INVISIBLE);
                startInterview.setVisibility(View.INVISIBLE);
                startInterview.setClickable(false);
            }
        });

        startInterview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] arrSelectedVenueNames = spinVenue.getSelectedItem().toString().split("/");
                String selectedVenue = arrSelectedVenueNames[0].trim();
                interviewDetails.setSelectedVenue(selectedVenue);
                interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "Venue selected : " + spinVenue.getSelectedItem().toString()+"\n");
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
    public void onBackPressed() {
        Intent intent = new Intent(ConsentFormActivity.this, LoginActivity.class);
        ConsentFormActivity.this.startActivity(intent);
        ConsentFormActivity.this.finish();
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
        alertDialog.setTitle("GPS settings");
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to turn it on?");
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