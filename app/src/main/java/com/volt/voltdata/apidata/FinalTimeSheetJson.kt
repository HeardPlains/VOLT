package com.volt.voltdata.apidata

import com.google.gson.annotations.SerializedName

data class FinalTimeSheetJson(
    @SerializedName("first_name")
    val first_name: String,
    @SerializedName("last_name")
    val last_name: String,
)

