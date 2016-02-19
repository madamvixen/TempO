# TempO
##Synopsis
Application to display temperatures for 5 days of the week: Monday to Friday, the ambient temperature information and functionality for the conversion of celsius to fahrenheit and vice versa, using the JNI native c/C++ shared libraries.

##Implementation requirements
1. Random list of numbers to be generated for updating the temperatures for the 5 days.
2. Populate the UI with the weekdays and the temperature values.
3. Include Ambient Temperature display in UI
4. Incorporate JNI Native C++ code , as a shared library for the conversion of Celsius to Fahrenheit and vice versa, for each day (slightly ambiguous)

##Additions
1. To clear ambiguity, one button for each day added to toggle between celsius and fahrenheit, and another button added to change units for all the temperature values.
2. Boolean for including Json implementation. Idea explained later.
3. Name of the city, and present date and time included in the display.

##Installation
copy the app-debug.apk file into your phone's storage. Open. install. 

##API Reference
No APIs are used for fetching the temperature data. But one link I have found very useful and at the same time, have been able to get upto 5 days forecast, is OpenWeatherMaps API.


