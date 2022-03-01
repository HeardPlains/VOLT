package com.volt.voltdata.apidata

import kotlinx.serialization.Serializable

@Serializable
data class ExtraFinalTimeSheetData(
    val emp_id: Int,
    val location_code: String,
    val date: String,
    val time_out: String,
    val hours: Double,
    val notes: String
)

