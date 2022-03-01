package com.volt.voltdata.apidata

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeData(
    val id: Int,
    val emp_id: Int,
    val first_name: String,
    val last_name: String,
    val previous_locations: String,
    val previous_tasks: String,
    val current_task: String,
    val current_location: String,
    val status: Int,
    val foreman: Int,
    val previous_time: String
)