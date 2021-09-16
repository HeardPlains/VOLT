package com.volt.ui.Authentication


import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Build.VERSION_CODES.M
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.transition.FragmentTransitionSupport
import com.volt.MainActivity
import com.volt.R
import com.volt.databinding.FragmentAuthenticationBinding
import com.volt.voltdata.ApiHandler
import com.volt.voltdata.CacheHandler
import com.volt.voltdata.apidata.ActiveTimeSheetData
import com.volt.voltdata.apidata.FinalTimeSheetData
import com.volt.voltdata.appdata.AppHandler
import com.volt.voltdata.appdata.Pages
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.math.roundToInt
import kotlin.random.Random


class AuthenticationFragment : Fragment() {


    private var _binding: FragmentAuthenticationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val apiHandler = ApiHandler()

    @RequiresApi(M)
    override fun onStart() {
        super.onStart()

    }

    @ExperimentalSerializationApi
    @RequiresApi(M)
    fun updatePage() {
        AppHandler.pageUpdate(requireActivity())
        if (AppHandler.connection) {
            CacheHandler.refreshCacheData(requireActivity())
        }
    }


    @RequiresApi(M)
    private fun refreshPage() {
        Thread.sleep(100)
        val fragmentManager = parentFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(
            R.id.nav_host_fragment_activity_main,
            AuthenticationFragment().newInstance()
        )
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    @ExperimentalSerializationApi
    private fun setVisibility(view: View) {
        val manual = view.findViewById(R.id.manualPane) as ConstraintLayout
        manual.visibility = View.VISIBLE
        if (AppHandler.admin) {
            AppHandler.renderEmployeesInSpinner(CacheHandler.getEmployeeCacheList(requireActivity()),
                binding.empSpinner,
                requireActivity())
            AppHandler.renderTasksInSpinner(CacheHandler.getTaskCacheList(requireActivity()),
                binding.taskSpinner,
                requireActivity())
            AppHandler.renderLocationsInSpinner(CacheHandler.getLocationCacheList(requireActivity()),
                binding.locationSpinner,
                requireActivity())
        } else {
            Toast.makeText(
                requireActivity(),
                "Please Enter Foreman ID in the Settings to View Data",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    @RequiresApi(M)
    @ExperimentalSerializationApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {


        _binding = FragmentAuthenticationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.authenticationToggleButton.isChecked = true


        AppHandler.currentPage = Pages.AUTHENTICATION

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

        binding.authenticationToggleButton.setOnClickListener {
            Log.i("TK Button", binding.authenticationToggleButton.isChecked.toString())
        }




        return root
    }


    fun newInstance(): AuthenticationFragment {
        return AuthenticationFragment()
    }

    private fun timeToDouble(time: String): Double {
        Log.i("TK Testing", (time.split(":")[0]))
        val hours = (time.split(":")[0]).toInt()
        val minutes = (time.split(":")[1]).toInt()
        return hours.toDouble() + ((minutes.toDouble() / 60))
    }

    @ExperimentalSerializationApi
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val manual = view.findViewById(R.id.manualPane) as ConstraintLayout
        manual.visibility = View.INVISIBLE
        (activity as MainActivity?)!!.setFragmentRefreshListener(object :
            MainActivity.FragmentRefreshListener {
            override fun onRefresh(string: String) {
                Log.i("TK Admin Check", "Worked Thrice!")
                setVisibility(view)
            }
        })


        if (AppHandler.admin) {
            binding.submitButton.setOnClickListener {
                if ((requireView().findViewById(R.id.editTextTime) as Button).text.toString() == "Enter Time") {
                    Toast.makeText(requireActivity(), "Please Enter a Time!", Toast.LENGTH_LONG)
                        .show()

                } else {
                    if (!binding.authenticationToggleButton.isChecked) {
                        val name =
                            (requireView().findViewById(R.id.empSpinner) as Spinner).selectedItem.toString()
                                .split(" ")
                        val timeSheet = FinalTimeSheetData(
                            name[0],
                            name[1],
                            (timeToDouble((requireView().findViewById(R.id.editTextTime) as Button).text.toString()) * 100).roundToInt() / 100.0
                        )
                        if (CacheHandler.finalSheetLogCheck(requireActivity(), timeSheet)) {
                            if (AppHandler.connection) {
                                apiHandler.postFinalTimeSheetWithTime(timeSheet)
                                Toast.makeText(
                                    requireActivity(),
                                    (requireView().findViewById(R.id.empSpinner) as Spinner).selectedItem.toString() + " Logged Out!",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                CacheHandler.addOffLineSignOut(timeSheet, requireActivity())
                                Toast.makeText(
                                    requireActivity(),
                                    (requireView().findViewById(R.id.empSpinner) as Spinner).selectedItem.toString() + " Logged Out Offline!",
                                    Toast.LENGTH_LONG
                                ).show()
                                CacheHandler.printAllCache(requireActivity())
                                Log.i("TK Testing", CacheHandler.getOfflineSignOutCacheList(requireActivity()).toString())
                            }
                        } else {
                            Toast.makeText(
                                requireActivity(),
                                "${name[0]} ${name[1]} Already In Offline Check Out!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {

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
                        if (CacheHandler.activeSheetLogCheck(requireActivity(), timeSheet)) {
                            if (AppHandler.connection) {
                                apiHandler.postActiveTimeSheetWithTime(timeSheet)
                                Toast.makeText(
                                    requireActivity(),
                                    (requireView().findViewById(R.id.empSpinner) as Spinner).selectedItem.toString() + " Logged In!",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                CacheHandler.addOffLineSignIn(timeSheet, requireActivity())
                                Toast.makeText(
                                    requireActivity(),
                                    (requireView().findViewById(R.id.empSpinner) as Spinner).selectedItem.toString() + " Logged In Offline!",
                                    Toast.LENGTH_LONG
                                ).show()
                                CacheHandler.printAllCache(requireActivity())
                                Log.i("TK Testing", CacheHandler.getOfflineSignInCacheList(requireActivity()).toString())
                            }
                        } else {
                            Toast.makeText(
                                requireActivity(),
                                "${name[0]} ${name[1]} Already Checked In!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
                refreshPage()
            }
        } else {
            Toast.makeText(
                requireActivity(),
                "Please Enter Foreman ID in the Settings to View Data",
                Toast.LENGTH_LONG
            ).show()
        }
        binding.editTextTime.setOnClickListener {
//            val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
//            val cal = Calendar.getInstance()
//            System.out.println(dateFormat.format(cal.time))
            val c: Calendar = Calendar.getInstance()
            val mHour = c.get(Calendar.HOUR)
            val mMinute = c.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                requireActivity(),
                { _, hourOfDay, min ->
                    val hour = if (hourOfDay < 10) "0$hourOfDay" else hourOfDay
                    val minute = if (min < 10) "0$min" else min
                    binding.editTextTime.text = "$hour:$minute:00"
                },
                mHour,
                mMinute,
                false
            )
            timePickerDialog.show()
        }

        updatePage()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}