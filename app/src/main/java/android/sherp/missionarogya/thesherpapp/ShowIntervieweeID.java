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
    String previousIntervieweeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_interviewee_id);

        String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        //Log.d("omg2", "datestart " + mydate);
        interviewDetails.setEnd(mydate);
        InterviewDetails.setInstance(interviewDetails);

        TextView txtuser = (TextView) findViewById(R.id.loginID);
        txtuser.setText(interviewDetails.getInterviewerID());

        String intervieweeID = generateIntervieweeID(interviewDetails.getIntervieweeID(), interviewDetails.getDeviceID(), interviewDetails.getQasetID());
        interviewDetails.setIntervieweeID(intervieweeID);
        InterviewDetails.setInstance(interviewDetails);
        TextView txtintervieweeID = (TextView) findViewById(R.id.intervieweeID);
        txtintervieweeID.setText(intervieweeID);

        boolean isUpdated = updateIntervieweeIDtoConfig(interviewDetails.getIntervieweeID());
        if (isUpdated) {
            Toast.makeText(ShowIntervieweeID.this, "Interviewee ID updated to Config file.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ShowIntervieweeID.this, "Interviewee ID update to Config file failed.", Toast.LENGTH_SHORT).show();
        }
        boolean isWriteSuccessful = writeInterviewDataToDevice(interviewDetails);
        if (isWriteSuccessful) {
            Toast.makeText(ShowIntervieweeID.this, "Successfully updated interview data to device.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ShowIntervieweeID.this, "Error occured while updating interview data to device.", Toast.LENGTH_SHORT).show();
        }

        final ImageButton buttonTakeAnotherInterview = (ImageButton) findViewById(R.id.buttonTakeAnotherInterview);
        final Button buttonTakeAnotherInterviewdiffQaset = (Button) findViewById(R.id.buttonTakeAnotherInterviewDiffQAset);
        final Button logout = (Button) findViewById(R.id.logout);
        buttonTakeAnotherInterview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowIntervieweeID.this, ConsentFormActivity.class);
                ShowIntervieweeID.this.startActivity(intent);
                ShowIntervieweeID.this.finish();
            }
        });

        buttonTakeAnotherInterviewdiffQaset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowIntervieweeID.this, LogoutActivity.class);
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
                //Toast.makeText(ShowIntervieweeID.this, "read data " + byteArrayOutputStream.toString(), Toast.LENGTH_LONG).show();
                FileOutputStream fos = new FileOutputStream(config);
                PrintWriter pw = new PrintWriter(fos);
                outputData = outputData.replaceAll(previousIntervieweeId, intervieweeID);
                pw.write(outputData);
                pw.close();
                fos.close();
                isUpdated = true;
            } catch (FileNotFoundException e) {
                Toast.makeText(ShowIntervieweeID.this, e.getMessage(), Toast.LENGTH_LONG).show();
                isUpdated = false;
            } catch (IOException e) {
                Toast.makeText(ShowIntervieweeID.this, e.getMessage(), Toast.LENGTH_LONG).show();
                isUpdated = false;
            }
        } else {
            Toast.makeText(ShowIntervieweeID.this, "Config file does not exist.", Toast.LENGTH_SHORT).show();
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
                success = writeToJSON(interviewDataDir, interviewDetails);
                //Toast.makeText(ShowIntervieweeID.this, "Interview data successfully written to JSON.", Toast.LENGTH_SHORT).show();
            } else {
                success = writeToJSON(interviewDataDir, interviewDetails);
                //Toast.makeText(ShowIntervieweeID.this, "Interview data successfully written to JSON.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(ShowIntervieweeID.this, "Error in writing interview data to JSON: "+e.getMessage(), Toast.LENGTH_LONG).show();
            success = false;
        }
        return success;
    }

    private boolean writeToJSON(File interviewDataDir, InterviewDetails interviewDetails) {
        //Toast.makeText(ShowIntervieweeID.this, "omg: "+interviewDetails.getAnswers(), Toast.LENGTH_LONG).show();
        boolean success;
        File interviewDataFile = new File(interviewDataDir, "interviewData.json");
        if (!interviewDataFile.exists()) {
            try {
                interviewDataFile.createNewFile();
                FileOutputStream f = new FileOutputStream(interviewDataFile);
                PrintWriter pw = new PrintWriter(f);
                pw.println("[");
                pw.println("    {");
                pw.println("        \"qaset_id\":"+"\""+interviewDetails.getQasetID()+"\",");
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
            } catch (IOException e) {
                Toast.makeText(ShowIntervieweeID.this, e.getMessage(), Toast.LENGTH_LONG).show();
                success = false;
            }
        } else {
            try {
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(interviewDataFile, true)));
                pw.println("    },");
                pw.println("    {");
                pw.println("        \"qaset_id\":"+"\""+interviewDetails.getQasetID()+"\",");
                pw.println("        \"interviewer_id\":"+"\""+interviewDetails.getInterviewerID()+"\",");
                pw.println("        \"interviewee_id\":"+"\""+interviewDetails.getIntervieweeID()+"\",");
                pw.println("        \"interview_dttm\": { \"startdt_tm\":"+"\""+interviewDetails.getStart()+"\", \"enddt_tm\":"+"\""+interviewDetails.getEnd()+"\"},");
                pw.println("        \"location\": { \"latitude\":"+"\""+interviewDetails.getLatitude()+"\", \"longitude\":"+"\""+interviewDetails.getLongitude()+"\"},");
                pw.println("        \"venue\":"+"\""+interviewDetails.getSelectedVenue()+"\",");
                pw.println("        \"answer\":"+interviewDetails.getAnswers());
                pw.flush();
                pw.close();
                success = true;
            } catch (IOException e) {
                Toast.makeText(ShowIntervieweeID.this, e.getMessage(), Toast.LENGTH_LONG).show();
                success = false;
            }
        }
        return success;
    }

    private String generateIntervieweeID(String lastIntervieweeID, String deviceID, String qasetID){
        previousIntervieweeId = lastIntervieweeID;
        String currentIntervieweeID = "";
        int i ;
        if(!lastIntervieweeID.equals("0000"))
        {
            String substr = lastIntervieweeID.substring(16);
            i = Integer.parseInt(substr);
        }
        else
        {
            i = Integer.parseInt(lastIntervieweeID) ;
        }
        i = i+1;
        currentIntervieweeID = qasetID + "_" + deviceID + "_" + Integer.toString(i);
     return currentIntervieweeID;
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
