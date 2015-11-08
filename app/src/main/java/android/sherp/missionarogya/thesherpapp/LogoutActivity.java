package android.sherp.missionarogya.thesherpapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class LogoutActivity extends AppCompatActivity {
    InterviewDetails interviewDetails = InterviewDetails.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "\n" + interviewDetails.getInterviewerID().toUpperCase() + " has logged out.\n-------------------------------------------------------------------------------------------------------------------------------------------------\n");
        InterviewDetails.setInstance(interviewDetails);
        if(InterviewDetails.writeToLogFile(interviewDetails.getLogMessage())){
            showToast("All your actions have been recorded for logging.");
        }else{
            showToast("There was a problem logging your actions.");
        }
        interviewDetails.setSelectedUsernameLocation(0);
        interviewDetails.setSelectedVenueLocation(0);
        interviewDetails.setLogMessage(null);
        interviewDetails.setConsentText(null);
        interviewDetails.setQasetID(null);
        interviewDetails.setDeviceID(null);
        interviewDetails.setInterviewerID(null);
        interviewDetails.setIntervieweeID(null);
        interviewDetails.setStart(null);
        interviewDetails.setEnd(null);
        interviewDetails.setAnswers(null);
        interviewDetails.setLatitude(null);
        interviewDetails.setLongitude(null);
        interviewDetails.setListOfVenues(null);
        interviewDetails.setSelectedVenue(null);
        interviewDetails.setIsFollowup(false);
        InterviewDetails.setInstance(interviewDetails);

        Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
        LogoutActivity.this.startActivity(intent);
        LogoutActivity.this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    private void showToast(String message){
        Toast toast;
        toast = Toast.makeText(LogoutActivity.this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 890);
        toast.show();
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
