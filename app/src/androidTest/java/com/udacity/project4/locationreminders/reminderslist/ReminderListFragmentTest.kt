package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorFragment
import com.udacity.project4.utils.EspresoIdlingResources
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    val  idelResource = DataBindingIdlingResource()


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


    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = ApplicationProvider.getApplicationContext()
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
        repository = GlobalContext.get().koin.get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }

    @Test
    fun navigationTest(){
  val senario =  launchFragmentInContainer <ReminderListFragment>(bundleOf(),R.style.AppTheme)
idelResource.monitorFragment(senario)
        val navigator  = mock(NavController::class.java)
        senario.onFragment {
            it.view?.let { it1 -> Navigation.setViewNavController(it1,navigator) }
        }

        onView( withId(R.id.addReminderFAB)).perform(click())
        verify(  navigator).navigate(ReminderListFragmentDirections.toSaveReminder())
    }

    @Test
        fun  displayTest(){

        val item =   ReminderDTO("title 1", "desciption 5 ", "location 5 ", 0.0, 0.0)
        runBlocking { repository.saveReminder(item) }
        Thread.sleep(1000)

        val senario =   launchFragmentInContainer <ReminderListFragment>(bundleOf(),R.style.AppTheme)
        idelResource.monitorFragment(senario)
        onView(ViewMatchers.withText(item.title)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText(item.description)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText(item.location)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )


    }

    @Test
    fun  errorTest(){
        runBlocking { repository.deleteAllReminders() }
        val senario =  launchFragmentInContainer <ReminderListFragment>(bundleOf(),R.style.AppTheme)
        idelResource.monitorFragment(senario)
        onView(withId(R.id.noDataTextView)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }


}