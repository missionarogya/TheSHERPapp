package android.sherp.missionarogya.thesherpapp;

import android.app.Application;
import android.database.DatabaseErrorHandler;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Created by Sonali Sinha on 8/27/2015.
 */
public class InterviewDetails{
    private static InterviewDetails ourInstance = new InterviewDetails();
    private int selectedUsernameLocation = 0;
    private int selectedVenueLocation = 0;
    private String qasetID;
    private String deviceID;
    private String interviewerID;
    private String intervieweeID;
    private String answers;
    private String start;
    private String listOfVenues;
    private String selectedVenue;
    private String end;
    private String latitude;
    private String longitude;
    private String consentText;
    private String logMessage = "";

    public int getSelectedUsernameLocation() { return selectedUsernameLocation;}

    public void setSelectedUsernameLocation(int selectedUsernameLocation) {
        this.selectedUsernameLocation = selectedUsernameLocation;
    }

    public int getSelectedVenueLocation() {
        return selectedVenueLocation;
    }

    public void setSelectedVenueLocation(int selectedVenueLocation) {
        this.selectedVenueLocation = selectedVenueLocation;
    }

     public static boolean writeToLogFile(String message){
        boolean success ;
        File logFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"SherpLog.txt");
        try {
            if (logFile.exists() && logFile.isFile()) {
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)));
                pw.println(message);
                pw.flush();
                pw.close();
                success = true;
            } else {
                logFile.createNewFile();
                if(logFile.isFile() && logFile.exists()) {
                    FileOutputStream f = new FileOutputStream(logFile);
                    PrintWriter pw = new PrintWriter(f);
                    pw.println(message);
                    pw.flush();
                    pw.close();
                    f.close();
                    success = true;
                }
                else{
                    success = false;
                }
            }
        }
        catch(Exception e){
            success = false;
        }
        return success;
    }


    public String getConsentText() { return consentText; }

    public void setConsentText(String consentText) { this.consentText = consentText; }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    public String getListOfVenues() {
        return listOfVenues;
    }

    public void setListOfVenues(String listOfVenues) {
        this.listOfVenues = listOfVenues;
    }

    public String getSelectedVenue() {
        return selectedVenue;
    }

    public void setSelectedVenue(String selectedVenue) {
        this.selectedVenue = selectedVenue;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }


    private InterviewDetails() {
    }

    public String getQasetID() {
        return qasetID;
    }

    public void setQasetID(String qasetID) {
        this.qasetID = qasetID;
    }

    public String getInterviewerID() {
        return interviewerID;
    }

    public void setInterviewerID(String interviewerID) {
        this.interviewerID = interviewerID;
    }

    public String getIntervieweeID() {
        return intervieweeID;
    }

    public void setIntervieweeID(String intervieweeID) {
        this.intervieweeID = intervieweeID;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public static InterviewDetails getInstance() {
        return ourInstance;
    }

    public static void setInstance(InterviewDetails interviewDetails) {
        InterviewDetails.ourInstance = interviewDetails;
    }
}
