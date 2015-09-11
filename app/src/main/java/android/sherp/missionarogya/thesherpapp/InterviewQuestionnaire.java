package android.sherp.missionarogya.thesherpapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import java.util.Calendar;

public class InterviewQuestionnaire extends AppCompatActivity {
    InterviewDetails interviewDetails = InterviewDetails.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_questionnaire);

        String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        Log.d("omg2", "datestart " + mydate);
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
        InterviewDetails.setInstance(interviewDetails);
        Intent intent = new Intent(currentActivity,ShowIntervieweeID.class);
        currentActivity.startActivity(intent);
        currentActivity.finish();
    }
}

