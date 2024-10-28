package com.udacity.project4.util

import android.view.WindowManager
import androidx.test.espresso.Root
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class ToastMatcher() : TypeSafeMatcher<Root>() {
    override fun describeTo(description: Description?) {
        description?.appendText("is Toast")
    }

    override fun matchesSafely(item: Root?): Boolean {
       val type = item?.windowLayoutParams?.get()?.type

        if (type == WindowManager.LayoutParams.TYPE_TOAST)
        {
           val  appToken = item.decorView.applicationWindowToken
            val token = item.decorView.windowToken
            if (token==appToken)
                return  true
        }
        return false
    }

}