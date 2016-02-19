# TempO
##Synopsis
Application to display temperatures for 5 days of the week: Monday to Friday, the ambient temperature information and functionality for the conversion of celsius to fahrenheit and vice versa, using the JNI native c/C++ shared libraries.

##Implementation requirements
1. Random list of numbers to be generated for updating the temperatures for the 5 days.
2. Populate the UI with the weekdays and the temperature values.
3. Include Ambient Temperature display in UI
4. Incorporate JNI Native C++ code , as a shared library for the conversion of Celsius to Fahrenheit and vice versa, for each day (slightly ambiguous)

##Additional implementation
1. To clear ambiguity, one button for each day added to toggle between celsius and fahrenheit, and another button added to change units for all the temperature values.
2. Boolean for including Json implementation.
3. present date and time included in the display.
4. Image Icons for the temperature range added. 2 icons added for now. (one for High temperature and one for low)

##Installation
copy the app-debug.apk file into your phone's storage. Open. install. 

##API Reference
No APIs are used for fetching the temperature data. Random values generated.

##If not random temperature values...
1. Obtain API key for OpenWeatherMaps API, and get the URL for API call
2. httpConnect to the specified URL,& obtain the JsonObjects.
3. Must be done in doInBackGround(...) method of AsyncTask class
4. Option 2: Use android Volley to obtain the library for methods handling JsonArrayRequest etc
