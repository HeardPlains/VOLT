package com.volt

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.*
import android.nfc.tech.Ndef
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.volt.voltdata.apidata.ActiveTimeSheetData
import com.volt.voltdata.apidata.FinalTimeSheetData
import com.volt.databinding.ActivityMainBinding
import com.volt.voltdata.CacheHandler
import com.volt.voltdata.VOLTApi
import com.volt.voltdata.appdata.AppHandler
import com.volt.voltdata.appdata.Pages
import kotlinx.serialization.ExperimentalSerializationApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random


//const val BASE_URL = "http://10.0.0.119:80/"
const val BASE_URL = "http://73.243.134.128:80/"


@ExperimentalSerializationApi
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var empID = ""
    private var task = ""
    private var location = ""
    private var settingValue = 0
    private var cardName = ""
    private var cardID = ""
    private var fragmentRefreshListener: FragmentRefreshListener? = null

    private var nfcAdapter: NfcAdapter? = null

    // Pending intent for NFC intent foreground dispatch.
    // Used to read all NDEF tags while the app is running in the foreground.
    private var nfcPendingIntent: PendingIntent? = null



    fun setCardName(str: String) {
        cardName = str
    }

    fun setCardID(str: String) {
        cardID = str
    }



    fun setSettingValue(num: Int) {
        settingValue = num
    }


    fun setEmpId(str: String) {
        empID = str
    }

    fun setTask(str: String) {
        task = str
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()
        AppHandler.pageUpdate(this)
        if(AppHandler.connection){
            CacheHandler.refreshCacheData(this)
        }
    }



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //CacheHandler.deleteAll(requireActivity())
        //CacheHandler.refreshCacheData(this)

        //CacheHandler.deleteAll(requireActivity())



        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_check_in, R.id.navigation_dashboard, R.id.settingsFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        Log.i("TK Click NFC supported", (nfcAdapter != null).toString())
        Log.i("TK Click enabled", (nfcAdapter?.isEnabled).toString())


        // Read all tags when app is running and in the foreground
        // Create a generic PendingIntent that will be deliver to this activity. The NFC stack
        // will fill in the intent with the details of the discovered tag before delivering to
        // this activity.
        nfcPendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        )

        initNfcAdapter()



    }

    private fun initNfcAdapter() {
        val nfcManager = getSystemService(Context.NFC_SERVICE) as NfcManager
        nfcAdapter = nfcManager.defaultAdapter
    }

    override fun onResume() {
        super.onResume()
        enableNfcForegroundDispatch()
    }

    private fun enableNfcForegroundDispatch() {
        try {
            val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val nfcPendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
            nfcAdapter?.enableForegroundDispatch(this, nfcPendingIntent, null, null)
        } catch (ex: IllegalStateException) {
            Log.e("TK Error", "Error enabling NFC foreground dispatch", ex)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val action = intent.action
        Log.i("TK Action", action.toString())

        try {
            if (NfcAdapter.ACTION_TECH_DISCOVERED == action
                || NfcAdapter.ACTION_NDEF_DISCOVERED == action ||
                NfcAdapter.ACTION_TAG_DISCOVERED == action
            ) {
                val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) ?: return
                val ndef = Ndef.get(tag) ?: return
                if (ndef.isWritable) {
                    when (AppHandler.currentPage) {
                        Pages.AUTHENTICATION -> authenticationNFC(ndef)
                        Pages.SETTINGS -> settingsNFC(ndef)
                        Pages.ASSIGN_CARD -> assignCard(ndef)
                        else -> "$AppHandler.currentPage operator is invalid operator."
                    }
                }
            } else {
                Toast.makeText(this, "Write on text box!", Toast.LENGTH_SHORT).show()
            }
        } catch (Ex: Exception) {
            Toast.makeText(this, Ex.message, Toast.LENGTH_SHORT).show()
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun authenticationNFC(ndef: Ndef) {
        if (AppHandler.admin) {
            if (AppHandler.authenticationToggle) {
                checkInNFC(ndef)
            } else {
                checkOutNFC(ndef)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun assignCard(ndef: Ndef){
        if (AppHandler.admin) {
            val message = NdefMessage(
                arrayOf(
                    NdefRecord.createTextRecord("en", AppHandler.currentCardAssign.empId),
                    NdefRecord.createTextRecord("en", AppHandler.currentCardAssign.task),
                    NdefRecord.createTextRecord(
                        "en",
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                    )
                )
            )
            ndef.connect()
            ndef.writeNdefMessage(message)
            ndef.close()
            Toast.makeText(
                applicationContext,
                "Card Assigned to ${AppHandler.currentCardAssign.empId}",
                Toast.LENGTH_SHORT
            )
                .show()
        } else {
            Toast.makeText(
                this,
                "Please Enter Foreman ID in Settings Before Scanning a Card",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkInNFC(ndef: Ndef) {
        if (AppHandler.admin) {
            val records = ndef.cachedNdefMessage.records
            for (record in records) {
                Log.i("TK Record", String(record.payload))
            }
            val name = records[0].toString().split(" ")
            val firstName = name[0]
            val lastName = name[1]
            val locationCode = location
            val taskCode = task
            val timeSheet = ActiveTimeSheetData(
                1,
                Random.nextInt(1000000, 9999999),
                firstName,
                lastName,
                "Time",
                locationCode,
                taskCode,
                7
            )

            Log.i("TK Click", timeSheet.toString())
            postNewEmployee(timeSheet)
            Toast.makeText(
                applicationContext,
                "$empID Signed In",
                Toast.LENGTH_SHORT
            )
                .show()
        } else {
            Toast.makeText(
                this,
                "Please Enter ID in Settings Before Scanning a Card",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    private fun checkOutNFC(ndef: Ndef) {
        if (AppHandler.admin) {
            val records = ndef.cachedNdefMessage.records
            for (record in records) {
                Log.i("TK Record", String(record.payload))
            }
            Toast.makeText(
                applicationContext,
                String(records[0].payload).substring(3) + " Signed Out",
                Toast.LENGTH_SHORT
            )
                .show()
            val name = String(records[0].payload).substring(3).split(" ")
            val firstName = name[0]
            val lastName = name[1]
            val timeSheet = FinalTimeSheetData(firstName, lastName)
            Log.i("TK Click", timeSheet.toString())
            postFinalTimeSheet(timeSheet)
        } else {
            Toast.makeText(
                this,
                "Please Enter ID in Settings Before Scanning a Card",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    private fun settingsNFC(ndef: Ndef) {
        if (AppHandler.admin) {
            if (settingValue == 1) {
                val records = ndef.cachedNdefMessage.records
                var list = ""
                for (record in records) {
                    Log.i("TK Record", String(record.payload).substring(3))
                    list += String(record.payload).substring(3) + "\r"
                }
                Toast.makeText(
                    applicationContext,
                    list,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (settingValue == 2) {
                val message = NdefMessage(
                    arrayOf(NdefRecord.createTextRecord("en", "Empty"))
                )
                ndef.connect()
                ndef.writeNdefMessage(message)
                ndef.close()
                Toast.makeText(
                    applicationContext,
                    "Card Has Been Wiped!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (settingValue == 3) {
                val message = NdefMessage(
                    arrayOf(
                        NdefRecord.createTextRecord("en", "AppHandler.admin"),
                        NdefRecord.createTextRecord("en", cardName),
                        NdefRecord.createTextRecord("en", cardID),
                    )
                )
                ndef.connect()
                ndef.writeNdefMessage(message)
                ndef.close()
                Toast.makeText(
                    applicationContext,
                    "Foreman Card Created",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {

            val records = ndef.cachedNdefMessage.records
            for (record in records) {
                Log.i("TK Record", String(record.payload).substring(3))
            }
            if (String(records[0].payload).substring(3) == "AppHandler.admin") {
                setCardID(String(records[2].payload).substring(3))

                if (getFragmentRefreshListener() != null) {
                    getFragmentRefreshListener()!!.onRefresh(String(records[2].payload).substring(3))
                }

                Toast.makeText(
                    applicationContext,
                    "Foreman Card Accepted!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Invalid AppHandler.admin Card",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getFragmentRefreshListener(): FragmentRefreshListener? {
        return fragmentRefreshListener
    }

    fun setFragmentRefreshListener(fragmentRefreshListener: FragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener
    }

    interface FragmentRefreshListener {
        fun onRefresh(str: String)
    }

    override fun onPause() {
        disableNfcForegroundDispatch()
        super.onPause()
    }

    private fun disableNfcForegroundDispatch() {
        try {
            nfcAdapter?.disableForegroundDispatch(this)
        } catch (ex: IllegalStateException) {
            Log.e("TK", "Error disabling NFC foreground dispatch", ex)
        }
    }


    private fun postNewEmployee(time_sheet: ActiveTimeSheetData) {
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VOLTApi::class.java)

        val call: Call<ActiveTimeSheetData> = api.postTimeSheet(time_sheet)

        call.enqueue(object : Callback<ActiveTimeSheetData> {
            override fun onResponse(
                call: Call<ActiveTimeSheetData>,
                response: Response<ActiveTimeSheetData>
            ) {
            }

            override fun onFailure(call: Call<ActiveTimeSheetData>, t: Throwable) {
                Log.i("TK Error", "${t.message}")
            }
        })
    }

    private fun postFinalTimeSheet(time_sheet: FinalTimeSheetData) {
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VOLTApi::class.java)

        val call: Call<FinalTimeSheetData> = api.postFinalSheet(time_sheet)

        call.enqueue(object : Callback<FinalTimeSheetData> {
            override fun onResponse(
                call: Call<FinalTimeSheetData>,
                response: Response<FinalTimeSheetData>
            ) {
            }

            override fun onFailure(call: Call<FinalTimeSheetData>, t: Throwable) {
                Log.i("TK Error", "${t.message}")
            }
        })
    }

}