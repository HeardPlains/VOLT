package com.volt.voltdata.appdata

import com.volt.voltdata.apidata.ActiveTimeSheetData

class OfflinePosts {
    private val offlinePosts = arrayListOf<ActiveTimeSheetData>()

    fun addOfflinePost(sheetData: ActiveTimeSheetData){
        offlinePosts.add(sheetData)
    }

    fun getOfflinePosts(): ArrayList<ActiveTimeSheetData> {
        return offlinePosts
    }
}