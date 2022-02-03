package com.volt.ui.check_in

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.volt.R
import com.volt.databinding.FragmentOptionMenuBinding
import com.volt.ui.check_in.pages.AssignCardFragment
import com.volt.ui.check_in.pages.LogFragment
import com.volt.voltdata.CacheHandler
import com.volt.voltdata.apidata.ActiveTimeSheetData
import com.volt.voltdata.appdata.AppHandler
import kotlinx.serialization.ExperimentalSerializationApi


class OptionMenuFragment : Fragment() {


    private var _binding: FragmentOptionMenuBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()
        AppHandler.pageUpdate(requireActivity())
        if (AppHandler.connection) {
            CacheHandler.refreshCacheData(requireActivity())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {


        _binding = FragmentOptionMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root



        return root
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (AppHandler.admin) {
            createTable(requireView().findViewById(R.id.tableLayout) as TableLayout,
                CacheHandler.getActiveSheetCacheList(requireActivity()))
        }





        binding.logConstraint.setOnClickListener {
            val fragmentManager = this.parentFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.nav_host_fragment_activity_main,
                LogFragment()
            )
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }


//        binding.findEmployeeButton.setOnClickListener {
//            val fragmentManager = requireActivity().supportFragmentManager
//            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
//            fragmentTransaction.replace(
//                R.id.nav_host_fragment_activity_main,
//                FindEmployeeFragment().newInstance()
//            )
//            fragmentTransaction.addToBackStack(null)
//            fragmentTransaction.commit()
//        }
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}