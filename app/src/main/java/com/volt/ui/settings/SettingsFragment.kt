package com.volt.ui.settings

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.preference.*
import com.volt.MainActivity
import com.volt.MainActivity.FragmentRefreshListener
import com.volt.R
import com.volt.voltdata.ApiHandler
import com.volt.voltdata.CacheHandler
import com.volt.voltdata.apidata.ForemanData
import com.volt.voltdata.appdata.AppHandler
import com.volt.voltdata.appdata.CurrentForeman
import com.volt.voltdata.appdata.Pages
import kotlinx.serialization.ExperimentalSerializationApi


class SettingsFragment : PreferenceFragmentCompat() {

    private var isAdmin = false
    private var showCard = false
    private val apiHandler = ApiHandler()

    @RequiresApi(Build.VERSION_CODES.M)
    @ExperimentalSerializationApi
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        //CacheHandler.deleteAll(requireActivity())
       // CacheHandler.refreshCacheData(requireActivity())
        CacheHandler.printAllCache(requireActivity())
        //CacheHandler.deleteAll(requireActivity())


        AppHandler.currentPage = Pages.SETTINGS

        adminCheck()
        emptyCells()

        updateList()
        val foremanPreference: EditTextPreference? = findPreference("foreman_id")
        foremanPreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, _ ->
                adminCheck()
                true
            }
        val scanTypePreference: ListPreference? = findPreference("scan_type")
        showCard = scanTypePreference?.entry!! == "Create Foreman Card"
        setVisibility()
        scanTypePreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                Log.i("TK Scan Type", newValue.toString())
                when (newValue) {
                    "Show Card Data" -> {
                        (activity as MainActivity?)?.setSettingValue(1)
                        showCard = false
                        setVisibility()
                    }
                    "Wipe Card Data" -> {
                        (activity as MainActivity?)?.setSettingValue(2)
                        showCard = false
                        setVisibility()
                    }
                    "Create Foreman Card" -> {
                        (activity as MainActivity?)?.setSettingValue(3)
                        showCard = true
                        emptyCells()
                        setVisibility()
                    }
                }
                true
            }


        val foremanCardName: EditTextPreference? = findPreference("card_foreman_name")
        Log.i("TK Card", foremanCardName?.text.toString())
        (activity as MainActivity?)?.setCardName(foremanCardName?.text.toString())
        foremanCardName?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                Log.i("TK Testing", newValue.toString())
                (activity as MainActivity?)?.setCardName(newValue.toString())
                true
            }
        val foremanCardID: EditTextPreference? = findPreference("card_foreman_id")
        Log.i("TK Card", foremanCardID?.text.toString())
        (activity as MainActivity?)?.setCardName(foremanCardID?.text.toString())
        foremanCardID?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                Log.i("TK Testing", newValue.toString())
                (activity as MainActivity?)?.setCardID(newValue.toString())
                true
            }

        (activity as MainActivity?)!!.setFragmentRefreshListener(object : FragmentRefreshListener {
            override fun onRefresh(string: String) {
                updateForemanID(string)
            }
        })


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CacheHandler.printForemanCache(requireActivity())
    }

    private fun updateList() {
        val scanTypePreference: ListPreference? = findPreference("scan_type")
        showCard = scanTypePreference?.entry!! == "Create Foreman Card"
        setVisibility()
        var newValue = scanTypePreference?.entry.toString()
        when (newValue) {
            "Show Card Data" -> {
                (activity as MainActivity?)?.setSettingValue(1)
                showCard = false
                setVisibility()
            }
            "Wipe Card Data" -> {
                (activity as MainActivity?)?.setSettingValue(2)
                showCard = false
                setVisibility()
            }
            "Create Foreman Card" -> {
                (activity as MainActivity?)?.setSettingValue(3)
                showCard = true
                emptyCells()
                setVisibility()
            }
        }
    }


    private fun emptyCells() {
        val foremanCardName: EditTextPreference? = findPreference("card_foreman_name")
        val foremanCardID: EditTextPreference? = findPreference("card_foreman_id")
        foremanCardName?.text = ""
        foremanCardID?.text = ""
    }

    fun updateForemanID(string: String) {
        Log.i("TK Foreman Create", string)
        val foremanPreference: EditTextPreference? = findPreference("foreman_id")
        foremanPreference?.text = string
        adminCheck()
        setVisibility()

    }


    override fun onStart() {
        super.onStart()
        setVisibility()
    }


    override fun onResume() {
        super.onResume()
        setVisibility()
    }


    private fun setVisibility() {
        val scanPreference: PreferenceCategory? = findPreference("scan_preferences")
        val cardPreferences: PreferenceCategory? = findPreference("card_preferences")
        scanPreference?.isVisible = isAdmin
        Log.i("TK Bool", showCard.toString())
        cardPreferences?.isVisible = showCard

    }


    private fun adminCheck() {
        apiHandler.getForemanData(::adminCheckHandler)
    }


    private fun adminCheckHandler(
        foremanData:
        List<ForemanData>
    ) {
        val foremanName: EditTextPreference? = findPreference("foreman_name")
        try {
            for (sheet in foremanData) {
                val foremanPreference: EditTextPreference? = findPreference("foreman_id")
                val foremanID = foremanPreference?.text.toString().toInt()
                Log.i("TK Foreman ID", sheet.foreman_id.toString())
                if (sheet.foreman_id == foremanID) {
                    AppHandler.currentForeman = CurrentForeman(
                        sheet.first_name,
                        sheet.last_name,
                        sheet.foreman_id,
                        sheet.current_location
                    )
                    foremanName?.text = AppHandler.currentForeman.fullName
                    isAdmin = true
                    (activity as MainActivity?)?.setAdmin(isAdmin)
                    (activity as MainActivity?)?.setSettingValue(1)
                    setVisibility()
                    return
                }
                foremanName?.text = "";
                isAdmin = false
                (activity as MainActivity?)?.setAdmin(isAdmin)
                (activity as MainActivity?)?.setSettingValue(0)
                showCard = false
                setVisibility()
                Toast.makeText(requireActivity(), "Foreman ID Invalid!", Toast.LENGTH_LONG)
                    .show()
            }
        } catch (ex: NumberFormatException) {
            isAdmin = false
            foremanName?.text = "";
            (activity as MainActivity?)?.setAdmin(isAdmin)
            (activity as MainActivity?)?.setSettingValue(0)
            showCard = false;
            setVisibility()
            Log.i("TK", ex.toString())
            Toast.makeText(requireActivity(), "Please Enter a Number", Toast.LENGTH_LONG).show()
        }

    }

}


