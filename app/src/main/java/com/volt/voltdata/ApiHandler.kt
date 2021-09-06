package com.volt.voltdata

import android.R
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.volt.BASE_URL
import com.volt.voltdata.apidata.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiHandler {
    private val api: VOLTApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(VOLTApi::class.java)


    fun renderEmployeesInSpinner(
        activity: FragmentActivity,
        spinner: Spinner
    ) {
        val employeeCall: Call<List<EmployeeData>> = api.getEmployees()
        val employeeValues = arrayListOf<String>()
        employeeCall.enqueue(object : Callback<List<EmployeeData>> {
            override fun onResponse(
                call: Call<List<EmployeeData>>,
                response: Response<List<EmployeeData>>
            ) {
                val rs: List<EmployeeData> = response.body()!!
                for (sheet in rs) {
                    employeeValues.add(sheet.first_name + " " + sheet.last_name)
                }
                val adapter =
                    ArrayAdapter(
                        activity,
                        R.layout.simple_spinner_item,
                        employeeValues
                    )
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<List<EmployeeData>>, t: Throwable) {
                Log.i("TK Error", "${t.message}")
            }

        })
    }

    fun renderTasksInSpinner(
        activity: FragmentActivity,
        spinner: Spinner
    ) {
        val taskCall: Call<List<TaskData>> = api.getTasks()
        val taskValues = arrayListOf<String>()
        taskCall.enqueue(object : Callback<List<TaskData>> {
            override fun onResponse(
                call: Call<List<TaskData>>,
                response: Response<List<TaskData>>
            ) {
                val rs: List<TaskData> = response.body()!!
                for (sheet in rs) {
                    taskValues.add(sheet.display_name)
                }
                val adapter =
                    ArrayAdapter(
                        activity,
                        R.layout.simple_spinner_item,
                        taskValues
                    )
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<List<TaskData>>, t: Throwable) {
                Log.i("TK Error", "${t.message}")
            }

        })
    }

    fun postActiveTimeSheet(time_sheet: ActiveTimeSheetData) {
        val call: Call<ActiveTimeSheetData> = api.postTimeSheet(time_sheet)
        call.enqueue(object : Callback<ActiveTimeSheetData> {
            override fun onResponse(
                call: Call<ActiveTimeSheetData>,
                response: Response<ActiveTimeSheetData>
            ) {


            }

            override fun onFailure(call: Call<ActiveTimeSheetData>, t: Throwable) {
                Log.i("TK Error", "${t.message}")
            }
        })
    }

    fun postFinalTimeSheet(time_sheet: FinalTimeSheetJson) {

        val call: Call<FinalTimeSheetJson> = api.postFinalSheet(time_sheet)
        call.enqueue(object : Callback<FinalTimeSheetJson> {
            override fun onResponse(
                call: Call<FinalTimeSheetJson>,
                response: Response<FinalTimeSheetJson>
            ) {
            }

            override fun onFailure(call: Call<FinalTimeSheetJson>, t: Throwable) {
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun createTable(
        activity: FragmentActivity,
        tableLayout: TableLayout,
        time_sheet: ActiveTimeSheetData
    ) {
        for (i in 1..1) {
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
            val fullName = time_sheet.first_name + " " + time_sheet.last_name
            name.text = fullName
            time.text = time_sheet.time_in
            location.text = time_sheet.location_code
            task.text = time_sheet.task_code
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
            tableLayout.addView(row, i)
        }
    }

    fun renderEmployeesInTable(
        activity: FragmentActivity,
        tableLayout: TableLayout
    ) {
        val activeCall: Call<List<ActiveTimeSheetData>> = api.getTimeSheet()
        activeCall.enqueue(object : Callback<List<ActiveTimeSheetData>> {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onResponse(
                call: Call<List<ActiveTimeSheetData>>,
                response: Response<List<ActiveTimeSheetData>>
            ) {
                val rs: List<ActiveTimeSheetData> = response.body()!!
                for (sheet in rs) {
                    createTable(activity, tableLayout, sheet)
                }
            }

            override fun onFailure(call: Call<List<ActiveTimeSheetData>>, t: Throwable) {
                Log.i("TK Error", "${t.message}")
            }

        })
    }

    fun getForemanData(
        func: (foremanData: List<ForemanData>) -> Unit
    ) {
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VOLTApi::class.java)
        val employeeCall: Call<List<ForemanData>> = api.getForeman()
        employeeCall.enqueue(object : Callback<List<ForemanData>> {
            override fun onResponse(
                call: Call<List<ForemanData>>,
                response: Response<List<ForemanData>>
            ) {
                val rs: List<ForemanData> = response.body()!!
                for (sheet in rs) {
                    Log.i("TK Foreman", sheet.toString())
                }
                func(rs)
            }

            override fun onFailure(call: Call<List<ForemanData>>, t: Throwable) {
                Log.i("TK Error", "${t.message}")
            }
        })
    }
}