package com.codingtest.malabika.tempo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    public native double tempconvmethod(double initialTemp, boolean isC);
    public native double[] tempconvmethodarray(double[] initialTempArray, boolean isC);
    static{
        System.loadLibrary("tempConversion");
    }

    //comment
    public final static String TAG = "TEMPO";
    ToggleButton Btn_CFToggleAll;
    Button Btn_Exit;
//    ToggleButton Btn_CFToggle2;
//    ToggleButton Btn_CFToggle3;
//    ToggleButton Btn_CFToggle4;
//    ToggleButton Btn_CFToggle5;
//    int indexWeekDay;
    ImageButton Btn_Refresh;
    TextView ambientTempTextView;
    TextView  cityNameTextView;
    ListView tempMonToFriListView;
//    ListView MonToFriDayListView;

    boolean isCelsius = true;

    double[] tempList = new double[5];
    String[] weekdays_list = {"MON", "TUE", "WED", "THUR", "FRI"};
    boolean[] btnState = new boolean[5];
    ArrayList<String> temperatureValues = new ArrayList<>();

    public static final int MIN_TEMP = -20;
    public static final int MAX_TEMP = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialisation of UI elements

        ambientTempTextView = (TextView) findViewById(R.id.AmbientTempText);  // TextView for display of Ambient Temperature
        tempMonToFriListView = (ListView) findViewById(R.id.MontoFriTempList);  // LIst View displaying the temperature for the week (mon to fri)

        cityNameTextView = (TextView) findViewById(R.id.LocationNameText);
        Btn_Refresh = (ImageButton) findViewById(R.id.RefreshDataButton);
        Btn_CFToggleAll = (ToggleButton) findViewById(R.id.toggleAllButton);
        Btn_Exit = (Button) findViewById(R.id.buttonExit);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
        String currentDateandTime = sdf.format(new Date());
        String cityName = "NEW BRUNSWICK "+ "\n NEW JERSEY" + "\n"+ currentDateandTime;
        cityNameTextView.setText(cityName);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAmbientTempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        generateRandomTemperatureList(MIN_TEMP, MAX_TEMP);
        populateListView(weekdays_list,null,temperatureValues);

        //for each button pressed, change the temperature alongside
        Btn_CFToggleAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // toggle between the celsius and fahrenheit display of the temperatures in the application
                //updateTemperatureDisplay(isChecked);
                temperatureValues.clear();
                tempList = tempconvmethodarray(tempList,isChecked);
                for(int i = 0; i < tempList.length; i++) {
                    Log.e(TAG, "changed to: " + tempList[i]);
                    temperatureValues.add(String.valueOf(tempList[i]));
                }
                resetButtonStates(btnState,isChecked);
                populateListView(weekdays_list,btnState,temperatureValues);
            }
        });

        Btn_Refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Refresh the data obtained from the API
                //resetButtonStates(btnState,isCelsius);
                generateRandomTemperatureList(MIN_TEMP, MAX_TEMP);
                populateListView(weekdays_list, null, temperatureValues);
                Toast.makeText(MainActivity.this,"Temperature Values Refreshed", Toast.LENGTH_SHORT).show();
                //loadTemperatureInfo();
            }
        });

        Btn_Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Exiting Application..." , Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


    SensorManager sensorManager;
    Sensor mAmbientTempSensor;
    @Override
    public void onSensorChanged(SensorEvent event) {
        float ambient_temperature;
        if(event != null)
            ambient_temperature = event.values[0];
        else
            ambient_temperature = (float)0.0;

        ambientTempTextView.setText(String.valueOf(ambient_temperature));
        ambientTempTextView.setTextSize(30);
        ambientTempTextView.setTextColor(Color.RED);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(mAmbientTempSensor!=null)
            sensorManager.registerListener(this,mAmbientTempSensor,SensorManager.SENSOR_DELAY_NORMAL);
        else
            ambientTempTextView.setText("Temperature Sensor Not Found");
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }


    public class listArrayAdapter extends BaseAdapter implements ListAdapter{
        Context context;
        LayoutInflater layoutInflater;

        ArrayList<String> tempValues = new ArrayList<>();
        String[] weekdays = new String[5];
        boolean[] toggleState = new boolean[5];

        TextView tempText;
        TextView weekDayText;
        ToggleButton tempToggle;


        public listArrayAdapter(Context context,String[] weekdayvalues, boolean[] btnStateValues, ArrayList<String> values) {
            //super(context, resource, values);
            this.context = context;
            this.tempValues = values;
            this.weekdays = weekdayvalues;
            if(btnStateValues!=null)
                this.toggleState = btnStateValues;
            else {
                resetButtonStates(toggleState,true);
            }
            layoutInflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);

            btnState = toggleState;
            temperatureValues = tempValues;
        }

        @Override
        public int getCount() {
            return tempValues.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View tempRowView = layoutInflater.inflate(R.layout.custom_list_view, parent, false);

            tempText = (TextView)tempRowView.findViewById(R.id.weekday_temp_text);
            weekDayText = (TextView) tempRowView.findViewById(R.id.weekday_name_text);
            tempToggle = (ToggleButton) tempRowView.findViewById(R.id.cel_fahr_toggle);

            tempText.setText(tempValues.get(position));
            weekDayText.setText(weekdays[position]);
            tempToggle.setChecked(toggleState[position]);

            tempToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    toggleState[position] = !toggleState[position];
                    String str = tempValues.get(position);
                    tempValues.set(position, String.valueOf(tempconvmethod(Double.parseDouble(str), isChecked)));
                    notifyDataSetChanged();
                }
            });
            btnState = toggleState;
            temperatureValues = tempValues;
            return tempRowView;
        }
    }


    private void resetButtonStates(boolean[] inputBtnArray, boolean stateInput)
    {
        for (int i = 0; i < inputBtnArray.length; i++) {
            inputBtnArray[i] = stateInput;
        }
    }
    private void populateListView(String[] wList, boolean[] bList, ArrayList<String> tList){

        listArrayAdapter tempListAdapter = new listArrayAdapter(MainActivity.this,wList,bList, tList);
        tempMonToFriListView.setAdapter(tempListAdapter);
    }

    @Override
    public void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    private void generateRandomTemperatureList(int Min, int Max) {
        temperatureValues.clear();
        for(int i = 0; i < tempList.length; i++)
        {
            tempList[i] = Min + (int)(Math.random() * ((Max - Min) + 1));
            Log.e(TAG, "Temperature is: "+ tempList[i]);
            // To make the temperatures more realistic, the temperature offset can be preset
            temperatureValues.add(Double.toString(tempList[i]));
        }
    }

    private static final String TOGGLE_BTN_STATE = "ToggleButtonState";
    private static final String TEMPERATURE_VALUES = "TemperatureValues";


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) //only when application closed due to OS related reasons
    {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBooleanArray(TOGGLE_BTN_STATE, btnState);
        savedInstanceState.putStringArrayList(TEMPERATURE_VALUES,temperatureValues);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        populateListView(weekdays_list, savedInstanceState.getBooleanArray(TOGGLE_BTN_STATE), savedInstanceState.getStringArrayList(TEMPERATURE_VALUES));
    }
}
