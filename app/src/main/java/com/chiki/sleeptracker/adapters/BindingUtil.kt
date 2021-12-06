package com.chiki.sleeptracker.adapters

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.chiki.sleeptracker.R
import com.chiki.sleeptracker.convertDurationToFormatted
import com.chiki.sleeptracker.convertLongToDateString
import com.chiki.sleeptracker.convertNumericQualityToString
import com.chiki.sleeptracker.database.SleepNight

@BindingAdapter("sleepDurationFormatted")
fun TextView.setSleepDurationFormatted(sleepNight:SleepNight?){
    sleepNight?.let {
        text = convertDurationToFormatted(sleepNight.startTimeMilli,sleepNight.endTimeMilli)
    }
}

@BindingAdapter("sleepQualityString")
fun TextView.setSleepQualityString(sleepNight: SleepNight?){
    sleepNight?.let {
        text = convertNumericQualityToString(sleepNight.sleepQuality, resources)
    }
}

@BindingAdapter("sleepImage")
fun ImageView.setSleepImage(sleepNight: SleepNight?){
    sleepNight?.let {
        setImageResource(when(sleepNight.sleepQuality){
            0-> R.drawable.ic_sleep_0
            1-> R.drawable.ic_sleep_1
            2-> R.drawable.ic_sleep_2
            3-> R.drawable.ic_sleep_3
            4-> R.drawable.ic_sleep_4
            5-> R.drawable.ic_sleep_5
            else-> R.drawable.ic_launcher_sleep_tracker_foreground
        })
    }
}

@BindingAdapter("sleepFormattedStartTime")
fun TextView.setSleepFormattedStartTime(sleepNight: SleepNight?){
    sleepNight?.let {
        text = convertLongToDateString(it.startTimeMilli)
    }
}
@BindingAdapter("sleepFormattedEndTime")
fun TextView.setSleepFormattedEndTime(sleepNight: SleepNight?){
    sleepNight?.let {
        text = convertLongToDateString(it.endTimeMilli)
    }
}