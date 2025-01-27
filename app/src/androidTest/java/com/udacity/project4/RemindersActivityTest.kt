package com.udacity.project4

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.ToastMatcher
import com.udacity.project4.util.monitorActivity
import com.udacity.project4.utils.EspresoIdlingResources
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get

@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    val  idelResource = DataBindingIdlingResource()
    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }
    @Before
    fun  register(){
        IdlingRegistry.getInstance().register(idelResource)
        IdlingRegistry.getInstance().register(EspresoIdlingResources.countingIdlingResource)
    }

    @After
    fun tearDown(){

        IdlingRegistry.getInstance().unregister(idelResource)
        IdlingRegistry.getInstance().unregister(EspresoIdlingResources.countingIdlingResource)
    }


    @Test
    fun useCase(){
        var activitySenario =  ActivityScenario.launch(RemindersActivity::class.java)
        idelResource.monitorActivity(activitySenario)
        val item =   ReminderDTO("title 1", "desciption 5 ", "location 5 ", 0.0, 0.0)
        onView(withId(R.id.reminderssRecyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.saveReminder)).check(matches(isDisplayed()))
        onView(withId(R.id.saveReminder)).perform(click())
        onView(withText(R.string.err_enter_title)).check(matches(isDisplayed()))
        onView(withId(R.id.reminderTitle)).perform(typeText(item.title))
        onView(withId(R.id.reminderDescription)).perform(typeText(item.description))
        Espresso.closeSoftKeyboard()
        Thread.sleep(3000)
        onView(withId(R.id.saveReminder)).perform(click())
        onView(withText(R.string.err_select_location)).check(matches(isDisplayed()))
        onView(withId(R.id.selectLocation)).perform(click())
        Thread.sleep(3000)
        onView(withId(R.id.map_fragment)).perform(longClick())
        onView(withId(R.id.floatingActionButton)).perform(click())
        onView(withId(R.id.saveReminder)).perform(click())
        onView(withId(R.id.reminderssRecyclerView)).check(matches(isDisplayed()))
        onView(withText(R.string.reminder_saved)).inRoot(ToastMatcher().apply { matches(isDisplayed()) })
        onView(withText(item.title)).check(matches( isDisplayed()))
        onView(withText(item.description)).check(matches( isDisplayed()))
        activitySenario.close()

    }

}
