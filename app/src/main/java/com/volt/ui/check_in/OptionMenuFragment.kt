package com.volt.ui.check_in

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.volt.R
import com.volt.databinding.FragmentOptionMenuBinding
import com.volt.ui.check_in.pages.AssignCardFragment
import com.volt.ui.check_in.pages.LogFragment
import com.volt.voltdata.CacheHandler
import com.volt.voltdata.appdata.AppHandler


class OptionMenuFragment : Fragment() {


    private var _binding: FragmentOptionMenuBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()
        AppHandler.pageUpdate(requireActivity())
        if (AppHandler.connection){
            CacheHandler.refreshCacheData(requireActivity())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentOptionMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)





        binding.empIdCheckInButton.setOnClickListener {
            val fragmentManager = parentFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.nav_host_fragment_activity_main,
                AssignCardFragment().newInstance()
            )
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        binding.logConstraint.setOnClickListener {
            val fragmentManager = parentFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.nav_host_fragment_activity_main,
                LogFragment()
            )
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }


//        binding.findEmployeeButton.setOnClickListener {
//            val fragmentManager = parentFragmentManager
//            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
//            fragmentTransaction.replace(
//                R.id.nav_host_fragment_activity_main,
//                FindEmployeeFragment().newInstance()
//            )
//            fragmentTransaction.addToBackStack(null)
//            fragmentTransaction.commit()
//        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}