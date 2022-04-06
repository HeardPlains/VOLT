package com.volt.voltdata.data

import android.util.Log
import com.volt.MainActivity
import com.volt.voltdata.CacheHandler
import com.volt.voltdata.apidata.ApiHandler
import com.volt.voltdata.apidata.EmployeeData
import com.volt.voltdata.appdata.AppHandler
import com.volt.voltdata.appdata.CurrentForeman
import com.volt.voltdata.data.model.LoggedInUser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(foremanID: String): Result<LoggedInUser> {

        try {
            for (emp in AppHandler.employeeData) {
                Log.i("TK ID Checking", "${emp.emp_id} ,${foremanID}")
                if (emp.emp_id == foremanID.toInt()) {

                    if (emp.foreman == 1) {
                        AppHandler.currentForeman = CurrentForeman(
                            emp.first_name,
                            emp.last_name,
                            emp.emp_id,
                            emp.current_location
                        )
                        AppHandler.admin = true
                        return Result.Success(LoggedInUser(emp.emp_id.toString(),
                            "${emp.first_name} ${emp.last_name}"))
                    } else {
                        AppHandler.admin = false
                    }
                }
            }
            return Result.Error(IOException("Error logging in"))
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }


    fun login(username: String, password: String): Result<LoggedInUser> {

        try {
            // TODO: handle loggedInUser authentication
            val fakeUser = LoggedInUser(UUID.randomUUID().toString(), "Jane Doe")
            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {

    }
}