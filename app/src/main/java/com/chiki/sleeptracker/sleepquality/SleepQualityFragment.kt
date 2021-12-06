package com.chiki.sleeptracker.sleepquality

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.chiki.sleeptracker.database.SleepDatabase
import com.chiki.sleeptracker.databinding.FragmentSleepQualityBinding
import com.chiki.sleeptracker.sleeptracker.SleepTrackerViewModelFactory

class SleepQualityFragment : Fragment() {

    //Binding
    private var _binding:FragmentSleepQualityBinding? = null
    private val binding get() = _binding!!

    //Lifecycle Methods
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSleepQualityBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(this.activity).application
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao
        val viewModelFactory = SleepQualityViewModelFactory(dataSource)
        val sleepQualityViewModel = ViewModelProvider(this,viewModelFactory).get(SleepQualityViewModel::class.java)

        binding.sleepQualityViewModel = sleepQualityViewModel
        binding.lifecycleOwner = this

        //Observer
        sleepQualityViewModel.navigateToSleepTrackerFragment.observe(viewLifecycleOwner){navigate->
            if (navigate){
                navigateToSleepTrackerFragment()
                sleepQualityViewModel.doneNavigateToSleepTrackerFragment()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Actions
    private fun navigateToSleepTrackerFragment(){
        val action = SleepQualityFragmentDirections.actionSleepQualityFragmentToSleepTrackerFragment()
        findNavController().navigate(action)
    }
}