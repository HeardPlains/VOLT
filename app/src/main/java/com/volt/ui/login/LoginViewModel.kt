package com.volt.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.volt.voltdata.data.LoginRepository
import com.volt.voltdata.data.Result

import com.volt.R

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job

        when (val result = loginRepository.login(username, password)) {
            is Result.Success -> {
                _loginResult.value =
                    LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
            }
            is Result.ConnectionError -> {
                _loginResult.value = LoginResult(connection = R.string.connection_error)
            }
            else -> {
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }
    }
    fun login(foremanID: String) {
        // can be launched in a separate asynchronous job


        when (val result = loginRepository.login(foremanID)) {
            is Result.Success -> {
                _loginResult.value =
                    LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
            }
            is Result.ConnectionError -> {
                _loginResult.value = LoginResult(error = R.string.no_connection)
            }
            else -> {
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }

    }

//    fun loginDataChanged(username: String, password: String) {
//        if (!isUserNameValid(username)) {
//            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
//        } else if (!isPasswordValid(password)) {
//            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
//        } else {
//            _loginForm.value = LoginFormState(isDataValid = true)
//        }
//    }

    fun loginDataChanged(foremanID: String) {
        if (!isUserNameValid(foremanID)) {
            _loginForm.value = LoginFormState(foremanIDError = R.string.invalid_username)
        }  else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}