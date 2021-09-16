package com.volt.voltdata.apidata

import kotlinx.serialization.Serializable

@Serializable
data class FinalTimeSheetData(
    val first_name: String,
    val last_name: String,
    val hours: Double
)

