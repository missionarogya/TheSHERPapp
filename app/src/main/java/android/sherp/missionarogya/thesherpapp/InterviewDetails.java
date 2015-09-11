package android.sherp.missionarogya.thesherpapp;

import android.app.Application;
import android.database.DatabaseErrorHandler;

import java.util.Date;

/**
 * Created by Sonali Sinha on 8/27/2015.
 */
public class InterviewDetails{
    private static InterviewDetails ourInstance = new InterviewDetails();
    private String qasetID;
    private String deviceID;

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

    private String interviewerID;
    private String intervieweeID;
    private String answers;
    private String start;
    private String listOfVenues;
    private String selectedVenue;

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

    private String end;
    private String latitude;
    private String longitude;

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
