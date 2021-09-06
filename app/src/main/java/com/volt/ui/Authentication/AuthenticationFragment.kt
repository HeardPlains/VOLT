package com.volt.ui.Authentication


import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.volt.voltdata.ApiHandler
import com.volt.MainActivity
import com.volt.R
import com.volt.voltdata.apidata.FinalTimeSheetJson
import com.volt.databinding.FragmentAuthenticationBinding
import com.volt.ui.check_in.pages.AssignCardFragment
import com.volt.voltdata.appdata.AppHandler
import com.volt.voltdata.appdata.Pages


class AuthenticationFragment : Fragment() {


    private var _binding: FragmentAuthenticationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val apiHandler = ApiHandler()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAuthenticationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        AppHandler.currentPage = Pages.AUTHENTICATION
        if ((activity as MainActivity?)?.getAdmin() == true) {
            apiHandler.renderEmployeesInSpinner(requireActivity(), binding.empSpinner)
            apiHandler.renderTasksInSpinner(requireActivity(),binding.taskSpinner)
        } else {
            Toast.makeText(
                requireActivity(),
                "Please Enter Foreman ID in the Settings to View Data",
                Toast.LENGTH_LONG
            ).show()
        }

        binding.empSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem =
                    parent.getItemAtPosition(position).toString()
                Log.i("TK Spinner", selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

     //   binding.manualPane.isVisible = false



        return root
    }

    fun newInstance(): AssignCardFragment {
        return AssignCardFragment()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if ((activity as MainActivity?)?.getAdmin() == true) {
            binding.button.setOnClickListener {
                val name =
                    (requireView().findViewById(R.id.empSpinner) as Spinner).selectedItem.toString()
                        .split(" ")
                val timeSheet = FinalTimeSheetJson(name[0], name[1])
                Log.i("TK Click", timeSheet.toString())
                Toast.makeText(
                    requireActivity(),
                    (requireView().findViewById(R.id.empSpinner) as Spinner).selectedItem.toString() + " Logged Out!",
                    Toast.LENGTH_LONG
                ).show()

                apiHandler.postFinalTimeSheet(timeSheet)
            }
        } else {
            Toast.makeText(
                requireActivity(),
                "Please Enter Foreman ID in the Settings to View Data",
                Toast.LENGTH_LONG
            ).show()
        }
        binding.editTextTime.setOnClickListener() {
            val c: Calendar = Calendar.getInstance()
            val mHour = c.get(Calendar.HOUR_OF_DAY)
            val mMinute = c.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                requireActivity(),
                { _, hourOfDay, minute ->
                    binding.editTextTime.text = "$hourOfDay:$minute"
                },
                mHour,
                mMinute,
                false
            )
            timePickerDialog.show()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}