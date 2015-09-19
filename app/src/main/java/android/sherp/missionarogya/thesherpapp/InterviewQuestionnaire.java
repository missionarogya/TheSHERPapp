package android.sherp.missionarogya.thesherpapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;

public class InterviewQuestionnaire extends AppCompatActivity {
    InterviewDetails interviewDetails = InterviewDetails.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_questionnaire);
        String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        interviewDetails.setLogMessage(interviewDetails.getLogMessage() + mydate + " :: Starting the Interview.\n");
        interviewDetails.setStart(mydate);
        InterviewDetails.setInstance(interviewDetails);

        String url = "file:///android_asset/" + interviewDetails.getQasetID() + "/index.html";

        WebView webView = (WebView) findViewById(R.id.webviewInterviewQuestionnaire);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
        webView.addJavascriptInterface(new JSBridgeToSaveAnswers(InterviewQuestionnaire.this, interviewDetails), "JSBridgeToSaveAnswers");
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
    InterviewDetails interviewDetails;

    JSBridgeToSaveAnswers(Activity currentActivity, InterviewDetails interviewDetails){
       // Log.d("omg1 :: ", "currentActivity "+currentActivity.toString());
        this.currentActivity = currentActivity;
        this.interviewDetails = interviewDetails;
    }

    @android.webkit.JavascriptInterface
    public void saveAnswersToApp(String answers) {
        //Log.d("omg1 :: ", "ans "+answers);
        interviewDetails.setAnswers(answers);
        interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "\nAnswers: " + answers + "\n\n");
        InterviewDetails.setInstance(interviewDetails);
        Intent intent = new Intent(currentActivity,ShowIntervieweeID.class);
        currentActivity.startActivity(intent);
        currentActivity.finish();
    }

    @android.webkit.JavascriptInterface
    public void showAlert(String message){
        Toast.makeText(currentActivity, message, Toast.LENGTH_SHORT).show();
    }

    @android.webkit.JavascriptInterface
    public void playMusic(String audioName){
        MediaPlayer mp = new MediaPlayer();
        if(mp.isPlaying())
        {
            mp.stop();
        }
        try {
            mp.reset();
            AssetFileDescriptor afd = currentActivity.getAssets().openFd(interviewDetails.getQasetID()+audioName);
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mp.prepare();
            mp.start();
        } catch (IllegalStateException e) {
            Toast.makeText(currentActivity, "Error in playing audio: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "\n[Exception]Error in playing audio: " + e.getMessage() + "\n\n");
            InterviewDetails.setInstance(interviewDetails);
        } catch (IOException e) {
            Toast.makeText(currentActivity, "Error(I/O) in playing audio: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "\n[Exception]Error(I/O) in playing audio: " + e.getMessage() + "\n\n");
            InterviewDetails.setInstance(interviewDetails);
        }
    }
}