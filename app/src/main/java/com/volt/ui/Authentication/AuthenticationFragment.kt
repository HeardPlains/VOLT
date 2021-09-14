package com.volt.ui.Authentication


import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.volt.voltdata.ApiHandler
import com.volt.MainActivity
import com.volt.R
import com.volt.voltdata.apidata.FinalTimeSheetData
import com.volt.databinding.FragmentAuthenticationBinding
import com.volt.ui.check_in.pages.AssignCardFragment
import com.volt.voltdata.CacheHandler
import com.volt.voltdata.apidata.ActiveTimeSheetData
import com.volt.voltdata.apidata.EmployeeData
import com.volt.voltdata.apidata.TaskData
import com.volt.voltdata.appdata.AppHandler
import com.volt.voltdata.appdata.Pages
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.Cache
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random


class AuthenticationFragment : Fragment() {


    private var _binding: FragmentAuthenticationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val apiHandler = ApiHandler()
    private var authenticationToggle = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()
        AppHandler.pageUpdate(requireActivity())
        if (AppHandler.connection){
            CacheHandler.refreshCacheData(requireActivity())
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        AppHandler.pageUpdate(requireActivity())
        if (AppHandler.connection){
            CacheHandler.refreshCacheData(requireActivity())
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @ExperimentalSerializationApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {


        _binding = FragmentAuthenticationBinding.inflate(inflater, container, false)
        val root: View = binding.root


        AppHandler.currentPage = Pages.AUTHENTICATION
        if (AppHandler.admin) {
            AppHandler.renderEmployeesInSpinner(CacheHandler.getEmployeeCacheList(requireActivity()), binding.empSpinner, requireActivity())
            AppHandler.renderTasksInSpinner(CacheHandler.getTaskCacheList(requireActivity()), binding.taskSpinner, requireActivity())
            AppHandler.renderLocationsInSpinner(CacheHandler.getLocationCacheList(requireActivity()),binding.locationSpinner, requireActivity())
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
                id: Long,
            ) {
                val selectedItem =
                    parent.getItemAtPosition(position).toString()
                Log.i("TK Spinner", selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.authenticationToggleButton.setOnClickListener() {
            authenticationToggle = binding.authenticationToggleButton.isActivated
        }




        return root
    }




    fun newInstance(): AssignCardFragment {
        return AssignCardFragment()
    }

    @ExperimentalSerializationApi
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (AppHandler.admin) {
            binding.submitButton.setOnClickListener {
                if (authenticationToggle) {
                    val name =
                        (requireView().findViewById(R.id.empSpinner) as Spinner).selectedItem.toString()
                            .split(" ")
                    val timeSheet = FinalTimeSheetData(name[0], name[1])
                    Log.i("TK Click", timeSheet.toString())
                    Toast.makeText(
                        requireActivity(),
                        (requireView().findViewById(R.id.empSpinner) as Spinner).selectedItem.toString() + " Logged Out!",
                        Toast.LENGTH_LONG
                    ).show()
                    apiHandler.postFinalTimeSheet(timeSheet)
                }else{
                    val name =
                        (requireView().findViewById(R.id.empSpinner) as Spinner).selectedItem.toString()
                            .split(" ")
                    val timeSheet = ActiveTimeSheetData(
                        1,
                        Random.nextInt(1000000, 9999999),
                        name[0],
                        name[1],
                        (requireView().findViewById(R.id.editTextTime) as Button).text.toString(),
                        (requireView().findViewById(R.id.locationSpinner) as Spinner).selectedItem.toString(),
                        (requireView().findViewById(R.id.taskSpinner) as Spinner).selectedItem.toString(),
                        7
                    )
                    if(AppHandler.connection) {
                        apiHandler.postActiveTimeSheet(timeSheet)
                    }else{
                        CacheHandler.addOffLineSignIn(timeSheet,requireActivity())
                    }
                }

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