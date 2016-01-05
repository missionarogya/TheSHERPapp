package android.sherp.missionarogya.thesherpapp;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

public class ShowIntervieweeID extends AppCompatActivity {

    InterviewDetails interviewDetails = InterviewDetails.getInstance();
    String qasetID = interviewDetails.getQasetID();
    String previousIntervieweeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_show_interviewee_id);
            String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            interviewDetails.setLogMessage(interviewDetails.getLogMessage() + mydate + " :: Completing the Interview.\n");
            interviewDetails.setEnd(mydate);
            InterviewDetails.setInstance(interviewDetails);

            TextView txtuser = (TextView) findViewById(R.id.loginID);
            txtuser.setText(interviewDetails.getInterviewerID());

            String intervieweeID = interviewDetails.getIntervieweeID();
        Toast.makeText(ShowIntervieweeID.this, "omg"+intervieweeID, Toast.LENGTH_SHORT).show();

        boolean isUpdated = false;
            if(!(interviewDetails.isFollowup()) && !(qasetID.equals("DEMO"))){
                Toast.makeText(ShowIntervieweeID.this, "tg", Toast.LENGTH_SHORT).show();
                intervieweeID = generateIntervieweeID(interviewDetails.getIntervieweeID(), interviewDetails.getDeviceID(), interviewDetails.getQasetID());
                isUpdated = updateIntervieweeIDtoConfig(intervieweeID);
                Toast.makeText(ShowIntervieweeID.this, "tg1"+isUpdated, Toast.LENGTH_SHORT).show();
                interviewDetails.setIntervieweeID(intervieweeID);
                interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "\nCurrent Interviewee ID : " + intervieweeID + "\n\n");
                InterviewDetails.setInstance(interviewDetails);
            }
            TextView txtintervieweeID = (TextView) findViewById(R.id.intervieweeID);
            txtintervieweeID.setText("Interviewee ID : " + intervieweeID);

            if (isUpdated) {
                Toast.makeText(ShowIntervieweeID.this, "Interviewee ID updated to Config file.", Toast.LENGTH_SHORT).show();
                interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "Interviewee ID updated to Config file.\n");
                InterviewDetails.setInstance(interviewDetails);
            } else {
                if(interviewDetails.isFollowup()) {
                    Toast.makeText(ShowIntervieweeID.this, "This is a follow up interview.", Toast.LENGTH_SHORT).show();
                }else if((qasetID.equals("DEMO"))){
                    Toast.makeText(ShowIntervieweeID.this, "Interviewee ID not updated to Config file as this was a demo interview.", Toast.LENGTH_SHORT).show();
                    interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "Interviewee ID not updated to Config file as this was a demo interview.\n");
                    InterviewDetails.setInstance(interviewDetails);
                }else {
                    Toast.makeText(ShowIntervieweeID.this, "Interviewee ID update to Config file failed.", Toast.LENGTH_SHORT).show();
                    interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "Interviewee ID update to Config file failed.\n");
                    InterviewDetails.setInstance(interviewDetails);
                }
            }
            boolean isWriteSuccessful = false;
            if( !(qasetID.equals("DEMO"))) {
                isWriteSuccessful = writeInterviewDataToDevice(interviewDetails);
            }
            if (isWriteSuccessful) {
                Toast.makeText(ShowIntervieweeID.this, "Successfully updated interview data to device.", Toast.LENGTH_SHORT).show();
                interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "Successfully updated interview data to device.\n");
                InterviewDetails.setInstance(interviewDetails);
            } else {
                if((qasetID.equals("DEMO"))){
                    Toast.makeText(ShowIntervieweeID.this, "Data not updated to device as this was a demo interview.", Toast.LENGTH_SHORT).show();
                    interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "Data not updated to device as this was a demo interview.\n");
                    InterviewDetails.setInstance(interviewDetails);
                }else {
                    Toast.makeText(ShowIntervieweeID.this, "Error occured while updating interview data to device.", Toast.LENGTH_SHORT).show();
                    interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "Error occured while updating interview data to device.\n");
                    InterviewDetails.setInstance(interviewDetails);
                }
            }
            final ImageButton buttonTakeAnotherInterview = (ImageButton) findViewById(R.id.buttonTakeAnotherInterview);
            final Button logout = (Button) findViewById(R.id.logout);
            buttonTakeAnotherInterview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "Conducting another interview with the same QASet.\n");
                    InterviewDetails.setInstance(interviewDetails);
                    Intent intent = new Intent(ShowIntervieweeID.this, ConsentFormActivity.class);
                    ShowIntervieweeID.this.startActivity(intent);
                    ShowIntervieweeID.this.finish();
                }
            });

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShowIntervieweeID.this, LogoutActivity.class);
                    ShowIntervieweeID.this.startActivity(intent);
                    ShowIntervieweeID.this.finish();
                }
            });

    }

    private boolean updateIntervieweeIDtoConfig(String intervieweeID) {
        boolean isUpdated;
        String filename = interviewDetails.getQasetID() + ".txt" ;
        File sherpDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Sherp");
        File qasetDir = new File(sherpDir, interviewDetails.getQasetID() );
        File config = new File(qasetDir, filename);
        if (config.exists()) {
            try {
                FileInputStream fis = new FileInputStream(config);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int content;
                while ((content = fis.read()) != -1) {
                    byteArrayOutputStream.write((char) content);
                }
                fis.close();
                String outputData = byteArrayOutputStream.toString();
                FileOutputStream fos = new FileOutputStream(config);
                PrintWriter pw = new PrintWriter(fos);
                outputData = outputData.replaceAll(previousIntervieweeId, intervieweeID);
                pw.write(outputData);
                pw.close();
                fos.close();
                isUpdated = true;
            } catch (FileNotFoundException e) {
                Toast.makeText(ShowIntervieweeID.this, "Config File not found: "+e.getMessage(), Toast.LENGTH_LONG).show();
                interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "\n[Exception]Config File not found: " + e.getMessage() + "\n\n");
                InterviewDetails.setInstance(interviewDetails);
                isUpdated = false;
            } catch (IOException e) {
                Toast.makeText(ShowIntervieweeID.this, "I/O error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "\n[Exception]I/O error: " + e.getMessage() + "\n\n");
                InterviewDetails.setInstance(interviewDetails);
                isUpdated = false;
            }
        } else {
            Toast.makeText(ShowIntervieweeID.this, "Config file does not exist.", Toast.LENGTH_SHORT).show();
            interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "Config file does not exist\n");
            InterviewDetails.setInstance(interviewDetails);
            isUpdated = false;
        }
        return isUpdated;
    }

    private boolean writeInterviewDataToDevice(InterviewDetails interviewDetails) {
        boolean success;
        File interviewDataDir_ = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Sherp");
        File interviewDataDir = new File(interviewDataDir_, "InterviewData");
        try {
            if (!interviewDataDir.exists()) {
                interviewDataDir.mkdir();
                if(interviewDataDir.exists() && interviewDataDir.isDirectory()) {
                    success = writeToJSON(interviewDataDir, interviewDetails);
                }
                else{
                    success = false;
                }
            } else {
                success = writeToJSON(interviewDataDir, interviewDetails);
            }
        } catch (Exception e) {
            Toast.makeText(ShowIntervieweeID.this, "Error in writing interview data to JSON: "+e.getMessage(), Toast.LENGTH_LONG).show();
            interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "\n[Exception]Error in writing interview data to JSON : " + e.getMessage() + "\n\n");
            InterviewDetails.setInstance(interviewDetails);
            success = false;
        }
        return success;
    }

    private boolean writeToJSON(File interviewDataDir, InterviewDetails interviewDetails) {
        boolean success;
        File interviewDataFile = new File(interviewDataDir, "interviewData_"+interviewDetails.getDeviceID()+".json");
        if (!interviewDataFile.exists()) {
            try {
                interviewDataFile.createNewFile();
                if(interviewDataFile.exists() && interviewDataFile.isFile()){
                    FileOutputStream f = new FileOutputStream(interviewDataFile);
                    PrintWriter pw = new PrintWriter(f);
                    pw.println("[");
                    pw.println("    {");
                    pw.println("        \"qaset_id\":"+"\""+interviewDetails.getQasetID()+"\",");
                    pw.println("        \"followup\":"+"\""+interviewDetails.isFollowup()+"\",");
                    pw.println("        \"interviewer_id\":"+"\""+interviewDetails.getInterviewerID()+"\",");
                    pw.println("        \"interviewee_id\":"+"\""+interviewDetails.getIntervieweeID()+"\",");
                    pw.println("        \"interview_dttm\": { \"startdt_tm\":"+"\""+interviewDetails.getStart()+"\", \"enddt_tm\":"+"\""+interviewDetails.getEnd()+"\"},");
                    pw.println("        \"location\": { \"latitude\":"+"\""+interviewDetails.getLatitude()+"\", \"longitude\":"+"\""+interviewDetails.getLongitude()+"\"},");
                    pw.println("        \"venue\":"+"\""+interviewDetails.getSelectedVenue()+"\",");
                    pw.println("        \"answer\":"+interviewDetails.getAnswers());
                    pw.flush();
                    pw.close();
                    f.close();
                    success = true;
                }else{
                    success = false;
                }
            } catch (IOException e) {
                Toast.makeText(ShowIntervieweeID.this, e.getMessage(), Toast.LENGTH_LONG).show();
                interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "\n[Exception]Error in building JSON : " + e.getMessage() + "\n\n");
                InterviewDetails.setInstance(interviewDetails);
                success = false;
            }
        } else {
            try {
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(interviewDataFile, true)));
                pw.println("    },");
                pw.println("    {");
                pw.println("        \"qaset_id\":"+"\""+interviewDetails.getQasetID()+"\",");
                pw.println("        \"followup\":"+"\""+interviewDetails.isFollowup()+"\",");
                pw.println("        \"interviewer_id\":"+"\""+interviewDetails.getInterviewerID()+"\",");
                pw.println("        \"interviewee_id\":"+"\""+interviewDetails.getIntervieweeID()+"\",");
                pw.println("        \"interview_dttm\": { \"startdt_tm\":"+"\""+interviewDetails.getStart()+"\", \"enddt_tm\":"+"\""+interviewDetails.getEnd()+"\"},");
                pw.println("        \"location\": { \"latitude\":"+"\""+interviewDetails.getLatitude()+"\", \"longitude\":"+"\""+interviewDetails.getLongitude()+"\"},");
                pw.println("        \"venue\":" + "\"" + interviewDetails.getSelectedVenue()+"\",");
                pw.println("        \"answer\":"+interviewDetails.getAnswers());
                pw.flush();
                pw.close();
                success = true;
            } catch (IOException e) {
                Toast.makeText(ShowIntervieweeID.this, "Error(I/O): "+e.getMessage(), Toast.LENGTH_LONG).show();
                interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "\n[Exception]Error(I/O) in building JSON : " + e.getMessage() + "\n\n");
                InterviewDetails.setInstance(interviewDetails);
                success = false;
            }
        }
        return success;
    }

    private String generateIntervieweeID(String lastIntervieweeID, String deviceID, String qasetID){
        previousIntervieweeId = lastIntervieweeID;
        int i ;
        if(!lastIntervieweeID.equals("0000"))
        {
            String substr = lastIntervieweeID.substring(interviewDetails.getQasetID().length()+("_").length()+interviewDetails.getDeviceID().length()+("_").length()+1-1);
            i = Integer.parseInt(substr);
        }
        else
        {
            i = Integer.parseInt(lastIntervieweeID) ;
        }
        i = i+1;
        return qasetID + "_" + deviceID + "_" + Integer.toString(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_interviewee_id, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(ShowIntervieweeID.this, "You cannot go back to the Interview screen.", Toast.LENGTH_SHORT).show();
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
