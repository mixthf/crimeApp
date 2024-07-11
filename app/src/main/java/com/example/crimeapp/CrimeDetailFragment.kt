package com.example.crimeapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.crimeapp.databinding.FragmentCrimeDetailBinding
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.UUID
import java.util.Date

private const val DATE_FORMAT = "EEE, MMM, dd"
class CrimeDetailFragment : Fragment() {

    //private lateinit var crime : Crime
    private val args: CrimeDetailFragmentArgs by navArgs()
    private val crimeDetailViewModel: CrimeDetailViewModel by viewModels{  //added 1
        CrimeDetailViewModelFactory(args.crimeId)
    }
    private var _binding: FragmentCrimeDetailBinding? = null

    private val binding: FragmentCrimeDetailBinding
        get() = checkNotNull(_binding){
            "Error.  Is the view visible?"
        }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        crime = Crime(
//            id = UUID.randomUUID(),
//            title = "",
//            date = Date(),
//            isSolved = false
//        )
//        Log.d(TAG, "The crime id: ${args.crimeId}")
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCrimeDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply{
            crimeTitle.doOnTextChanged { text, _, _, _->
                //crime = crime.copy(title = text.toString())
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(title = text.toString())
                }
            }

//            crimeDate.apply{
//                //text = crime.date.toString()
//                crimeDate.isEnabled = false
//            }

            crimeSolved.setOnCheckedChangeListener{_, isChecked ->
                //crime = crime.copy(isSolved = isChecked)
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(isSolved = isChecked)
                }
            }

            setFragmentResultListener(DatePickerFragment.REQUEST_KEY_DATE){
                _, bundle ->
                val newDate = bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
                crimeDetailViewModel.updateCrime { it.copy(date = newDate) }
            }

            crimeDelete.setOnClickListener {
                crimeDetailViewModel.crime.value?.let{
                    crime ->
                    crimeDetailViewModel.deleteCrime(crime)
                    findNavController().navigateUp()
                }
            }
        }

        //added 2
        viewLifecycleOwner.lifecycleScope.launch{
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                crimeDetailViewModel.crime.collect{ crime ->
                    crime?.let{updateUi(it)}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //added 3
    private fun updateUi(crime: Crime){
        binding.apply {
            if(crimeTitle.text.toString() != crime.title){
                crimeTitle.setText(crime.title)
            }
            crimeDate.text = crime.date.toString()
            crimeSolved.isChecked = crime.isSolved
            crimeDate.setOnClickListener{
                findNavController().navigate(
                    CrimeDetailFragmentDirections.selectDate(crime.date)
                )
            }
        }
    }



    private fun  getCrimeReport(crime: Crime) : String{
        val solvedString = if(crime.isSolved){
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dateString = android.text.format.DateFormat.format(DATE_FORMAT,crime.date).toString()

        val suspectString = if(crime.suspect.isBlank()){
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }
        return getString(
            R.string.crime_report, crime.title, dateString, solvedString, suspectString
        )
    }
}