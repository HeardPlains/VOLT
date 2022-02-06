package com.volt.ui.pages


import android.os.Build
import android.os.Build.VERSION_CODES.M
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.volt.R
import com.volt.databinding.FragmentTransitionFragementBinding
import com.volt.voltdata.apidata.ApiHandler
import com.volt.voltdata.CacheHandler
import com.volt.voltdata.appdata.AppHandler
import kotlinx.serialization.ExperimentalSerializationApi


class TransitionFragment : Fragment() {


    private var _binding: FragmentTransitionFragementBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val apiHandler = ApiHandler()

    @RequiresApi(M)
    override fun onStart() {
        super.onStart()
        updatePage()
    }

    private fun clearEmpList(linearLayout: LinearLayout){
        if (linearLayout.size > 1) linearLayout.removeAllViews()
    }



    @RequiresApi(M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {


        _binding = FragmentTransitionFragementBinding.inflate(inflater, container, false)
        val root: View = binding.root




        return root
    }


    fun newInstance(): TransitionFragment {
        return TransitionFragment()
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      //  updatePage()
        Thread.sleep(1000)
        val passedArguments = arguments
        val passedFrag = passedArguments!!.getString("fragment")
        Log.i("TK EMP_ID", passedFrag.toString())
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val fragment = when (passedFrag) {
            "Authentication" -> AuthenticationFragment().newInstance()
            else -> {
                EmployeePreviewFragment().newInstance()
            }
        }
        fragmentTransaction.replace(
            R.id.nav_host_fragment_activity_main,
            fragment
        )
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()


    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @ExperimentalSerializationApi
    @RequiresApi(M)
    fun updatePage() {
        AppHandler.pageUpdate(requireActivity())
        if (AppHandler.connection) {
            CacheHandler.refreshCacheData(requireActivity())
        }
    }
}

