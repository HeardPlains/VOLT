package com.volt.voltdata

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.volt.voltdata.apidata.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.text.Charsets.UTF_8

class CacheHandler() {
    companion object {


        private val apiHandler = ApiHandler()

        fun createCacheFile(activity: FragmentActivity, name: String) {
            val outputFile = File(activity.cacheDir, "$name.txt")
        }


        @ExperimentalSerializationApi
        @RequiresApi(Build.VERSION_CODES.M)
        fun refreshCacheData(activity: FragmentActivity) {

            deleteAll(activity)
            apiHandler.getForemanData {
                createCacheFile(activity, "foreman", it)
            }
            apiHandler.getEmployeeData {
                createCacheFile(activity, "employees", it)
            }
            apiHandler.getActiveTimeSheetData {
                createCacheFile(activity, "active", it)
            }
            apiHandler.getFinalTimeSheetData {
                createCacheFile(activity, "final", it)
            }
            apiHandler.getLocationData {
                createCacheFile(activity, "locations", it)
            }
            apiHandler.getTaskData {
                createCacheFile(activity, "tasks", it)
                printAllCache(activity)
            }

        }

        fun refreshCacheData(activity: FragmentActivity, file: String) {

        }


        @ExperimentalSerializationApi
        fun createCacheFile(activity: FragmentActivity, name: String, list: List<*>) {
            val outputFile = File(activity.cacheDir, "$name.txt")
            var inputStream = ByteArrayInputStream("".toByteArray())
            when (list[0]!!::class) {
                ForemanData::class -> {
                    inputStream =
                        ByteArrayInputStream(Json.encodeToString(list as List<ForemanData>)
                            .toByteArray(UTF_8))
                }
                EmployeeData::class -> {
                    inputStream =
                        ByteArrayInputStream(Json.encodeToString(list as List<EmployeeData>)
                            .toByteArray(UTF_8))
                }
                ActiveTimeSheetData::class -> {
                    inputStream =
                        ByteArrayInputStream(Json.encodeToString(list as List<ActiveTimeSheetData>)
                            .toByteArray(UTF_8))
                }
                FinalTimeSheetData::class -> {
                    inputStream =
                        ByteArrayInputStream(Json.encodeToString(list as List<FinalTimeSheetData>)
                            .toByteArray(UTF_8))
                }
                LocationData::class -> {
                    inputStream =
                        ByteArrayInputStream(Json.encodeToString(list as List<LocationData>)
                            .toByteArray(UTF_8))
                }
                TaskData::class -> {
                    inputStream = ByteArrayInputStream(Json.encodeToString(list as List<TaskData>)
                        .toByteArray(UTF_8))
                }
            }
            val outputStream = FileOutputStream(outputFile)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
        }


        fun createCacheFile(activity: FragmentActivity, name: String, text: String) {
            val outputFile = File(activity.cacheDir, "$name.txt")
            val inputStream = ByteArrayInputStream(text.toByteArray(UTF_8))
            val outputStream = FileOutputStream(outputFile)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
        }

        @ExperimentalSerializationApi
        fun printForemanCache(activity: FragmentActivity) {
            Log.i("TK Testing Cache","Calling Correctly ${activity.cacheDir.toString()}")
            for (files in activity.cacheDir.listFiles()!!) {
                Log.i("TK Testing Cache", files.name)
                Log.i("TK Testing Cache", "foreman.txt")
                if (files.name == "foreman.txt") {
                    Log.i("TK Testing", File(files.toString()).readText(UTF_8))
                }
            }
        }

        @ExperimentalSerializationApi
        fun printAllCache(activity: FragmentActivity) {
            Log.i("TK Cache Printing...", "Attempting to Print")
            for (files in activity.cacheDir.listFiles()!!) {
                Log.i("TK Cache", files.toString())
                Log.i("TK File Output", File(files.toString()).readText(UTF_8))
//                val obj =
//                    Json.decodeFromString<List<ForemanData>>(File(files.toString()).readText(UTF_8))
//                for (data in obj) {
//                    Log.i("TK Object Decode", data.toString())
//                }

            }

        }

        fun deleteAll(activity: FragmentActivity) {
            for (files in activity.cacheDir.listFiles()!!) {
                files.delete()
            }
        }


    }

}