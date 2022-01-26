package com.volt.voltdata.apidata

import kotlinx.serialization.Serializable

@Serializable
data class FinalTimeSheetData(
    val emp_id: Int,
    val hours: Double
)

