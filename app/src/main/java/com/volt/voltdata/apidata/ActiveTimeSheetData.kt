package com.volt.voltdata.apidata

import com.google.gson.annotations.SerializedName

data class ActiveTimeSheetData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("emp_id")
    val emp_id: Int,
    @SerializedName("first_name")
    val first_name: String,
    @SerializedName("last_name")
    val last_name: String,
    @SerializedName("time_in")
    val time_in: String,
    @SerializedName("location_code")
    val location_code: String,
    @SerializedName("task_code")
    val task_code: String,
    @SerializedName("foreman_id")
    val foreman_id: Int
)

