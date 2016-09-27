package com.example.carl.womenofinfluence;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static android.R.attr.button;

public class Feedback extends AppCompatActivity {

    private GlobalAppData appData;
    final Button submitButton = (Button) findViewById(R.id.btnSubmit);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setTitle(null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_home_white);

        appData = GlobalAppData.getInstance(getString(R.string.ACCESS_TOKEN), Feedback.this);

        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendFeedback();
                successDialog(view);
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
            case R.id.action_search:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendFeedback() {

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
