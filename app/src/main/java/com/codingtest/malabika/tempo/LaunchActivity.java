package com.codingtest.malabika.tempo;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;


public class LaunchActivity extends AppCompatActivity {

    boolean useJsonMethod = false; //Make true to see the values as fetched from the OpenWeathermap API.
    //CODE NOT IMPLEMENTED for useJsonMethod = true

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        if(!useJsonMethod) {
            //Calling Main Activity after a few seconds of delay
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent i = new Intent(LaunchActivity.this, MainActivity.class);
                    startActivity(i);
                }

            }, 1000L);
        }
        else {

            //TODO:  Add functionality for obtaining the Json object from the API call

            Toast.makeText(LaunchActivity.this, "Fetching weather data online NOT Implemented!", Toast.LENGTH_SHORT).show();

            //Calling main activity
            Intent i = new Intent(LaunchActivity.this, MainActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        finish();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}
