package com.chiki.sleeptracker.sleepdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.chiki.sleeptracker.R
import com.chiki.sleeptracker.databinding.FragmentSleepDetailBinding
import java.util.ArrayList

class SleepDetailFragment : Fragment() {

    //Binding
    private var _binding: FragmentSleepDetailBinding? = null
    private val binding get() = _binding!!

    //Lifecycle
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSleepDetailBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sleepNight = SleepDetailFragmentArgs.fromBundle(requireArguments()).sleepNight
        val sleepDetailViewModelFactory = SleepDetailViewModelFactory(sleepNight)
        val sleepDetailViewModel = ViewModelProvider(this,sleepDetailViewModelFactory).get(SleepDetailViewModel::class.java)

        binding.sleepDetailViewModel = sleepDetailViewModel
        binding.lifecycleOwner = this

        //Observers
        sleepDetailViewModel.navigateToSleepTracker.observe(viewLifecycleOwner){navigate->
            if (navigate){
                navigateToSleepTrackerFragment()
                sleepDetailViewModel.doneNavigateToSleepTracker()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Actions
    private fun navigateToSleepTrackerFragment(){
        val action = SleepDetailFragmentDirections.actionSleepDetailFragmentToSleepTrackerFragment()
        findNavController().navigate(action)
    }
}