package com.codingtest.malabika.tempo;

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
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    //---------------------------------------------------------------------------------------------
    //JNI Native C/C++ Shared Library Implementation - declaring the native functions in the jave file

    //Ignore warnings for the native methods below. Does not affect the functionality of the application

    public native double tempconvmethod(double initialTemp, boolean isC);
    public native double[] tempconvmethodarray(double[] initialTempArray, boolean isC);

    static{
        System.loadLibrary("tempConversion");
    }

    //---------------------------------------------------------------------------------------------

    public final static String TAG = "TEMPO";
    public static final int MIN_TEMP = -20;
    public static final int MAX_TEMP = 20;
    private static final String TOGGLE_BTN_STATE = "ToggleButtonState";
    private static final String TEMPERATURE_VALUES = "temperatureValue_List";

    //Declaration of UI Elements
    ToggleButton Btn_CFToggleAll;
    Button Btn_Exit;
    ImageButton Btn_Refresh;
    TextView ambientTempTextView;
    TextView  dateTimeTextView;
    ListView tempMonToFriListView;


    double[] temperatureValues = new double[5];
    String[] weekdays_list = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
    boolean[] unitToggleStateAll = new boolean[5];
    ArrayList<String> temperatureValue_List = new ArrayList<>();

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
        dateTimeTextView = (TextView) findViewById(R.id.DateTimeText);
        Btn_Refresh = (ImageButton) findViewById(R.id.RefreshDataButton);
        Btn_CFToggleAll = (ToggleButton) findViewById(R.id.toggleAllButton);
        Btn_Exit = (Button) findViewById(R.id.buttonExit);

        SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy", Locale.US);
        dateTimeTextView.setText(sdf.format(new Date()));

        //---------------------------------------------------------------------------------------
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAmbientTempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);


        //Initial Population of the list in UI
        generateRandomTemperatureList(MIN_TEMP, MAX_TEMP);
        populateListView(weekdays_list,null,temperatureValue_List);

        //----------------------------------------------------------------------------------------
        //Button implementation for toggling units between Celsius and Fahrenheit
        //Changes units for all entries in the list
        //----------------------------------------------------------------------------------------

        Btn_CFToggleAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // toggle between the celsius and fahrenheit display of the temperatures in the application
                temperatureValue_List.clear();

                //Calling native method to accept the double array and return the double array with altered units
                temperatureValues = tempconvmethodarray(temperatureValues,isChecked);

                for(int i = 0; i < temperatureValues.length; i++) {
                    temperatureValue_List.add(String.valueOf(temperatureValues[i]));
                }

                resetButtonStates(unitToggleStateAll,isChecked);
                populateListView(weekdays_list,unitToggleStateAll,temperatureValue_List);
            }
        });

        //----------------------------------------------------------------------------------------
        //Refresh Button implementation for resetting the values in the list with new set of Celsius values; updating the units as well
        //todo :Populate the random values of the temperatures keeping in mind the daily setting of the units (either C or F)
        //----------------------------------------------------------------------------------------

        Btn_Refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                generateRandomTemperatureList(MIN_TEMP, MAX_TEMP);
                populateListView(weekdays_list, null, temperatureValue_List);

                Toast.makeText(MainActivity.this,"Temperature Values Refreshed", Toast.LENGTH_SHORT).show();
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

    //---------------------------------------------------------------------------------------------
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
            sensorManager.registerListener(this,mAmbientTempSensor,SensorManager.SENSOR_DELAY_NORMAL); //Register listener to check if data available and changed at the sensor.
        else
            ambientTempTextView.setText(R.string.SensorNotFound);
    }
    //---------------------------------------------------------------------------------------------

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
        boolean[] unitToggleState = new boolean[5];

        TextView temperatureTextView;
        TextView weekDayText;
        ToggleButton Btn_CelsiusFahrToggle;

        //Constructor for custom adapter, taking the present unit values(from the button), and the Temperature values
        public listArrayAdapter(Context context,String[] weekdayvalues, boolean[] btnStateValues, ArrayList<String> values) {

            this.context = context;
            this.tempValues = values;
            this.weekdays = weekdayvalues;

            if(btnStateValues!=null)
                this.unitToggleState = btnStateValues; //Loading previous button state values.
            else {
                resetButtonStates(unitToggleState,true); //if not previous unit selections available, then resetting the states to true; i.e. By default setting units to Celsius
            }

            layoutInflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE); //Get the Layout Inflater

            unitToggleStateAll = unitToggleState;
            temperatureValue_List = tempValues;
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
            temperatureTextView = (TextView)tempRowView.findViewById(R.id.weekday_temp_text);
            weekDayText = (TextView) tempRowView.findViewById(R.id.weekday_name_text);
            Btn_CelsiusFahrToggle = (ToggleButton) tempRowView.findViewById(R.id.cel_fahr_toggle);

            //Populate List with values generated
            temperatureTextView.setText(tempValues.get(position));
            weekDayText.setText(weekdays[position]);
            Btn_CelsiusFahrToggle.setChecked(unitToggleState[position]);


            /**
             * Additional Button implementation:
             * Button against each Day's temperature value entry to toggle each day's view between celsius and fahrenheit
             */
            Btn_CelsiusFahrToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    unitToggleState[position] = !unitToggleState[position]; //Toggle button state
                    String str = tempValues.get(position);
                    tempValues.set(position, String.valueOf(tempconvmethod(Double.parseDouble(str), isChecked))); //Calling the Native C++ function to perform conversion

                    notifyDataSetChanged();
                }
            });

            //Saving temperature values and button states from storing in bundle
            //useful when restoring activity state
            unitToggleStateAll = unitToggleState;
            temperatureValue_List = tempValues;

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
     * @param Min
     * @param Max
     */

    private void generateRandomTemperatureList(int Min, int Max) {
        temperatureValue_List.clear();
        for(int i = 0; i < temperatureValues.length; i++)
        {
            temperatureValues[i] = Min + (int)(Math.random() * ((Max - Min) + 1));
            Log.e(TAG, "Temperature is: "+ temperatureValues[i]);
            temperatureValue_List.add(Double.toString(temperatureValues[i]));
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
        savedInstanceState.putBooleanArray(TOGGLE_BTN_STATE, unitToggleStateAll);
        savedInstanceState.putStringArrayList(TEMPERATURE_VALUES,temperatureValue_List);
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
