//
// Created by Malabika on 2/17/2016.
//

//#include "tempConversion.h"
#include <jni.h>
#include <cmath>
#include <string.h>

extern "C" //very very very important keyword!!!
jdouble Java_com_codingtest_malabika_tempo_MainActivity_tempconvmethod(JNIEnv *env, jobject obj, jdouble jtempinput, jboolean jToFahrenheit)
{
    jfloat tempInitial;
    jfloat tempFinal;

    tempInitial = (jfloat)jtempinput;
    tempFinal = 0.0;


    if(!jToFahrenheit) //convert from celsius to fahrenheit
    {
        tempFinal = (jfloat)((tempInitial * 1.8) + 32);
        //tempFinal = roundf(tempFinal*100)/100;
    }
    else // convert from fahrenheit to celsius
    {
        tempFinal = (jfloat)((tempInitial - 32)/(1.8));

    }

    return (jdouble)roundf(tempFinal*100)/100;
}
