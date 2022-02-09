package com.volt.ui.pages

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
import com.volt.voltdata.CacheHandler
import com.volt.voltdata.appdata.AppHandler
import com.volt.voltdata.appdata.CurrentForeman
import com.volt.voltdata.appdata.Pages
import kotlinx.serialization.ExperimentalSerializationApi


class SettingsFragment : PreferenceFragmentCompat() {


    private var showCard = false



    @RequiresApi(Build.VERSION_CODES.M)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {


        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        AppHandler.pageUpdate(requireActivity())
        CacheHandler.printAllCache(requireActivity())



        AppHandler.currentPage = Pages.SETTINGS
        val foremanPreference: EditTextPreference? = findPreference("foreman_id")
        adminCheck(foremanPreference?.text.toString())
        emptyCells()

        updateList()
        foremanPreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, value ->
                adminCheck(value as String)
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

    @ExperimentalSerializationApi
    fun updateForemanID(string: String) {
        Log.i("TK Foreman Create", string)
        val foremanPreference: EditTextPreference? = findPreference("foreman_id")
        foremanPreference?.text = string
        adminCheck(foremanPreference!!.text.toString())
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
        scanPreference?.isVisible = false
        //Log.i("TK Bool", showCard.toString())
        cardPreferences?.isVisible = false

    }

    @ExperimentalSerializationApi
    private fun adminCheck(value: String) {
        val foremanName: EditTextPreference? = findPreference("foreman_name")
        try {
            for (sheet in CacheHandler.getEmployeeCacheList(requireActivity())) {
                Log.i("TK Foreman ID", sheet.emp_id.toString())
                Log.i("TK Foreman ID2", value)
                if (sheet.emp_id == value.toInt()) {
                    if(sheet.foreman == 1) {
                        AppHandler.currentForeman = CurrentForeman(
                            sheet.first_name,
                            sheet.last_name,
                            sheet.emp_id,
                            sheet.current_location
                        )
                        foremanName?.text = AppHandler.currentForeman.fullName
                        AppHandler.admin = true
                        (activity as MainActivity?)?.setSettingValue(1)
                        setVisibility()
                        return
                    }
                } else {
                    foremanName?.text = "";
                    AppHandler.admin = false
                    (activity as MainActivity?)?.setSettingValue(0)
                    showCard = false
                    setVisibility()
                }
            }
            Toast.makeText(requireActivity(), "Foreman ID Invalid!", Toast.LENGTH_LONG)
                .show()
        } catch (ex: NumberFormatException) {
            AppHandler.admin = false
            foremanName?.text = "";
            (activity as MainActivity?)?.setSettingValue(0)
            showCard = false;
            setVisibility()
            Log.i("TK", ex.toString())
            Toast.makeText(requireActivity(), "Please Enter a Number", Toast.LENGTH_LONG).show()
        }

    }

}


