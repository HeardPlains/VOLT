package com.volt.voltdata.apidata

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ForemanData(
    @SerializedName("first_name")
    val first_name: String,
    @SerializedName("foreman_id")
    val foreman_id: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("last_name")
    val last_name: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("current_location")
    val current_location: String
)