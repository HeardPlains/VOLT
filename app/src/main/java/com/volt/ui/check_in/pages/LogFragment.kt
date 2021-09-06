package com.volt.ui.check_in.pages


import android.content.Context
import android.net.ConnectivityManager
import android.os.Build.VERSION_CODES.M
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
import com.volt.R
import com.volt.databinding.FragmentLogBinding
import com.volt.voltdata.ApiHandler


class LogFragment : Fragment() {


    private var _binding: FragmentLogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding
        @RequiresApi(M)
        get() = _binding!!
    private val apiHandler = ApiHandler()

    val taskValues = arrayListOf<String>()
    val employeeValues = arrayListOf<String>()


    @RequiresApi(M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val cm =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val connection = (cm.activeNetwork?.toString() != null)
        Log.i("TK", cm.activeNetwork.toString())



        _binding = FragmentLogBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.offlineLogConstraint.isVisible = connection
        binding.button2.isEnabled = connection


//        binding.taskSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                val selectedItem =
//                    parent.getItemAtPosition(position).toString()
//                Log.i("TK Spinner", selectedItem)
//                (activity as MainActivity?)?.setTask(selectedItem)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }


        return root
    }


    fun newInstance(): LogFragment {
        return LogFragment()
    }

    @RequiresApi(M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apiHandler.renderEmployeesInTable(
            requireActivity(),
            requireView().findViewById(R.id.allTable) as TableLayout
        )
//        val ll = requireView().findViewById(R.id.allTable) as TableLayout
//
//        for (i in 1..1) {
//            val row = TableRow(requireActivity())
//            val tl: TableRow.LayoutParams = TableRow.LayoutParams(
//                TableRow.LayoutParams.MATCH_PARENT,
//                TableRow.LayoutParams.WRAP_CONTENT,
//                .25f
//            )
//            row.layoutParams = tl
//            val tr: TableRow.LayoutParams = TableRow.LayoutParams(
//                0,
//                TableRow.LayoutParams.WRAP_CONTENT,
//                .25f
//            )
//            val name = TextView(requireActivity())
//            val time = TextView(requireActivity())
//            val location = TextView(requireActivity())
//            val task = TextView(requireActivity())
//            name.text = "Ty Ordway"
//            time.text = "10:10:00"
//            location.text = "17C"
//            task.text = "RU"
//            name.textAlignment = TEXT_ALIGNMENT_CENTER
//            time.textAlignment = TEXT_ALIGNMENT_CENTER
//            location.textAlignment = TEXT_ALIGNMENT_CENTER
//            task.textAlignment = TEXT_ALIGNMENT_CENTER
//            name.setTextAppearance(R.style.tableRowTextView)
//            time.setTextAppearance(R.style.tableRowTextView)
//            location.setTextAppearance(R.style.tableRowTextView)
//            task.setTextAppearance(R.style.tableRowTextView)
//            name.layoutParams = tr
//            time.layoutParams = tr
//            location.layoutParams = tr
//            task.layoutParams = tr
//            row.addView(name)
//            row.addView(time)
//            row.addView(location)
//            row.addView(task)
//            ll.addView(row, i)
//        }
    }


    @RequiresApi(M)
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}