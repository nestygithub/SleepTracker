package com.chiki.sleeptracker.sleepdetail

import androidx.lifecycle.*
import com.chiki.sleeptracker.database.SleepNight

class SleepDetailViewModel(val sleepNight: SleepNight):ViewModel() {

    //States
    private var _currentSleepNight = MutableLiveData<SleepNight>()
    val currentSleepNight:LiveData<SleepNight> get() = _currentSleepNight
    //Events
    private var _navigateToSleepTracker = MediatorLiveData<Boolean>()
    val navigateToSleepTracker:LiveData<Boolean> get() = _navigateToSleepTracker

    //Lifecycle
    init {
        _currentSleepNight.value = sleepNight
    }

    //Buttons
    fun onClose(){
        _navigateToSleepTracker.value = true
    }

    //Actions
    fun doneNavigateToSleepTracker(){
        _navigateToSleepTracker.value = false
    }
}

class SleepDetailViewModelFactory(val sleepNight: SleepNight) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SleepDetailViewModel::class.java)) {
            return SleepDetailViewModel(sleepNight) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}