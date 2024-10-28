package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {
    lateinit var database: RemindersDatabase

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @Before
    fun init() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

    }

    @Test
    fun testAdd(){
       var temp =  ReminderDTO("title 1  ", "desciption 5 ", "location 5 ", 0.0, 0.0)
        runBlocking {
            database.reminderDao().saveReminder(temp)
        }
        assert(! runBlocking { database.reminderDao().getReminders().isEmpty() })

    }
   @Test
    fun testgetElementByIdToAddedElement(){
       var temp =  ReminderDTO("title 1  ", "desciption 5 ", "location 5 ", 0.0, 0.0)
       runBlocking {
           database.reminderDao().saveReminder(temp)

       }

       var result = runBlocking { database.reminderDao().getReminderById(temp.id) }
       assert(result != null)

   }

    @Test

    fun testgetElementByIdToElement(){
        var temp =  ReminderDTO("title 1  ", "desciption 5 ", "location 5 ", 0.0, 0.0)
        var result = runBlocking { database.reminderDao().getReminderById(temp.id) }
        assert(result == null)

    }


    @Test
    fun testgetElemnts(){
      var list =  mutableListOf<ReminderDTO>(
            ReminderDTO("title 1  ", "desciption 5 ", "location 5 ", 0.0, 0.0),
            ReminderDTO("title 2  ", "desciption 6 ", "location 6 ", 0.0, 0.0),
            ReminderDTO("title 3  ", "desciption 7 ", "location 7 ", 0.0, 0.0),
            ReminderDTO("title 4  ", "desciption 8 ", "location 8 ", 0.0, 0.0),
            ReminderDTO("title 5  ", "desciption 9 ", "location 9 ", 0.0, 0.0)


        )

        for (it in  list)
        {

          runBlocking {  database.reminderDao().saveReminder(it) }
        }

        var result = runBlocking { database.reminderDao().getReminders() }

        assert(list.size.equals(result.size))

        assert(result.containsAll(list))



    }

    @Test
    fun testDelete(){

        var list =  mutableListOf<ReminderDTO>(
            ReminderDTO("title 1  ", "desciption 5 ", "location 5 ", 0.0, 0.0),
            ReminderDTO("title 2  ", "desciption 6 ", "location 6 ", 0.0, 0.0),
            ReminderDTO("title 3  ", "desciption 7 ", "location 7 ", 0.0, 0.0),
            ReminderDTO("title 4  ", "desciption 8 ", "location 8 ", 0.0, 0.0),
            ReminderDTO("title 5  ", "desciption 9 ", "location 9 ", 0.0, 0.0)


        )

        for (it in  list)
        {

            runBlocking {  database.reminderDao().saveReminder(it) }
        }


        runBlocking {
            database.reminderDao().deleteAllReminders()
        }

        var result = runBlocking {  database.reminderDao().getReminders()}
        assert(result.isEmpty())
    }

}