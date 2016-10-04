package com.example.carl.womenofinfluence;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.attr.button;

public class Feedback extends AppCompatActivity {

    private GlobalAppData appData;
    private Button submitButton;

    //EditText fields
    private EditText editName;
    private EditText editEmail;
    private EditText editMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setTitle(R.string.app_name);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_home_black);

        appData = GlobalAppData.getInstance(getString(R.string.ACCESS_TOKEN), Feedback.this);

        //initialize edittext fields
        editName = (EditText) findViewById(R.id.fieldName);
        editEmail = (EditText) findViewById(R.id.fieldEmail);
        editMessage = (EditText) findViewById(R.id.fieldMessage);

        submitButton = (Button) findViewById(R.id.btnSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //checks whether there are error messages is set
                //runs email intent if no errors
                if(validateFields())
                    sendFeedback(view);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        item = menu.findItem(R.id.menu_refresh);
        item.setVisible(false);

        item = menu.findItem(R.id.menu_feedback);
        item.setVisible(false);

        appData.getNotify().checkNotificationStatus(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                startActivity(new Intent(Feedback.this, Home.class));
                return true;
            case R.id.action_notification:
                if (item.isChecked())
                    appData.getNotify().isChecked(item, this);
                else
                    appData.getNotify().isUnChecked(item, this);
                return true;
            case R.id.menu_video_gallery:
                startActivity(new Intent(Feedback.this, VideoGallery.class));
                return true;
            //case R.id.action_search:
                //return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Gathers form data and sends it to email intent
    //intent opens email client chooser
    public void sendFeedback(View view) {

        String subject = "Ascend - Women of Influence - Feedback - " + editMessage.getText();
        String message = "" + editMessage.getText();
        clearFields();

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        //TODO add email address for Hawke Films once set up and prior to deployment
        emailIntent.setData(Uri.parse("mailto:" + "carl.j.halliburton@gmail.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        try {
            startActivity(Intent.createChooser(emailIntent, "Send feedback using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(Feedback.this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    //checks whether EditTextfields are empty or not
    //if so display error message
    public boolean validateFields() {
        if (editName.getText().toString().equals(""))
            editName.setError("Name is Required");
        else
            editName.setError(null);

        if (editEmail.getText().toString().equals(""))
            editEmail.setError("Name is Required");
        else
            editEmail.setError(null);

        if (editMessage.getText().toString().equals(""))
            editMessage.setError("Name is Required");
        else
            editMessage.setError(null);

        return isErrorFree();
    }

    //checks if any error message visible before submitting
    boolean isErrorFree() {
        if (TextUtils.isEmpty(editName.getError()) ||
                (TextUtils.isEmpty(editEmail.getError())) ||
                (TextUtils.isEmpty(editMessage.getError()))) {
            return true;
        }
        else
            return false;
    }

    public void successDialog(View view) {

        clearFields();

        new AlertDialog.Builder(this)
                .setTitle("Feedback Sent")
                .setMessage("Thank-you for sending feedback.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void clearFields() {
        EditText name = (EditText)findViewById(R.id.fieldName);
        name.setText("");

        EditText email = (EditText)findViewById(R.id.fieldEmail);
        email.setText("");

        EditText subject = (EditText)findViewById(R.id.fieldSubject);
        subject.setText("");

        EditText message = (EditText)findViewById(R.id.fieldMessage);
        message.setText("");
    }


}
