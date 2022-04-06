package com.volt.ui.login

import android.os.Build
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentTransaction
import com.volt.MainActivity
import com.volt.databinding.FragmentLoginBinding

import com.volt.R
import com.volt.ui.pages.AuthenticationFragment
import com.volt.ui.pages.EmployeePreviewFragment
import com.volt.voltdata.CacheHandler
import com.volt.voltdata.appdata.AppHandler
import com.volt.voltdata.appdata.CurrentForeman

class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel
    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onStart() {
        AppHandler.employeeData = CacheHandler.getEmployeeCacheList(requireActivity())
        super.onStart()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        val foremanIDEditText = binding.foremanId
//        val usernameEditText = binding.username
//        val passwordEditText = binding.password
        val loginButton = binding.login
        val loadingProgressBar = binding.loading

        loginViewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                loginButton.isEnabled = loginFormState.isDataValid
                loginFormState.foremanIDError?.let {
                    foremanIDEditText.error = getString(it)
                }
            })

        loginViewModel.loginResult.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                loginResult.error?.let {
                    showLoginFailed(it)
                }
                loginResult.success?.let {
                    updateUiWithUser(it)
                }
            })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                loginViewModel.loginDataChanged(
                    foremanIDEditText.text.toString(),
//                    usernameEditText.text.toString(),
//                    passwordEditText.text.toString()
                )
            }
        }
//        usernameEditText.addTextChangedListener(afterTextChangedListener)
//        passwordEditText.addTextChangedListener(afterTextChangedListener)
        foremanIDEditText.addTextChangedListener(afterTextChangedListener)
        foremanIDEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(
                    foremanIDEditText.text.toString()
//                    usernameEditText.text.toString(),
//                    passwordEditText.text.toString()
                )
            }
            false
        }

        loginButton.setOnClickListener {
            Log.i("TK Error Check", "Press")
            loadingProgressBar.visibility = View.VISIBLE
            loginViewModel.login(
                foremanIDEditText.text.toString()
//                usernameEditText.text.toString(),
//                passwordEditText.text.toString()
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome) + " " + model.displayName
        // TODO : initiate successful logged in experience


        AppHandler.admin = true
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()

        val fragmentManager = this.parentFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val fragment = AuthenticationFragment().newInstance()
        fragmentTransaction.replace(
            R.id.nav_host_fragment_activity_main,
            fragment
        )
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
        (activity as MainActivity?)?.showNavBar()

    }



    @RequiresApi(Build.VERSION_CODES.M)
    private fun showLoginFailed(@StringRes errorString: Int) {

        AppHandler.admin = false
        val fragmentManager = this.parentFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val fragment = LoginFragment()
        fragmentTransaction.replace(
            R.id.nav_host_fragment_activity_main,
            fragment
        )
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}