package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {
   var shouldReturnError = false
   var data:MutableList<ReminderDTO>

    init {

        data = mutableListOf<ReminderDTO>(
            ReminderDTO("title 1  ", "desciption 5 ", "location 5 ", 0.0, 0.0),
            ReminderDTO("title 2  ", "desciption 6 ", "location 6 ", 0.0, 0.0),
            ReminderDTO("title 3  ", "desciption 7 ", "location 7 ", 0.0, 0.0),
            ReminderDTO("title 4  ", "desciption 8 ", "location 8 ", 0.0, 0.0),
            ReminderDTO("title 5  ", "desciption 9 ", "location 9 ", 0.0, 0.0)


        )
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError)
            return  Result.Error("Error")
       return  Result.Success(data)
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        data.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError)
            return  Result.Error("Error")
        for (item in data)
        {
            if (item.id.equals(id))
                return  Result.Success(item)
        }
        return  Result.Error("Reminder not found!")
    }

    override suspend fun deleteAllReminders() {
      data.clear()
    }


    fun areEqual(temp: ReminderDataItem, item:ReminderDTO):Boolean{
        if (temp.id.equals(item.id))
            if (temp.title.equals(item.title))
                if (temp.description.equals(item.description))
                    if (temp.location.equals(item.location))
                        if (temp.latitude?.equals(item.latitude) ==true)
                            if (temp.longitude?.equals(item.longitude) ==true)
                                return true

        return false
    }


}