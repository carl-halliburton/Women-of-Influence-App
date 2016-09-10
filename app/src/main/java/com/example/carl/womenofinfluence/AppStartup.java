package com.example.carl.womenofinfluence;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/*The purpose of this activity is to obtain an Access Token for authentication and save to
  preferences. After this is done the activity is killed with finish() method.*/
public class AppStartup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_startup);

        getAccessToken();
    }

    public void getAccessToken() {
        //Make sure that ACCESS_TOKEN is set in strings.xml
        String accessToken = getString(R.string.ACCESS_TOKEN);
        if (accessToken != null && !accessToken.equals("ACCESS_TOKEN")) {
            //Store accessToken in SharedPreferences
            SharedPreferences prefs = getSharedPreferences("com.example.carl.womenofinfluence", Context.MODE_PRIVATE);
            prefs.edit().putString("access-token", accessToken).apply();

            //Proceed to Home Activity
            Intent intent = new Intent(AppStartup.this, Home.class);
            startActivity(intent);
            finish();
        }
    }

    public void onClickGotoHome(View v) {
        startActivity(new Intent(AppStartup.this, Home.class));
        finish();
    }

}
