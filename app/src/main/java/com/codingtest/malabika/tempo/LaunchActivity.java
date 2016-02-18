package com.codingtest.malabika.tempo;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class LaunchActivity extends AppCompatActivity {

    boolean hasNetworkConnectivity = false;
//    TextView InProgressText;
    //String tempDataURL = "http://api.openweathermap.org/data/2.5/forecast/city?id=5101717&units=metric&APPID=9fc9ec9ba5f3a3a53d2cb1e637be4c88";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

//        InProgressText = (TextView) findViewById(R.id.inProgressTextView);

        hasNetworkConnectivity = checkIfHasNetworkConnectivity();

        if(hasNetworkConnectivity)
        {
            //Start the asynctask for downloading the json object
            //new TempDownloadTask().execute(tempDataURL);
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // populate the list view in main activity
                    Intent i = new Intent(LaunchActivity.this, MainActivity.class);
                    startActivity(i);
                }

            }, 1000L);

        }
        else
        {
            Log.e("TEMPO", "connection not available");
            //Alert dialog for not connected to the internet
        }

    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    private boolean checkIfHasNetworkConnectivity() {

        boolean wifiConnected = false;
        boolean mobileConnected = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni.getTypeName().equalsIgnoreCase("WIFI"))
            if(ni.isConnected())
                wifiConnected = true;

        if(ni.getTypeName().equalsIgnoreCase("MOBILE"))
            if(ni.isConnected())
                mobileConnected = true;

        return (wifiConnected||mobileConnected);
    }

    //start the async download task for the weather data.
    //Made public so that the main activity can access the data too to populate the list, when refresh button is hit.
    public class TempDownloadTask extends AsyncTask <String, Void, String> {

        @Override
        protected void onPreExecute() {
            Log.e("TEMPO", "getting the JSON object...");
//            InProgressText.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... params) {
            //API call to get the JSON object
            String ApiUrl = params[0];
            Log.e("TEMPO", ApiUrl);

            //generate random values for temperatures
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            Log.e("TEMPO", "Done execution on background task");

            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // populate the list view in main activity
                    Intent i = new Intent(LaunchActivity.this, MainActivity.class);
                    startActivity(i);
                }

            }, 1000L);

        }
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
