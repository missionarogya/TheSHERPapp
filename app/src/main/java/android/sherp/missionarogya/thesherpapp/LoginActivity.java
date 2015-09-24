package android.sherp.missionarogya.thesherpapp;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    InterviewDetails interviewDetails = InterviewDetails.getInstance();
    int backPressed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        if(interviewDetails.getLogMessage() == null){
            interviewDetails.setLogMessage("-------------------------------------------------------------------------------------------------------------------------------------------------\nLaunching the SHERP Interview app.\n");
        }else {
            interviewDetails.setLogMessage(interviewDetails.getLogMessage()+"-------------------------------------------------------------------------------------------------------------------------------------------------\nLaunching the SHERP Interview app.\n");
        }
        InterviewDetails.setInstance(interviewDetails);

        if(interviewDetails.getQasetID() == null){
            interviewDetails.setLogMessage(interviewDetails.getLogMessage()+"QASet has not been selected yet. Displaying all QASets.\n");
            InterviewDetails.setInstance(interviewDetails);
            Intent intent = new Intent(LoginActivity.this, QASetSelectionActivity.class);
            LoginActivity.this.startActivity(intent);
        }
        final ImageButton loginButton = (ImageButton) findViewById(R.id.login);
        final EditText usernameText   = (EditText) findViewById(R.id.username);
        TextView txtqasetID = (TextView)findViewById(R.id.qasetID);
        if(interviewDetails.getInterviewerID() != null){
            usernameText.setText(interviewDetails.getInterviewerID());
        }
        boolean validUser = false ;
        String fileText = readConfig();
        String[] users = saveIntervieweeObject(fileText);
        if(interviewDetails.getQasetID() != null){
            txtqasetID.setText(interviewDetails.getQasetID());
            interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "Launching the Login screen.\n\nReading from config - " + interviewDetails.getQasetID()+  ".txt" + "\n\n" + fileText + "\n\n");
            InterviewDetails.setInstance(interviewDetails);
        }
        final String[] listOfUsers = users;
        //final String[] listOfUsers = {"sonali","samya","rajib"}; //for emulator
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean isValiduser = false;
                String username = usernameText.getText().toString().toLowerCase().trim();
                Log.d("omg : ", "username" + username);
                for (String uname : listOfUsers) {
                    Log.d("omg : ", "uname" + uname);
                    if (username.equals(uname.toLowerCase())) {
                        interviewDetails.setInterviewerID(username);
                        InterviewDetails.setInstance(interviewDetails);
                        Log.d("omg : ", "happy " + username);
                        isValiduser = true;
                        break;
                    }
                }
                if (username.length() == 0 || username.equals("") || username == null) {
                    showToast("Username required");
                } else if (isValiduser) {
                    interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "Logged in as: " + interviewDetails.getInterviewerID().toUpperCase() + "\n");
                    InterviewDetails.setInstance(interviewDetails);
                    Intent intent = new Intent(LoginActivity.this, ConsentFormActivity.class);
                    LoginActivity.this.startActivity(intent);
                } else {
                    showToast("Access denied");
                    interviewDetails.setLogMessage(interviewDetails.getLogMessage() + username.toUpperCase() + " was denied login access.\n");
                    InterviewDetails.setInstance(interviewDetails);
                }
            }
        });

    }

    String[] saveIntervieweeObject(String fileText){
        String[] tokens = fileText.split(";");
        String[] users = {};
        for(String s : tokens) {
            Log.d("omg : ", "token"+s);
            String[] params = s.split(":");
            Log.d("omg : ", "params[0]"+params[0]);
            if (params[0].contains("users")) {
                Log.d("omg : ", "omg!!");
                users = params[1].split(",");
            }
            if (params[0].contains("last_interviewee_id")) {
                Log.d("omg : ", "omg!!");
                interviewDetails.setIntervieweeID(params[1]);
                InterviewDetails.setInstance(interviewDetails);
            }
            if (params[0].contains("device_id")) {
                Log.d("omg : ", "omg!!");
                interviewDetails.setDeviceID(params[1]);
                InterviewDetails.setInstance(interviewDetails);
            }
            if (params[0].contains("venue")) {
                Log.d("omg : ", "omg!!");
                interviewDetails.setListOfVenues("Select,"+params[1]);
                InterviewDetails.setInstance(interviewDetails);
            }

        }
        return users;
    }

    private String readConfig(){
        String qasetID = "";
        //interviewDetails.setQasetID("QS001MSM"); //for emulator
        if(interviewDetails.getQasetID() != null)
        {
           qasetID = interviewDetails.getQasetID();
        }
        String filename = qasetID  + ".txt" ;
        File config;
        File qasetDir;
        File sherpDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"Sherp");
        if(sherpDir.exists() && sherpDir.isDirectory()){
            qasetDir = new File(sherpDir,qasetID);
            if(qasetDir.exists() && qasetDir.isDirectory()){
                config = new File(qasetDir, filename);
            }
            else
            {
                showToast(qasetID + "Config File does not exist");
                interviewDetails.setLogMessage(interviewDetails.getLogMessage() + qasetID + "Config File does not exist. \n");
                InterviewDetails.setInstance(interviewDetails);
                config = null;
            }
        }
        else{
            showToast("Sherp Folder does not exist");
            interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "SHERP folder does not exist. \n");
            InterviewDetails.setInstance(interviewDetails);
            qasetDir = null;
            config = null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (qasetDir!=null && config != null) {
            if (config.exists()) {
                try {
                    FileInputStream fis = new FileInputStream(config);
                    int content;
                    while ((content = fis.read()) != -1) {
                        // convert to char and display it
                        System.out.print((char) content);
                        byteArrayOutputStream.write((char) content);
                    }
                    fis.close();
                } catch (FileNotFoundException e) {
                    showToast("File not found : " + e.getMessage());
                    interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "\n[Exception]File not found :" + e.getMessage() + "\n\n");
                    InterviewDetails.setInstance(interviewDetails);
                } catch (IOException e) {
                    showToast("Error in reading file : " + e.getMessage());
                    interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "\n[Exception]Error in reading file :" + e.getMessage() + "\n\n");
                    InterviewDetails.setInstance(interviewDetails);
                }
            }
        }
        return byteArrayOutputStream.toString();
       // return "users:rajib,sonali,samya;" +
       //"device_id:tab001;" +
       //"venue:abc,dfg,iop;" +
       //"last_interviewee_id:0000;"; //for emulator
    }

    private void showToast(String message){
        Toast toast;
        toast = Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 330);
        toast.show();
    }

    @Override
    public void onBackPressed() {
        backPressed = backPressed + 1;
        boolean logFileWrite;
        if(backPressed == 1){
            Toast.makeText(LoginActivity.this, "Press the back key again to exit.", Toast.LENGTH_SHORT).show();
        }else if (backPressed == 2){
            interviewDetails.setQasetID(null);
            interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "Exiting from app.\n");
            InterviewDetails.setInstance(interviewDetails);
            logFileWrite = InterviewDetails.writeToLogFile(interviewDetails.getLogMessage());
            if(logFileWrite){
                Toast.makeText(LoginActivity.this, "All your actions have been recorded for logging.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(LoginActivity.this, "There was a problem logging your actions..", Toast.LENGTH_SHORT).show();
            }
            interviewDetails.setLogMessage(null);
            InterviewDetails.setInstance(interviewDetails);
            LoginActivity.this.finish();
        }else{
            interviewDetails.setQasetID(null);
            interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "Exiting from app.\n");
            InterviewDetails.setInstance(interviewDetails);
            logFileWrite = InterviewDetails.writeToLogFile(interviewDetails.getLogMessage());
            if(logFileWrite){
                Toast.makeText(LoginActivity.this, "All your actions have been recorded for logging.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(LoginActivity.this, "There was a problem logging your actions..", Toast.LENGTH_SHORT).show();
            }
            interviewDetails.setLogMessage(null);
            InterviewDetails.setInstance(interviewDetails);
            LoginActivity.this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
