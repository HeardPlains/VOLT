package com.volt.voltdata.apidata

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class FinalTimeSheetData(
    @SerializedName("first_name")
    val first_name: String,
    @SerializedName("last_name")
    val last_name: String,
)

