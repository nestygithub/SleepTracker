package com.chiki.sleeptracker.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SleepDatabaseDao{

    //Getters
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC LIMIT 1")
    fun getTonight(): SleepNight?   //Returns last night

    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC")
    fun getAllNights():LiveData<List<SleepNight>>   //Returns all nights

    @Query("SELECT * FROM daily_sleep_quality_table WHERE nightId = :key")
    fun get(key: Long):SleepNight       // Returns the night with an specific Id

    //Inserts
    @Insert
    fun insert(night: SleepNight)   //Inserts a new night

    //Updates
    @Update
    fun update(night: SleepNight)   //Updates an specific night

    //Deletes
    @Query("DELETE FROM daily_sleep_quality_table")
    fun clear()     //Deletes all night at once
}