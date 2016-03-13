package android.sherp.missionarogya.thesherpapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

public class InterviewQuestionnaire extends AppCompatActivity {
    InterviewDetails interviewDetails = InterviewDetails.getInstance();
    MediaRecorder myAudioRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_interview_questionnaire);
        String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        interviewDetails.setLogMessage(interviewDetails.getLogMessage() + mydate + " :: Starting the Interview.\n");
        interviewDetails.setStart(mydate);
        InterviewDetails.setInstance(interviewDetails);

        try {
            myAudioRecorder=new MediaRecorder();
            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            File parentDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Sherp");
            if (parentDir.exists() && parentDir.isDirectory()) {
                File qasetDir = new File(parentDir, interviewDetails.getQasetID());
                if (qasetDir.exists() && qasetDir.isDirectory()) {
                    File soundDir = new File(qasetDir, "audio");
                    if (soundDir.exists() && soundDir.isDirectory()) {
                       myAudioRecorder.setOutputFile(soundDir.getAbsolutePath() +"/demo.mp3");
                    }
                }
            }
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }

        //to load webview
        String url = "file:///android_asset/" + deleteBOMCharacters(interviewDetails.getQasetID()) + "/index.html";
        WebView webView = (WebView) findViewById(R.id.webviewInterviewQuestionnaire);
        WebSettings settings = webView.getSettings();
        webView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
        webView.addJavascriptInterface(new JSBridgeToSaveAnswers(InterviewQuestionnaire.this, interviewDetails, myAudioRecorder), "JSBridgeToSaveAnswers");
    }

    public String deleteBOMCharacters(String qasetId) {
        String updatedString = qasetId;
        int count = 0;
        char[] charBOM = qasetId.toCharArray();
        for(char a : charBOM){
            int intValue = (int) a;
            // Hexa value of BOM = EF BB BF  => int 65279
            if (intValue == 65279) {
                Toast.makeText(getApplicationContext(),"This file starts with a BOM",Toast.LENGTH_SHORT).show();
                count = count+1;
                continue;
            } else {
                Toast.makeText(getApplicationContext(),"This file does not contain BOM",Toast.LENGTH_SHORT).show();
                break;
            }
        }
        if(count > 0){
            interviewDetails.setLogMessage("This file contains BOM!");
            InterviewDetails.setInstance(interviewDetails);
            updatedString = updatedString.substring(count);
        }
        return updatedString;
    }


    @Override
    public void onBackPressed() {
        Toast.makeText(InterviewQuestionnaire.this, "You cannot go back from here.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_interview_questionnaire, menu);
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


class JSBridgeToSaveAnswers{
    private Activity currentActivity;
    MediaRecorder myAudioRecorder;
    InterviewDetails interviewDetails;
    MediaPlayer mp = new MediaPlayer();
    String currentlyPlaying = "none";

    JSBridgeToSaveAnswers(Activity currentActivity, InterviewDetails interviewDetails, MediaRecorder myAudioRecorder){
        this.currentActivity = currentActivity;
        this.interviewDetails = interviewDetails;
        this.myAudioRecorder = myAudioRecorder;
    }

    @android.webkit.JavascriptInterface
    public void saveAnswersToApp(String answers) {
        try {
            myAudioRecorder.stop();
            myAudioRecorder.release();
            myAudioRecorder = null;
        }
        catch (Exception e) {
            Toast.makeText(currentActivity,e.toString(),Toast.LENGTH_LONG).show();
        }
        //saving answers
        interviewDetails.setAnswers(answers);
        interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "\nAnswers: " + answers + "\n\n");
        InterviewDetails.setInstance(interviewDetails);
        Intent intent = new Intent(currentActivity, ShowIntervieweeID.class);
        currentActivity.startActivity(intent);
        currentActivity.finish();
    }

    @android.webkit.JavascriptInterface
    public void showAlert(String message){
        Toast.makeText(currentActivity, message, Toast.LENGTH_SHORT).show();
    }

    @android.webkit.JavascriptInterface
    public void playMusic(String audioName){
        int resume;
        if(currentlyPlaying.equals(audioName)){
            resume = 1;
        }
        else
        {
            currentlyPlaying = audioName;
            resume = 0;
        }
        FileInputStream fis = null;
        File qasetDir ;
        File soundFile ;
        File soundDir;
        FileDescriptor fd = null;
        try {
            File parentDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Sherp");
            if (parentDir.exists() && parentDir.isDirectory()) {
                qasetDir = new File(parentDir, interviewDetails.getQasetID());
                if (qasetDir.exists() && qasetDir.isDirectory()) {
                    soundDir = new File(qasetDir, "audio");
                    if (soundDir.exists() && soundDir.isDirectory()) {
                        soundFile = new File(soundDir, audioName);
                        if(soundFile.exists() && soundFile.isFile()){
                            soundFile.setReadable(true);
                            fis = new FileInputStream(soundFile.getAbsolutePath());
                            fd = fis.getFD();
                        }
                    }
                }
            }
            if (fd != null ) {
                if (mp.isPlaying() && resume==1) {
                    mp.pause();
                }
                else {
                    mp.reset();
                    mp.setDataSource(fd);
                    mp.prepare();
                    mp.start();
                    fis.close();
                }
            }
        }catch (FileNotFoundException e) {
            Toast.makeText(currentActivity, "Error(File not found) in playing audio: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "\n[File not found Exception]Error in playing audio: " + e.getMessage() + "\n\n");
            InterviewDetails.setInstance(interviewDetails);
        } catch (IllegalStateException e) {
            Toast.makeText(currentActivity, "Error(Illegal state exception) in playing audio: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "\n[Illegal State Exception]Error in playing audio: " + e.getMessage() + "\n\n");
            InterviewDetails.setInstance(interviewDetails);
        } catch (IOException e) {
            Toast.makeText(currentActivity, "Error(I/O) in playing audio: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "\n[Exception]Error(I/O) in playing audio: " + e.getMessage() + "\n\n");
            InterviewDetails.setInstance(interviewDetails);
        }
        catch (Exception e) {
            Toast.makeText(currentActivity, "Error in playing audio: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "\n[Exception]Error in playing audio: " + e.getMessage() + "\n\n");
            InterviewDetails.setInstance(interviewDetails);
        }
    }
}