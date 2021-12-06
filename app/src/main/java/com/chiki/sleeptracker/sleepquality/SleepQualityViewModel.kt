package com.chiki.sleeptracker.sleepquality

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chiki.sleeptracker.database.SleepDatabaseDao
import kotlinx.coroutines.*

class SleepQualityViewModel(val database: SleepDatabaseDao):ViewModel(){

    //Coroutines
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //Event
    private var _navigateToSleepTrackerFragment = MutableLiveData<Boolean>()
    val navigateToSleepTrackerFragment:LiveData<Boolean> get() = _navigateToSleepTrackerFragment

    //Lifecycle
    init {

    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    //Buttons
    fun onQuality0(){updateQuality(0)}
    fun onQuality1(){updateQuality(1)}
    fun onQuality2(){updateQuality(2)}
    fun onQuality3(){updateQuality(3)}
    fun onQuality4(){updateQuality(4)}
    fun onQuality5(){updateQuality(5)}

    //Actions
    fun doneNavigateToSleepTrackerFragment(){
        _navigateToSleepTrackerFragment.value = false
    }
    private fun updateQuality(quality:Int){
        uiScope.launch {
            updateQualityNight(quality)
            _navigateToSleepTrackerFragment.value = true
        }
    }
    private suspend fun updateQualityNight(quality:Int){
        withContext(Dispatchers.IO){
            val tonight = database.getTonight()
            if(tonight!=null){
                tonight.sleepQuality = quality
                database.update(tonight)
            }
        }
    }
}

class SleepQualityViewModelFactory(private val dataSource: SleepDatabaseDao) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SleepQualityViewModel::class.java)) {
            return SleepQualityViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}