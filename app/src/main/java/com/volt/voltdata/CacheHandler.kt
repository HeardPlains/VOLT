package com.volt.voltdata

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.volt.voltdata.apidata.*
import com.volt.voltdata.appdata.AppHandler
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import kotlin.text.Charsets.UTF_8

class CacheHandler() {
    companion object {


        private val apiHandler = ApiHandler()


        @ExperimentalSerializationApi
        @RequiresApi(Build.VERSION_CODES.M)
        fun refreshCacheData(activity: FragmentActivity) {
            Log.i("TK Refresh", "Refreshing Api Data")
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

//        fun refreshCacheData(activity: FragmentActivity, file: String) {
//
//        }


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
                    inputStream =
                        ByteArrayInputStream(Json.encodeToString(list as List<TaskData>)
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

        @ExperimentalSerializationApi
        fun addOffLineSignIn(sheet: ActiveTimeSheetData, activity: FragmentActivity) {
            AppHandler.offlineSignIns.add(sheet)
            val outputFile = File(activity.cacheDir, "offlineSignIns.txt")
            val inputStream =
                ByteArrayInputStream(Json.encodeToString(AppHandler.offlineSignIns)
                    .toByteArray(UTF_8))
            val outputStream = FileOutputStream(outputFile)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
        }

        @ExperimentalSerializationApi
        fun addOffLineSignOut(sheet: FinalTimeSheetData, activity: FragmentActivity) {
            AppHandler.offlineSignOuts.add(sheet)
            val outputFile = File(activity.cacheDir, "offlineSignIns.txt")
            val inputStream =
                ByteArrayInputStream(Json.encodeToString(AppHandler.offlineSignOuts)
                    .toByteArray(UTF_8))
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


        fun printCacheFile(file: String, activity: FragmentActivity) {
            for (files in activity.cacheDir.listFiles()!!) {
                if (files.name == file) {
                    Log.i("TK $file Output", File(files.toString()).readText(UTF_8))
                }
            }
        }

        @ExperimentalSerializationApi
        fun printAllCache(activity: FragmentActivity) {
            Log.i("TK Cache Printing...", "Attempting to Print")
            for (files in activity.cacheDir.listFiles()!!) {
                Log.i("TK Cache", files.toString())
                Log.i("TK File Output", File(files.toString()).readText(UTF_8))
            }

        }

        private fun deleteAll(activity: FragmentActivity) {
            for (files in activity.cacheDir.listFiles()!!) {
                if (files.name != "offlineSignIns.txt" && files.name != "offlineSignOuts.txt") {
                    files.delete()
                }
            }
        }
         fun deleteOfflineLogs(activity: FragmentActivity) {
            for (files in activity.cacheDir.listFiles()!!) {
                if (files.name == "offlineSignIns.txt" || files.name == "offlineSignOuts.txt") {
                    files.delete()
                }
            }
        }

        @ExperimentalSerializationApi
        fun getForemanCacheList(activity: FragmentActivity): List<ForemanData> {
            for (files in activity.cacheDir.listFiles()!!) {
                if (files.name == "foreman.txt") {
                    Log.i("TK Foreman Output", File(files.toString()).readText(UTF_8))
                    return Json.decodeFromString(File(files.toString()).readText(
                        UTF_8))
                }
            }
            return emptyList()
        }

        @ExperimentalSerializationApi
        fun getEmployeeCacheList(activity: FragmentActivity): List<EmployeeData> {
            for (files in activity.cacheDir.listFiles()!!) {
                if (files.name == "employees.txt") {
                    Log.i("TK Employee Output", File(files.toString()).readText(UTF_8))
                    return Json.decodeFromString(File(files.toString()).readText(
                        UTF_8))
                }
            }
            return emptyList()
        }


        @ExperimentalSerializationApi
        fun getTaskCacheList(activity: FragmentActivity): List<TaskData> {
            for (files in activity.cacheDir.listFiles()!!) {
                if (files.name == "tasks.txt") {
                    Log.i("TK Tasks Output", File(files.toString()).readText(UTF_8))
                    return Json.decodeFromString(File(files.toString()).readText(
                        UTF_8))
                }
            }
            return emptyList()
        }

        @ExperimentalSerializationApi
        fun getActiveSheetCacheList(activity: FragmentActivity): List<ActiveTimeSheetData> {
            for (files in activity.cacheDir.listFiles()!!) {
                if (files.name == "active.txt") {
                    Log.i("TK Active Output", File(files.toString()).readText(UTF_8))
                    return Json.decodeFromString(File(files.toString()).readText(
                        UTF_8))
                }
            }
            return emptyList()
        }

        @ExperimentalSerializationApi
        fun getFinalSheetCacheList(activity: FragmentActivity): List<FinalTimeSheetData> {
            for (files in activity.cacheDir.listFiles()!!) {
                if (files.name == "final.txt") {
                    Log.i("TK Final Output", File(files.toString()).readText(UTF_8))
                    return Json.decodeFromString(File(files.toString()).readText(
                        UTF_8))
                }
            }
            return emptyList()
        }

        @ExperimentalSerializationApi
        fun getLocationCacheList(activity: FragmentActivity): List<LocationData> {
            for (files in activity.cacheDir.listFiles()!!) {
                if (files.name == "locations.txt") {
                    Log.i("TK Location Output", File(files.toString()).readText(UTF_8))
                    return Json.decodeFromString(File(files.toString()).readText(
                        UTF_8))
                }
            }
            return emptyList()
        }

        @ExperimentalSerializationApi
        fun getOfflineSignInCacheList(activity: FragmentActivity): List<ActiveTimeSheetData> {
            for (files in activity.cacheDir.listFiles()!!) {
                if (files.name == "offlineSignIns.txt") {
                    Log.i("TK Location Output", File(files.toString()).readText(UTF_8))
                    return Json.decodeFromString(File(files.toString()).readText(
                        UTF_8))
                }
            }
            return emptyList()
        }


    }
}