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

    //Ignore warnings for the native methods below. Doesnot affect the functionality of the application
    public native double tempconvmethod(double initialTemp, boolean isC);
    public native double[] tempconvmethodarray(double[] initialTempArray, boolean isC);

    static{
        System.loadLibrary("tempConversion");
    }

    public final static String TAG = "TEMPO";
    public static final int MIN_TEMP = -20;
    public static final int MAX_TEMP = 20;

    private static final String TOGGLE_BTN_STATE = "ToggleButtonState";
    private static final String TEMPERATURE_VALUES = "TemperatureValues";


    //Declaration of UI Elements
    ToggleButton Btn_CFToggleAll;
    Button Btn_Exit;
    ImageButton Btn_Refresh;
    TextView ambientTempTextView;
    TextView  cityNameTextView;
    ListView tempMonToFriListView;


    double[] tempList = new double[5];
    String[] weekdays_list = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
    boolean[] btnState = new boolean[5];
    ArrayList<String> temperatureValues = new ArrayList<>();

    SensorManager sensorManager;
    Sensor mAmbientTempSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //---------------------------------------------------------------------------------------
        //Initialisation of UI elements

        ambientTempTextView = (TextView) findViewById(R.id.AmbientTempText);  // TextView for display of Ambient Temperature
        tempMonToFriListView = (ListView) findViewById(R.id.MontoFriTempList);  // LIst View displaying the temperature for the week (mon to fri)
        cityNameTextView = (TextView) findViewById(R.id.LocationNameText);
        Btn_Refresh = (ImageButton) findViewById(R.id.RefreshDataButton);
        Btn_CFToggleAll = (ToggleButton) findViewById(R.id.toggleAllButton);
        Btn_Exit = (Button) findViewById(R.id.buttonExit);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
        String currentDateandTime = sdf.format(new Date());
        String cityName = "NEW BRUNSWICK "+ "\n\n NEW JERSEY" + "\n\n"+ currentDateandTime;
        cityNameTextView.setText(cityName);

        //---------------------------------------------------------------------------------------
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAmbientTempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);


        //Initial Population of the list in UI
        generateRandomTemperatureList(MIN_TEMP, MAX_TEMP);
        populateListView(weekdays_list,null,temperatureValues);

        //----------------------------------------------------------------------------------------
        //Button implementation for toggling units between Celsius and Fahrenheit
        //Changes units for all entries in the list
        //----------------------------------------------------------------------------------------

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

        //----------------------------------------------------------------------------------------
        //Refresh Button implementation for resetting the values in the list with new set of Celsius values; updating the units as well
        //todo :Populate the random values of the temperatures keeping in mind the daily setting of the units
        //----------------------------------------------------------------------------------------

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

        //---------------------------------------------------------------------------------------
        //Exit Button for ending Main Activity

        Btn_Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Exiting Application..." , Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * Implementation of the Ambient Temperature measurement by external temperature sensors
     * The following methods are implemented assuming the sensor exists.
     * Null checks are placed wherever necessary.
     *
     * @param event
     */

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
        //Doing Nothing here
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

    /**
     * Custom Array Aadapter Implementation:
     * To incorporate button to change celsius to Fahrenheit for each day
     * and incorporate text box to display temperature as well.
     *
     */

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

            this.context = context;
            this.tempValues = values;
            this.weekdays = weekdayvalues;
            if(btnStateValues!=null)
                this.toggleState = btnStateValues; //Loading previous button state values.
            else {
                resetButtonStates(toggleState,true); //resetting the states to true; i.e. By default setting units to Celsius
            }

            layoutInflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE); //Get the Layout Inflater

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

            //Initialise UI elements for the custom list view
            tempText = (TextView)tempRowView.findViewById(R.id.weekday_temp_text);
            weekDayText = (TextView) tempRowView.findViewById(R.id.weekday_name_text);
            tempToggle = (ToggleButton) tempRowView.findViewById(R.id.cel_fahr_toggle);

            //Populate List with values generated
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

    //Method: To reset the button states to indicate celsius or fahrenheit units
    private void resetButtonStates(boolean[] inputBtnArray, boolean stateInput)
    {
        for (int i = 0; i < inputBtnArray.length; i++) {
            inputBtnArray[i] = stateInput;
        }
    }

    /**
     * PopulateListView:
     * Takes input the list of weekdays, the units chosen for each of the days by the user, and the corresponding list of temperature values.
     * List View generated by using the custom list adpater defined above.
     *
     * @param wList
     * @param bList
     * @param tList
     */
    private void populateListView(String[] wList, boolean[] bList, ArrayList<String> tList){

        listArrayAdapter tempListAdapter = new listArrayAdapter(MainActivity.this,wList,bList, tList);
        tempMonToFriListView.setAdapter(tempListAdapter);
    }

    /**
     * Method to generate random list of numbers for the 5 days of the week
     * //Todo: To make it look closer to the real-time temperature values, range with low standarddeviation
     *
     * @param Min
     * @param Max
     */

    private void generateRandomTemperatureList(int Min, int Max) {
        temperatureValues.clear();
        for(int i = 0; i < tempList.length; i++)
        {
            tempList[i] = Min + (int)(Math.random() * ((Max - Min) + 1));
            Log.e(TAG, "Temperature is: "+ tempList[i]);
            temperatureValues.add(Double.toString(tempList[i]));
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) 
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


    @Override
    public void onStart()
    {
        super.onStart();
    }


}
