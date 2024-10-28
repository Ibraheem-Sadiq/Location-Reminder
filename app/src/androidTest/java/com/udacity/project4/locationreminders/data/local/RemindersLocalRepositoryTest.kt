package com.udacity.project4.locationreminders.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    lateinit var repository: RemindersLocalRepository
    lateinit var database: RemindersDatabase

    @Before
    fun init() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @Test
    fun testAdd() {
        var temp = ReminderDTO("title 1  ", "desciption 5 ", "location 5 ", 0.0, 0.0)

        runBlocking { repository.saveReminder(temp) }

        var result = runBlocking { repository.getReminders() }

        result as com.udacity.project4.locationreminders.data.dto.Result.Success
        assert(!result.data.isEmpty())
        assert(result.data.size == 1)
    }

    fun testDelete() {
        var list = mutableListOf<ReminderDTO>(
            ReminderDTO("title 1  ", "desciption 5 ", "location 5 ", 0.0, 0.0),
            ReminderDTO("title 2  ", "desciption 6 ", "location 6 ", 0.0, 0.0),
            ReminderDTO("title 3  ", "desciption 7 ", "location 7 ", 0.0, 0.0),
            ReminderDTO("title 4  ", "desciption 8 ", "location 8 ", 0.0, 0.0),
            ReminderDTO("title 5  ", "desciption 9 ", "location 9 ", 0.0, 0.0)


        )

        for (it in list) {

            runBlocking { repository.saveReminder(it) }
        }

        runBlocking {
            repository.deleteAllReminders()
        }


        var result = runBlocking { repository.getReminders() }

        result as com.udacity.project4.locationreminders.data.dto.Result.Success
        assert(result.data.isEmpty())
    }

    @Test
    fun testGetReminders() {
        var list = mutableListOf<ReminderDTO>(
            ReminderDTO("title 1  ", "desciption 5 ", "location 5 ", 0.0, 0.0),
            ReminderDTO("title 2  ", "desciption 6 ", "location 6 ", 0.0, 0.0),
            ReminderDTO("title 3  ", "desciption 7 ", "location 7 ", 0.0, 0.0),
            ReminderDTO("title 4  ", "desciption 8 ", "location 8 ", 0.0, 0.0),
            ReminderDTO("title 5  ", "desciption 9 ", "location 9 ", 0.0, 0.0)


        )

        for (it in list) {

            runBlocking { repository.saveReminder(it) }
        }


        var result = runBlocking { repository.getReminders() }

        result as com.udacity.project4.locationreminders.data.dto.Result.Success
        assert(!result.data.isEmpty())
        assert(result.data.size == 5)

    }

    @Test
    fun testItemNotFound() {
        runBlocking { repository.deleteAllReminders() }
        var result = runBlocking { repository.getReminder("1") }

        assert(result.javaClass.isInstance(com.udacity.project4.locationreminders.data.dto.Result.Error::class.java))
        result = result as Result.Error
        assert(result.message.equals("Reminder not found!"))

    }


}