package com.udacity.project4.utils

import androidx.test.espresso.idling.CountingIdlingResource

object EspresoIdlingResources {
@JvmField
    val  countingIdlingResource=CountingIdlingResource("GLOBAL")
    fun  increament(){
        countingIdlingResource.increment()
    }
    fun decreament(){
        if (!countingIdlingResource.isIdleNow)
            countingIdlingResource.decrement()
    }

    inline fun  <T> wrapEspressoIdlingResource(function:()->T):T{
        EspresoIdlingResources.increament()
        return try {
function()
        }
        finally {
            EspresoIdlingResources.decreament()
        }
    }
}