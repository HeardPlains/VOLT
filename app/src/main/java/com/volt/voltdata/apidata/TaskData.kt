package com.volt.voltdata.apidata

import kotlinx.serialization.Serializable

@Serializable
data class TaskData(
    val display_name: String,
    val id: Int
)