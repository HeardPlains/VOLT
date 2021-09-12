package com.volt.voltdata.appdata

import android.content.Context
import android.net.ConnectivityManager
import androidx.fragment.app.FragmentActivity
import com.volt.voltdata.CacheHandler

class AppHandler {
    companion object {

        var currentPage = Pages.SETTINGS
        var currentForeman: CurrentForeman = CurrentForeman("", "", 0, "")
        var authenticationToggle = false
        var currentCardAssign: CurrentCardAssign = CurrentCardAssign("", "")
        //var cm = ConnectivityManager

        fun pageUpdate(activity: FragmentActivity) {
            //cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }
    }

}