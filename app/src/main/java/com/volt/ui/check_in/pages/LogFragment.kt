package com.volt.ui.check_in.pages


import android.content.Context
import android.graphics.Color
import android.media.MediaExtractor
import android.net.ConnectivityManager
import android.os.Build
import android.os.Build.VERSION_CODES.M
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.ViewGroup
import android.widget.*
import android.widget.TableLayout
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.tabs.TabLayout
import com.volt.R
import com.volt.databinding.FragmentLogBinding
import com.volt.voltdata.ApiHandler
import com.volt.voltdata.CacheHandler
import com.volt.voltdata.apidata.ActiveTimeSheetData
import com.volt.voltdata.apidata.FinalTimeSheetData
import com.volt.voltdata.appdata.AppHandler
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.Cache


class LogFragment : Fragment() {


    private var _binding: FragmentLogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding
        @RequiresApi(M)
        get() = _binding!!
    private val apiHandler = ApiHandler()

    @RequiresApi(M)
    override fun onStart() {
        super.onStart()
//        AppHandler.pageUpdate(requireActivity())
//        if (AppHandler.connection) {
//            CacheHandler.refreshCacheData(requireActivity())
//        }
    }

    @RequiresApi(M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {


        _binding = FragmentLogBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.dataPushButton.isEnabled = AppHandler.connection
        if (AppHandler.connection || AppHandler.offlineSignIns.isNotEmpty()) {
            binding.dataPushButton.text = "PUSH TABLE"
        } else {
            binding.dataPushButton.text = "OFFLINE..."
        }

        binding.dataPushButton.isEnabled = AppHandler.connection

        binding.dataPushButton.setOnClickListener {
            val entries =
                CacheHandler.getOfflineSignInCacheList(requireActivity()).size + CacheHandler.getOfflineSignOutCacheList(
                    requireActivity()).size
            //Sign In Requests
            for (list in CacheHandler.getOfflineSignInCacheList(requireActivity())) {
                apiHandler.postActiveTimeSheetWithTime(list)
            }
            AppHandler.offlineSignIns.clear()
            binding.offlineTableSignIn.removeAllViews()
            Thread.sleep(1000)
            //Sign Out Requests
            for (list in CacheHandler.getOfflineSignOutCacheList(requireActivity())) {
                apiHandler.postFinalTimeSheetWithTime(list)
            }
            AppHandler.offlineSignOuts.clear()
            binding.offlineTableSignOut.removeAllViews()
            //
            CacheHandler.deleteOfflineLogs(requireActivity())
            Toast.makeText(requireActivity(), "Sent $entries to the Database!", Toast.LENGTH_LONG)
                .show()
            refreshPage()

        }



        var presses = 0
        binding.clearLogsButton.setOnClickListener() {
            presses++
            binding.clearLogsButton.setBackgroundColor(Color.RED)
            binding.clearLogsButton.setTextColor(Color.BLACK)
            if (presses > 1) {
                AppHandler.offlineSignIns.clear()
                binding.offlineTableSignIn.removeAllViews()
                CacheHandler.deleteOfflineLogs(requireActivity())
                Toast.makeText(requireActivity(), "Logs Cleared!", Toast.LENGTH_LONG).show()
                refreshPage()
            }
        }

//        binding.scrollView.setOnScrollChangeListener { view, i, i2, i3, i4 ->
//            Log.i("TK", "$view is Moving $i $i2 $i3 $i4")
//            if (i4 > i2 + 20) {
//                refreshPage()
//            }
//        }
//


        return root
    }


    @RequiresApi(M)
    private fun refreshPage() {
        val fragmentManager = this.parentFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(
            R.id.nav_host_fragment_activity_main,
            LogFragment().newInstance()
        )
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun newInstance(): LogFragment {
        return LogFragment()
    }


    @RequiresApi(M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Thread.sleep(250)


        if(AppHandler.admin) {
            if (CacheHandler.getOfflineSignInCacheList(requireActivity()).isNotEmpty()) {
                createTable(requireView().findViewById(R.id.allTable) as TableLayout,
                    CacheHandler.getActiveSheetCacheList(requireActivity()))
                createSignInTable(requireView().findViewById(R.id.offlineTableSignIn) as TableLayout,
                    CacheHandler.getOfflineSignInCacheList(requireActivity()))
            } else {
                Log.i("TK Active Empty?",
                    CacheHandler.getActiveSheetCacheList(requireActivity()).isNotEmpty().toString())
            }
            if (CacheHandler.getOfflineSignOutCacheList(requireActivity()).isNotEmpty()) {
                createSignOutTable(requireView().findViewById(R.id.offlineTableSignOut) as TableLayout,
                    CacheHandler.getOfflineSignOutCacheList(requireActivity()))
            } else {
                Log.i("TK Final Empty?",
                    CacheHandler.getActiveSheetCacheList(requireActivity()).isNotEmpty().toString())
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun createTable(
        tableLayout: TableLayout,
        time_sheet: List<ActiveTimeSheetData>,
    ) {
        for ((count, sheet) in time_sheet.withIndex()) {
            Log.i("TK Table", "Creating a table row: $sheet")
            val row = TableRow(activity)
            val tl: TableRow.LayoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                .25f
            )
            row.layoutParams = tl
            val tr: TableRow.LayoutParams = TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                .25f
            )
            val name = TextView(activity)
            val time = TextView(activity)
            val location = TextView(activity)
            val task = TextView(activity)
            val fullName = sheet.first_name + " " + sheet.last_name
            name.text = fullName
            time.text = sheet.time_in
            location.text = sheet.location_code
            task.text = sheet.task_code
            name.textAlignment = View.TEXT_ALIGNMENT_CENTER
            time.textAlignment = View.TEXT_ALIGNMENT_CENTER
            location.textAlignment = View.TEXT_ALIGNMENT_CENTER
            task.textAlignment = View.TEXT_ALIGNMENT_CENTER
            name.setTextAppearance(com.volt.R.style.tableRowTextView)
            time.setTextAppearance(com.volt.R.style.tableRowTextView)
            location.setTextAppearance(com.volt.R.style.tableRowTextView)
            task.setTextAppearance(com.volt.R.style.tableRowTextView)
            name.layoutParams = tr
            time.layoutParams = tr
            location.layoutParams = tr
            task.layoutParams = tr
            row.addView(name)
            row.addView(time)
            row.addView(location)
            row.addView(task)
            tableLayout.addView(row, count + 1)
        }
    }


    @RequiresApi(M)
    fun createSignInTable(
        tableLayout: TableLayout,
        time_sheet: List<ActiveTimeSheetData>,
    ) {
        for ((count, sheet) in time_sheet.withIndex()) {
            Log.i("TK Table", "Creating a table row: $sheet")
            val row = TableRow(activity)
            val tl: TableRow.LayoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                .25f
            )
            row.layoutParams = tl
            val tr: TableRow.LayoutParams = TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                .25f
            )
            val name = TextView(activity)
            val time = TextView(activity)
            val location = TextView(activity)
            val task = TextView(activity)
            val fullName = sheet.first_name + " " + sheet.last_name
            name.text = fullName
            time.text = sheet.time_in
            location.text = sheet.location_code
            task.text = sheet.task_code
            name.textAlignment = View.TEXT_ALIGNMENT_CENTER
            time.textAlignment = View.TEXT_ALIGNMENT_CENTER
            location.textAlignment = View.TEXT_ALIGNMENT_CENTER
            task.textAlignment = View.TEXT_ALIGNMENT_CENTER
            name.setTextAppearance(com.volt.R.style.tableRowTextView)
            time.setTextAppearance(com.volt.R.style.tableRowTextView)
            location.setTextAppearance(com.volt.R.style.tableRowTextView)
            task.setTextAppearance(com.volt.R.style.tableRowTextView)
            name.layoutParams = tr
            time.layoutParams = tr
            location.layoutParams = tr
            task.layoutParams = tr
            row.addView(name)
            row.addView(time)
            row.addView(location)
            row.addView(task)
            tableLayout.addView(row, count + 1)
        }
    }

    @RequiresApi(M)
    fun createSignOutTable(
        tableLayout: TableLayout,
        time_sheet: List<FinalTimeSheetData>,
    ) {
        for ((count, sheet) in time_sheet.withIndex()) {
            Log.i("TK Table", "Creating a table row: $sheet")
            val row = TableRow(activity)
            val tl: TableRow.LayoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                .25f
            )
            row.layoutParams = tl
            val tr: TableRow.LayoutParams = TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                .25f
            )
            val name = TextView(activity)
            val time = TextView(activity)
            var fullName = ""
            for (entry in CacheHandler.getEmployeeCacheList(requireActivity())){
                if (sheet.emp_id ==  entry.emp_id) {
                    fullName = entry.first_name + " " + entry.last_name
                }
            }


            name.text = fullName
            time.text = sheet.hours.toString()

            name.textAlignment = View.TEXT_ALIGNMENT_CENTER
            time.textAlignment = View.TEXT_ALIGNMENT_CENTER


            name.setTextAppearance(com.volt.R.style.tableRowTextView)
            time.setTextAppearance(com.volt.R.style.tableRowTextView)

            name.layoutParams = tr
            time.layoutParams = tr

            row.addView(name)
            row.addView(time)

            tableLayout.addView(row, count + 1)
        }
    }


    @RequiresApi(M)
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}