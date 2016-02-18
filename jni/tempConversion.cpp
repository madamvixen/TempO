//
// Created by Malabika on 2/17/2016.
//

#include <jni.h>
#include <cmath>
#include <string.h>

extern "C"
jdouble Java_com_codingtest_malabika_tempo_MainActivity_tempconvmethod(JNIEnv *env, jobject obj, jdouble jtempinput, jboolean jToFahrenheit)
{
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

extern "C" //very very very important keyword!!!
jdoubleArray Java_com_codingtest_malabika_tempo_MainActivity_tempconvmethodarray(JNIEnv *env, jobject obj, jdoubleArray jtempinput, jboolean jToFahrenheit)
{
    double *inputArray = env->GetDoubleArrayElements(jtempinput, NULL);

    jsize length = env->GetArrayLength(jtempinput);
    jdouble outputArray[length];

    jfloat tempInitial;
    jfloat tempFinal;

    for(int i = 0; i < length; i++)
    {
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

    env->ReleaseDoubleArrayElements(jtempinput, inputArray, 0); // release resources

    jdoubleArray outJNIArray = env->NewDoubleArray(length);  // allocate
    env->SetDoubleArrayRegion(outJNIArray, 0 , length, outputArray);  // copy
    return outJNIArray;
}
