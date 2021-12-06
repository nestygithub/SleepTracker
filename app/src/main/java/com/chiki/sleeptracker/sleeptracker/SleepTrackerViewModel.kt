package com.chiki.sleeptracker.sleeptracker

import android.app.Application
import androidx.lifecycle.*
import com.chiki.sleeptracker.database.SleepDatabaseDao
import com.chiki.sleeptracker.database.SleepNight
import com.chiki.sleeptracker.formatNights
import kotlinx.coroutines.*

class SleepTrackerViewModel(private val database: SleepDatabaseDao, application: Application):AndroidViewModel(application) {

    //Coroutines
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //States
    private var tonight = MutableLiveData<SleepNight?>()
    val nights = database.getAllNights()
    val startButtonVisible = Transformations.map(tonight){
        it == null
    }
    val stopButtonVisible = Transformations.map(tonight){
        it != null
    }
    val clearButtonVisible = Transformations.map(nights){
        nights.value?.isNotEmpty()
    }

    //Events
    private var _navigateToSleepQualityFragment = MutableLiveData<SleepNight>()         //Navigate to the Sleep Quality Fragment
    val navigateToSleepQualityFragment:LiveData<SleepNight> get() = _navigateToSleepQualityFragment
    private var _showSnackBarClearMessage = MutableLiveData<Boolean>()                  //Show a message in the snack bar when the database was cleared
    val showSnackBarClearMessage:LiveData<Boolean> get() = _showSnackBarClearMessage
    private var _navigateToSleepDetailFragment = MutableLiveData<SleepNight>()
    val navigateToSleepDetailFragment:LiveData<SleepNight> get() = _navigateToSleepDetailFragment


    //Lifecycle
    init {
        initializeTonight()
    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    //Buttons
    fun onStartTracking(){
        uiScope.launch {
            val newNight = SleepNight()
            insert(newNight)
            tonight.value = getTonightFromDatabase()
        }
    }
    fun onStopTracking(){
        uiScope.launch {
            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
            _navigateToSleepQualityFragment.value = oldNight
        }
    }
    fun onClear(){
        uiScope.launch {
            clear()
            tonight.value = null
            _showSnackBarClearMessage.value = true
        }
    }
    fun onSleepNightSelected(sleepNight: SleepNight){
        _navigateToSleepDetailFragment.value = sleepNight
    }

    //Actions
    fun doneNavigateToSleepQualityFragment(){
        _navigateToSleepQualityFragment.value = null
    }
    fun doneShowSnackBarClearMessage(){
        _showSnackBarClearMessage.value = false
    }
    fun doneNavigateToSleepDetailFragment(){
        _navigateToSleepDetailFragment.value = null
    }
    private fun initializeTonight() {
        uiScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }
    private suspend fun getTonightFromDatabase(): SleepNight? {
        return withContext(Dispatchers.IO){
            var night = database.getTonight()
            if(night?.startTimeMilli != night?.endTimeMilli){
                night = null
            }
            night
        }
    }
    private suspend fun insert(night: SleepNight){
        withContext(Dispatchers.IO){
            database.insert(night)
        }
    }
    private suspend fun update(oldNight: SleepNight){
        withContext(Dispatchers.IO) {
            database.update(oldNight)
        }
    }
    private suspend fun clear(){
        withContext(Dispatchers.IO){
            database.clear()
        }
    }

}

class SleepTrackerViewModelFactory(private val dataSource: SleepDatabaseDao, private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SleepTrackerViewModel::class.java)) {
            return SleepTrackerViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
