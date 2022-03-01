package com.volt.voltdata.apidata

import android.util.Log
import com.volt.BASE_URL
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




    fun postFinalTimeSheet(time_sheet: FinalTimeSheetData) {

        val call: Call<FinalTimeSheetData> = api.postFinalSheet(time_sheet)
        call.enqueue(object : Callback<FinalTimeSheetData> {
            override fun onResponse(
                call: Call<FinalTimeSheetData>,
                response: Response<FinalTimeSheetData>
            ) {
            }

            override fun onFailure(call: Call<FinalTimeSheetData>, t: Throwable) {
            }

        })
    }

    fun postFinalTimeSheetWithTime(time_sheet: FinalTimeSheetData) {

        val call: Call<FinalTimeSheetData> = api.postFinalSheetWithTime(time_sheet)
        call.enqueue(object : Callback<FinalTimeSheetData> {
            override fun onResponse(
                call: Call<FinalTimeSheetData>,
                response: Response<FinalTimeSheetData>
            ) {
            }

            override fun onFailure(call: Call<FinalTimeSheetData>, t: Throwable) {
            }

        })
    }

    fun postExtraFinalSheet(time_sheet: ExtraFinalTimeSheetData) {

        val call: Call<ExtraFinalTimeSheetData> = api.postExtraFinalSheet(time_sheet)
        call.enqueue(object : Callback<ExtraFinalTimeSheetData> {
            override fun onResponse(
                call: Call<ExtraFinalTimeSheetData>,
                response: Response<ExtraFinalTimeSheetData>
            ) {
            }

            override fun onFailure(call: Call<ExtraFinalTimeSheetData>, t: Throwable) {
            }

        })
    }

    fun postActiveTimeSheetWithTime(time_sheet: ActiveTimeSheetData) {

        val call: Call<ActiveTimeSheetData> = api.postTimeSheetWithTime(time_sheet)
        call.enqueue(object : Callback<ActiveTimeSheetData> {
            override fun onResponse(
                call: Call<ActiveTimeSheetData>,
                response: Response<ActiveTimeSheetData>
            ) {
            }

            override fun onFailure(call: Call<ActiveTimeSheetData>, t: Throwable) {
            }

        })
    }

    fun postManualActiveTimeSheet(time_sheet: ActiveTimeSheetData) {

        val call: Call<ActiveTimeSheetData> = api.postTimeSheetWithTime(time_sheet)
        call.enqueue(object : Callback<ActiveTimeSheetData> {
            override fun onResponse(
                call: Call<ActiveTimeSheetData>,
                response: Response<ActiveTimeSheetData>
            ) {
            }

            override fun onFailure(call: Call<ActiveTimeSheetData>, t: Throwable) {
            }

        })
    }


    fun updateEmployee(employeeData: EmployeeData) {

        val call: Call<EmployeeData> = api.updateEmployee(employeeData)
        call.enqueue(object : Callback<EmployeeData> {
            override fun onResponse(
                call: Call<EmployeeData>,
                response: Response<EmployeeData>
            ) {
            }

            override fun onFailure(call: Call<EmployeeData>, t: Throwable) {
            }

        })
    }



//    fun getForemanData(
//        func: (foremanData: List<ForemanData>) -> Unit
//    )  {
//        val employeeCall: Call<List<ForemanData>> = api.getForeman()
//        employeeCall.enqueue(object : Callback<List<ForemanData>> {
//            override fun onResponse(
//                call: Call<List<ForemanData>>,
//                response: Response<List<ForemanData>>
//            ) {
//                val rs: List<ForemanData> = response.body()!!
//                func(rs)
//            }
//
//            override fun onFailure(call: Call<List<ForemanData>>, t: Throwable) {
//                Log.i("TK Error", "${t.message}")
//            }
//        })
//    }

    fun getEmployeeData(
        func: (foremanData: List<EmployeeData>) -> Unit
    ) {
        val employeeCall: Call<List<EmployeeData>> = api.getEmployees()
        employeeCall.enqueue(object : Callback<List<EmployeeData>> {
            override fun onResponse(
                call: Call<List<EmployeeData>>,
                response: Response<List<EmployeeData>>
            ) {
                val rs: List<EmployeeData> = response.body()!!

                func(rs)
            }

            override fun onFailure(call: Call<List<EmployeeData>>, t: Throwable) {
                Log.i("TK Error", "${t.message}")
            }
        })
    }

    fun getActiveTimeSheetData(
        func: (foremanData: List<ActiveTimeSheetData>) -> Unit
    ) {
        val employeeCall: Call<List<ActiveTimeSheetData>> = api.getActiveTimeSheet()
        employeeCall.enqueue(object : Callback<List<ActiveTimeSheetData>> {
            override fun onResponse(
                call: Call<List<ActiveTimeSheetData>>,
                response: Response<List<ActiveTimeSheetData>>
            ) {
                val rs: List<ActiveTimeSheetData> = response.body()!!

                func(rs)
            }

            override fun onFailure(call: Call<List<ActiveTimeSheetData>>, t: Throwable) {
                Log.i("TK Error", "${t.message}")
            }
        })
    }

    fun getLocationData(
        func: (foremanData: List<LocationData>) -> Unit
    ) {
        val employeeCall: Call<List<LocationData>> = api.getLocations()
        employeeCall.enqueue(object : Callback<List<LocationData>> {
            override fun onResponse(
                call: Call<List<LocationData>>,
                response: Response<List<LocationData>>
            ) {
                val rs: List<LocationData> = response.body()!!

                func(rs)
            }

            override fun onFailure(call: Call<List<LocationData>>, t: Throwable) {
                Log.i("TK Error", "${t.message}")
            }
        })
    }
    fun getTaskData(
        func: (foremanData: List<TaskData>) -> Unit
    ) {
        val employeeCall: Call<List<TaskData>> = api.getTasks()
        employeeCall.enqueue(object : Callback<List<TaskData>> {
            override fun onResponse(
                call: Call<List<TaskData>>,
                response: Response<List<TaskData>>
            ) {
                val rs: List<TaskData> = response.body()!!

                func(rs)
            }

            override fun onFailure(call: Call<List<TaskData>>, t: Throwable) {
                Log.i("TK Error", "${t.message}")
            }
        })
    }

    fun getFinalTimeSheetData(
        func: (foremanData: List<FinalTimeSheetData>) -> Unit
    ) {
        val employeeCall: Call<List<FinalTimeSheetData>> = api.getFinalTimeSheet()
        employeeCall.enqueue(object : Callback<List<FinalTimeSheetData>> {
            override fun onResponse(
                call: Call<List<FinalTimeSheetData>>,
                response: Response<List<FinalTimeSheetData>>
            ) {
                val rs: List<FinalTimeSheetData> = response.body()!!

                func(rs)
            }

            override fun onFailure(call: Call<List<FinalTimeSheetData>>, t: Throwable) {
                Log.i("TK Error", "${t.message}")
            }
        })
    }
}