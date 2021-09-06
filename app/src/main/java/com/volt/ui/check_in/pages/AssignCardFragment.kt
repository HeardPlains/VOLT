package com.volt.ui.check_in.pages


import android.content.Context
import android.net.ConnectivityManager
import android.os.Build.VERSION_CODES.M
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.volt.voltdata.ApiHandler
import com.volt.MainActivity
import com.volt.databinding.FragmentAssignCardBinding
import com.volt.voltdata.appdata.AppHandler
import com.volt.voltdata.appdata.Pages


class AssignCardFragment : Fragment() {


    private var _binding: FragmentAssignCardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding
        @RequiresApi(M)
        get() = _binding!!
    private val apiHandler = ApiHandler()


    @RequiresApi(M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        var cm =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        Log.i("TK", cm.activeNetwork.toString())



        _binding = FragmentAssignCardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        AppHandler.currentPage = Pages.ASSIGN_CARD
        if ((activity as MainActivity?)?.getAdmin() == true) {
            apiHandler.renderTasksInSpinner(requireActivity(), binding.taskSpinner)
            apiHandler.renderEmployeesInSpinner(requireActivity(), binding.empSpinner)
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
                (activity as MainActivity?)?.setEmpId(selectedItem)
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
                (activity as MainActivity?)?.setTask(selectedItem)
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