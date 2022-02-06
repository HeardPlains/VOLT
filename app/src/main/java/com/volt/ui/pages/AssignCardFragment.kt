package com.volt.ui.pages


import android.os.Build.VERSION_CODES.M
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.volt.voltdata.apidata.ApiHandler
import com.volt.databinding.FragmentAssignCardBinding
import com.volt.voltdata.appdata.AppHandler


class AssignCardFragment : Fragment() {


    private var _binding: FragmentAssignCardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding
        @RequiresApi(M)
        get() = _binding!!
    private val apiHandler = ApiHandler()

//    @RequiresApi(M)
//    override fun onStart() {
//        AppHandler.pageUpdate(requireActivity())
//        super.onStart()
//        if (AppHandler.connection){
//            CacheHandler.refreshCacheData(requireActivity())
//        }
//    }

    @RequiresApi(M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAssignCardBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        AppHandler.currentPage = Pages.ASSIGN_CARD
//        if (AppHandler.admin) {
//            AppHandler.renderEmployeesInSpinner(CacheHandler.getEmployeeCacheList(requireActivity()), binding.empSpinner, requireActivity())
//            AppHandler.renderTasksInSpinner(CacheHandler.getTaskCacheList(requireActivity()), binding.taskSpinner, requireActivity())
//        } else {
//            Toast.makeText(
//                requireActivity(),
//                "Please Enter Foreman ID in the Settings to View Data",
//                Toast.LENGTH_LONG
//            ).show()
//        }


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
                AppHandler.currentCardAssign.empId = selectedItem
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.taskSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem =
                    parent.getItemAtPosition(position).toString()
                Log.i("TK Spinner", selectedItem)
                AppHandler.currentCardAssign.task = selectedItem
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }






        return root
    }

    fun newInstance(): AssignCardFragment {
        return AssignCardFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    @RequiresApi(M)
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}