//
// Created by Malabika on 2/17/2016.
//

#include <jni.h>
#include <cmath>
#include <string.h>

//--------------------------------------------------------------------------------------------------

extern "C"
jdoubleArray Java_com_codingtest_malabika_tempo_MainActivity_tempconvmethodarray(JNIEnv *env, jobject obj, jdoubleArray jtempinput, jboolean jToFahrenheit)
{
    //receive primitive double array elements
    double *inputArray = env->GetDoubleArrayElements(jtempinput, NULL);

    jsize length = env->GetArrayLength(jtempinput);
    jdouble outputArray[length];

    jfloat tempInitial;
    jfloat tempFinal;

    for(int i = 0; i < length; i++)
    {
        /**
         * Calling functionality for Celsius to Fahrenheit and viceversa
         * //Inefficient: Functionalities being called twice
         * //todo: Increase Modularity; Add function to do the conversion and reuse
         */
        tempInitial = (jfloat)inputArray[i];
        tempFinal = 0.0;

        if(!jToFahrenheit) //convert from celsius to fahrenheit
        {
            tempFinal = (jfloat)((tempInitial * 1.8) + 32);
        }
        else // convert from fahrenheit to celsius
        {
            tempFinal = (jfloat)((tempInitial - 32)/(1.8));
        }
        outputArray[i] = (jdouble)roundf(tempFinal *100)/100;
    }

    //Releasing the elements we obtained using "Get..."
    env->ReleaseDoubleArrayElements(jtempinput, inputArray, 0);

    jdoubleArray outJNIArray = env->NewDoubleArray(length);
    env->SetDoubleArrayRegion(outJNIArray, 0 , length, outputArray); //Creating a copy of the computed array into the output array

    return outJNIArray;

}
//End of method

//---------------------------------------------------------------------------------------------------
extern "C"
jdouble Java_com_codingtest_malabika_tempo_MainActivity_tempconvmethod(JNIEnv *env, jobject obj, jdouble jtempinput, jboolean jToFahrenheit)
{
    //Accept the temperature values as passed from the MainActivity.java file.
    //Copy into local variables.
    //Float used to have control on precision. UI will be messy if large decimal numbers are displayed

    jfloat tempInitial;
    jfloat tempFinal;

    tempInitial = (jfloat)jtempinput;
    tempFinal = 0.0;

    if(!jToFahrenheit) //convert from celsius to fahrenheit
    {
        tempFinal = (jfloat)((tempInitial * 1.8) + 32);
    }
    else // convert from fahrenheit to celsius
    {
        tempFinal = (jfloat)((tempInitial - 32)/(1.8));

    }

    return (jdouble)roundf(tempFinal*100)/100; //returning the changed units of temperature

}
//End of method