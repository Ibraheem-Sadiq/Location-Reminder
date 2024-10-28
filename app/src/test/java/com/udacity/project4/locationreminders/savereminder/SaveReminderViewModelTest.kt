package com.udacity.project4.locationreminders.savereminder

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var dataSource: ReminderDataSource
    lateinit var viewModel: SaveReminderViewModel

    @Before
    fun innit() {
        dataSource =FakeDataSource()
        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), dataSource)

    }

    @Test
    fun  ReminderWithNullTitleShowError(){
        var temp = ReminderDataItem("", "desciption 5 ", "location 5 ", 0.0, 0.0)
       runBlocking {  viewModel.validateAndSaveReminder(temp) }
        assert(viewModel.navigationCommand.value ==null)

    }

    fun  cheackValidationWithNullRandomValue(){
        var temp = ReminderDataItem("", "desciption 5 ", "", 0.0, 0.0)
      runBlocking {
          assert( ! viewModel.validateEnteredData(temp))
      }
        assert(viewModel.navigationCommand.value ==null)
    }

    @Test

    fun testSave(){
        var temp = ReminderDataItem("title", "desciption 5 ", "location 5 ", 0.0, 0.0)
       runBlocking { viewModel.saveReminder(temp) }

        assert(viewModel.navigationCommand.value !=null)
    }

    @After
    fun tearDown() {
        stopKoin()
    }
    //TODO: provide testing to the SaveReminderView and its live data objects


}