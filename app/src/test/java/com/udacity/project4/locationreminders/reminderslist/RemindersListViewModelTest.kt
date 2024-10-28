package com.udacity.project4.locationreminders.reminderslist

import android.os.Looper.getMainLooper
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.locationreminders.MainCoroutine
import com.udacity.project4.locationreminders.data.FakeDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

 lateinit  var viewModel:RemindersListViewModel

lateinit var  dataSource:FakeDataSource

@ExperimentalCoroutinesApi
    @get:Rule
    val  main = MainCoroutine()

    @Before
fun  init(){
        dataSource = FakeDataSource()
    viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),dataSource)
}

    @After
    fun tearDown(){
        stopKoin()
    }

    @Test
    fun isAllItemsLoaded(){
        runBlocking {
            viewModel.loadReminders()
        }

        val  resultList  = viewModel.remindersList

        val list = viewModel.remindersList.value!!

        assertThat(resultList.value?.size).isEqualTo(list.size)

        for (item in dataSource.data)
        {
            var found =false
            for (temp in list )
                if (temp.id.equals(item.id))
                    found = true
            assert(found)
        }

    }

    @Test
    fun verification(){

       runBlocking {
           viewModel.loadReminders()
       }

        val list = viewModel.remindersList.value!!
        for (item in dataSource.data)
        {
            var  correct =false
            for (temp in list )
                   if (dataSource.areEqual(temp, item))
                       correct=true
            assert(correct)
        }

    }

    @Test
     fun testWithNoData(){
        runBlocking {
            dataSource.deleteAllReminders()
        }

        viewModel.loadReminders()
        assert(viewModel.showNoData.value == true)

    }
    @Test
    fun  loadWithError(){
        dataSource.shouldReturnError =true
         viewModel.loadReminders()
        assertThat(viewModel.showSnackBar.value).isEqualTo("Error")

    }

    @Test
    fun  loadSuccessful(){
        var observer = Observer<Boolean>{}
        main.pauseDispatcher()
        viewModel.loadReminders()
        assertThat(  viewModel.showLoading.value ).isTrue()
         observer = Observer<Boolean>{
            assertThat(  viewModel.showLoading.value ).isFalse()
            viewModel.showLoading.removeObserver(observer)
        }
        shadowOf(getMainLooper()).idle()

        viewModel.showLoading.observeForever(observer)
        main.dispatcher.resumeDispatcher()
        assertThat(viewModel.remindersList.value.isNullOrEmpty()).isFalse()
    }

}