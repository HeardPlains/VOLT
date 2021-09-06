package com.volt.voltdata

import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.volt.voltdata.apidata.ForemanData
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.text.Charsets.UTF_8

class CacheHandler {
    companion object {
        fun createCacheFile(activity: FragmentActivity, name: String) {
            val outputFile = File(activity.cacheDir, name)
        }

        fun createCacheFile(activity: FragmentActivity, name: String, text: String) {
            val outputFile = File(activity.cacheDir, name)
            val inputStream = ByteArrayInputStream(text.toByteArray(UTF_8))
            val outputStream = FileOutputStream(outputFile)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
        }


        @ExperimentalSerializationApi
        fun printAllCache(activity: FragmentActivity) {
            for (files in activity.cacheDir.listFiles()!!) {
                Log.i("TK Cache", files.toString())
                Log.i("TK File Output", File(files.toString()).readText(UTF_8))
                val obj =
                    Json.decodeFromString<List<ForemanData>>(File(files.toString()).readText(UTF_8))
                for (data in obj){
                    Log.i("TK Object Decode", data.toString())
                }

            }

        }

        fun deleteAll(activity: FragmentActivity) {
            for (files in activity.cacheDir.listFiles()!!) {
                files.delete()
            }
        }


    }

}