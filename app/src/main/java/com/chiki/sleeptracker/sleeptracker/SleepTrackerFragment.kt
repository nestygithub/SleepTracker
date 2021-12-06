package com.chiki.sleeptracker.sleeptracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.chiki.sleeptracker.adapters.DataItem
import com.chiki.sleeptracker.adapters.SleepNightAdapter
import com.chiki.sleeptracker.convertDurationToFormatted
import com.chiki.sleeptracker.database.SleepDatabase
import com.chiki.sleeptracker.database.SleepNight
import com.chiki.sleeptracker.databinding.FragmentSleepTrackerBinding
import com.google.android.material.snackbar.Snackbar

class SleepTrackerFragment : Fragment() {

    //Binding
    private var _binding: FragmentSleepTrackerBinding? = null
    private val binding get() = _binding!!

    //Lifecycle
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSleepTrackerBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(this.activity).application
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao
        val viewModelFactory = SleepTrackerViewModelFactory(dataSource,application)
        val sleepTrackerViewModel = ViewModelProvider(this,viewModelFactory).get(SleepTrackerViewModel::class.java)

        binding.sleepTrackerViewModel = sleepTrackerViewModel
        binding.lifecycleOwner = this

        //RecycleView and Adapter
        val adapter = SleepNightAdapter{
            if(it is DataItem.SleepNightItem) sleepTrackerViewModel.onSleepNightSelected(it.sleepNight)
        }
        binding.recyclerView.adapter = adapter
        val manager = GridLayoutManager(requireContext(),3)
        manager.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                return when(position){
                    0 -> 3
                    else ->1
                }
            }

        }
        binding.recyclerView.layoutManager = manager

        //Observers
        sleepTrackerViewModel.nights.observe(viewLifecycleOwner){
            adapter.addHeaderAndSubmitList(it)
        }
        sleepTrackerViewModel.navigateToSleepQualityFragment.observe(viewLifecycleOwner){currentNight->
            if(currentNight!=null){
                navigateToSleepQualityFragment()
                sleepTrackerViewModel.doneNavigateToSleepQualityFragment()
            }
        }
        sleepTrackerViewModel.showSnackBarClearMessage.observe(viewLifecycleOwner){showMessage->
            if(showMessage){
                showSnackBarClearMessage()
                sleepTrackerViewModel.doneShowSnackBarClearMessage()
            }
        }
        sleepTrackerViewModel.navigateToSleepDetailFragment.observe(viewLifecycleOwner){sleepNight->
            if(sleepNight!=null){
                navigateToSleepDetailFragment(sleepNight)
                sleepTrackerViewModel.doneNavigateToSleepDetailFragment()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Actions
    private fun navigateToSleepDetailFragment(it: SleepNight) {
        val action =
            SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepDetailFragment(it)
        findNavController().navigate(action)
    }
    private fun navigateToSleepQualityFragment(){
        val action = SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepQualityFragment()
        findNavController().navigate(action)
    }
    private fun showSnackBarClearMessage(){
        Snackbar.make(requireActivity().findViewById(android.R.id.content),"All your data is gone forever!",Snackbar.LENGTH_SHORT).show()
    }
}