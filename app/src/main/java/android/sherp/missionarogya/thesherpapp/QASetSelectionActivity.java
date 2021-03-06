package android.sherp.missionarogya.thesherpapp;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class QASetSelectionActivity extends AppCompatActivity {
    InterviewDetails interviewDetails = InterviewDetails.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qaset_selection2);
        interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "Launching QASetSelection screen.\n");
        InterviewDetails.setInstance(interviewDetails);
        String imgPath ="";

        File sherpDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"Sherp");
        if(sherpDir.exists() && sherpDir.isDirectory()){
            imgPath = sherpDir.getAbsolutePath();
        }

        String configData = readQASetDetailsFromConfig();

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.qasetRadioGroup);
        radioGroup.setGravity(Gravity.START);

        if (configData != null) {
            String[] arrConfigData = configData.split(";");
            if (arrConfigData != null ) {
                int i = 0;
                int j = 50;
                int k = 100;
                for (String s : arrConfigData) {
                    String[] radio = s.split(":");
                    RadioButton radioButtonView = new RadioButton(this);
                    radioButtonView.setGravity(Gravity.START);
                    ImageView imageView = new ImageView(this);
                    if(radio.length == 2) {
                      radio[0] = radio[0].replaceAll("(\\r|\\n)", "");
                      radioButtonView.setText(radio[0]);
                      imageView.setImageBitmap(BitmapFactory.decodeFile(imgPath + File.separator + radio[0] + ".png"));
                    }
                    layout.addView(imageView, RelativeLayout.LayoutParams.MATCH_PARENT);
                    imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                    radioButtonView.setY(i);
                    radioButtonView.setX(50);
                    radioButtonView.setOnClickListener(mThisButtonListener);
                    radioGroup.addView(radioButtonView, RelativeLayout.LayoutParams.MATCH_PARENT);
                    TextView textView = new TextView(this);
                    layout.addView(textView, RelativeLayout.LayoutParams.MATCH_PARENT);
                    textView.setTextSize(15);
                    textView.setGravity(Gravity.START);
                    textView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                    textView.setY(j);
                    textView.setX(50);
                    if(radio.length == 2) {
                        textView.setText(radio[1]);
                    }
                    imageView.setMaxHeight(10);
                    imageView.setMaxWidth(10);
                    imageView.setY(k);
                    imageView.setX(50);
                    i = i+220;
                    j = j+250;
                    k = k+250;
                }
            }
        }
    }

    public void onClick(View view) {
        try {
            String s = ((RadioButton) view).getText().toString();
            Toast.makeText(QASetSelectionActivity.this, "This is: " + s,
                    Toast.LENGTH_LONG).show();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private View.OnClickListener mThisButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            final ImageButton go = (ImageButton) findViewById(R.id.go);
            String s = ((RadioButton) v).getText().toString();
            interviewDetails.setLogMessage(interviewDetails.getLogMessage() +"\nSelected QASet id - "+s+"\n");
            interviewDetails.setQasetID(s);
            InterviewDetails.setInstance(interviewDetails);
            showToast("You have selected QASet: " + s);
            go.setVisibility(View.VISIBLE);
            go.setClickable(true);
            go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(QASetSelectionActivity.this, LoginActivity.class);
                    QASetSelectionActivity.this.startActivity(intent);
                }
            });
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_qaset_selection, menu);
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

    private String readQASetDetailsFromConfig(){
        String configData = "";
        try{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        File sherpDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"Sherp");
        if(sherpDir.exists() && sherpDir.isDirectory()){
            File sherpConfig = new File(sherpDir, "SherpConfig.txt");
            if(sherpConfig.exists() && sherpConfig.isFile()){
                try {
                    FileInputStream fis = new FileInputStream(sherpConfig);
                    int content;
                    while ((content = fis.read()) != -1) {
                        byteArrayOutputStream.write((char) content);
                    }
                    fis.close();
                    configData = byteArrayOutputStream.toString();
                    interviewDetails.setLogMessage(interviewDetails.getLogMessage() +"Reading from SherpConfig.txt.\n\n"+configData+"\n");
                    InterviewDetails.setInstance(interviewDetails);
                } catch (FileNotFoundException e) {
                    showToast("File not found : " + e.getMessage());
                    interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "\n[Exception]File not found : " + e.getMessage() + "\n\n");
                    InterviewDetails.setInstance(interviewDetails);
                } catch (IOException e) {
                    showToast("Error(I/O) in reading file : " + e.getMessage());
                    interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "\n[Exception]Error(I/O) in reading file : " + e.getMessage() + "\n\n");
                    InterviewDetails.setInstance(interviewDetails);
                } catch (Exception e){
                    showToast("Error in reading file : " + e.getMessage());
                    interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "\n[Exception]Error in reading file : " + e.getMessage() + "\n\n");
                    InterviewDetails.setInstance(interviewDetails);
                }
            }
            else{
                showToast("The Sherp Config file does not exist.");
                interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "The Sherp Config file does not exist.\n");
                InterviewDetails.setInstance(interviewDetails);
            }
        }
        else {
            showToast("The Sherp folder does not exist.");
            interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "The Sherp folder does not exist.\n");
            InterviewDetails.setInstance(interviewDetails);
        }
    }
    catch(Exception e)
    {
        showToast(e.getMessage());
        interviewDetails.setLogMessage(interviewDetails.getLogMessage() + "\n[Exception]Error reading SherpConfig.txt: " + e.getMessage()+"\n\n");
        InterviewDetails.setInstance(interviewDetails);
    }
    return configData;

      //  return "QS001MSM:dsddsddssdsdsd;" +
       //"QS004DRT:fgfgfgfffffffffff;" +
       //"QS001ABC:jjjjjjiiiiiiiiii;" +
       //"QS001XYZ:fmmmmmmmmmmmmmmmmmmmmmmmgff;"; //for emulator
    }

    @Override
    public void onBackPressed() {
        interviewDetails.setQasetID(null);
        InterviewDetails.setInstance(interviewDetails);
        QASetSelectionActivity.this.finish();
    }

    private void showToast(String message){
        Toast toast;
        toast = Toast.makeText(QASetSelectionActivity.this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 890);
        toast.show();
    }
}
