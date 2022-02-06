package com.volt.ui.pages


import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Build.VERSION_CODES.M
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.volt.R
import com.volt.databinding.FragmentAuthenticationBinding
import com.volt.voltdata.apidata.ApiHandler
import com.volt.voltdata.CacheHandler
import com.volt.voltdata.apidata.ActiveTimeSheetData
import com.volt.voltdata.apidata.EmployeeData
import com.volt.voltdata.apidata.FinalTimeSheetData
import com.volt.voltdata.appdata.AppHandler
import com.volt.voltdata.appdata.Pages
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

@RequiresApi(M)
class AuthenticationFragment : Fragment() {


    private var _binding: FragmentAuthenticationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val apiHandler = ApiHandler()

    override fun onStart() {
        super.onStart()
        updatePage()
    }


    private fun clearEmpList(linearLayout: LinearLayout) {
        if (linearLayout.size > 1) linearLayout.removeAllViews()
    }

    @RequiresApi(M)
    private fun generateEmpList() {

        val inList = arrayListOf<EmployeeData>()
        val outList = arrayListOf<EmployeeData>()
        for (entry in CacheHandler.getEmployeeCacheList(requireActivity())) {
            if (entry.status == 1) {
                inList.add(entry)
            } else {
                outList.add(entry)
            }
        }
        clearEmpList(requireView().findViewById(R.id.Linlay))

        //Generate Employee Card List
        createContainerList(requireView().findViewById(R.id.Linlay), outList)
        createPageBreak(requireView().findViewById(R.id.Linlay))
        createContainerList(requireView().findViewById(R.id.Linlay), inList)
    }

    private fun hideLin(){
        binding.Linlay.visibility = View.GONE
        binding.locationHeader.text = "Refreshing..."
    }

    private fun showLin() {
        binding.Linlay.visibility = View.VISIBLE
    }

    @RequiresApi(M)
    private fun refreshPage() {
        hideLin()
        GlobalScope.launch { // launches coroutine in main thread
            saveInDb()
        }
    }

    private fun reloadPage() {
        parentFragmentManager.beginTransaction().detach(this).commit()
        parentFragmentManager.beginTransaction().detach(this).commit()
        parentFragmentManager.beginTransaction().attach(this).commit()
        showLin()
    }


    @SuppressLint("NewApi")
    private suspend fun saveInDb() {
        Log.i("TKCheck",
            CacheHandler.getEmployeeCacheList(requireActivity()).isEmpty().toString() +
                    DateTimeFormatter
                        .ofPattern("ss.SSSSSS")
                        .withZone(ZoneOffset.UTC)
                        .format(Instant.now()))
        GlobalScope.async {
            delay(2000)
            updatePage()
            while (CacheHandler.getEmployeeCacheList(requireActivity()).isEmpty()) {
                delay(1000)
            }
//            Log.i("TKCheck",
//                CacheHandler.getEmployeeCacheList(requireActivity()).isEmpty().toString() +
//                        DateTimeFormatter
//                            .ofPattern("ss.SSSSSS")
//                            .withZone(ZoneOffset.UTC)
//                            .format(Instant.now()))
//            Log.i("TKCheck",
//                CacheHandler.getEmployeeCacheList(requireActivity()).toString())

            reloadPage()
        }
    }

    @RequiresApi(M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {


        _binding = FragmentAuthenticationBinding.inflate(inflater, container, false)
        val root: View = binding.root


        AppHandler.currentPage = Pages.AUTHENTICATION
        binding.locationHeader.text = AppHandler.currentForeman.currentLocation


        return root
    }


    fun newInstance(): AuthenticationFragment {
        return AuthenticationFragment()
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        generateEmpList()
    }


    fun dpToPx(dp: Int): Int {
        val density: Float = requireContext().resources
            .displayMetrics
            .density
        return (dp.toFloat() * density).roundToInt()
    }


    @ExperimentalSerializationApi
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    fun createContainerList(
        linearLayout: LinearLayout,
        emp_sheet: List<EmployeeData>,
    ) {
        for ((count, sheet) in emp_sheet.withIndex()) {
            Log.i("TK Table", "Creating a table row: $sheet")

            /* ---------------------------------------------------
            NAVBAR STYLE
            ----------------------------------------------------- */
            val constraint = ConstraintLayout(requireActivity())
            val constraintLayout: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                dpToPx(60)
            )

            constraint.layoutParams = constraintLayout
            constraint.setBackgroundResource(R.drawable.custom_constraint_light_grey)


/* ---------------------------------------------------
    Indicator Code
----------------------------------------------------- */

            val indicator = ConstraintLayout(requireActivity())
            indicator.id = View.generateViewId()
            val indicatorLayout: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(
                dpToPx(15),
                dpToPx(0)
            )
            indicatorLayout.bottomToBottom = ConstraintSet.PARENT_ID
            indicatorLayout.circleRadius = dpToPx(10)
            indicatorLayout.startToStart = ConstraintSet.PARENT_ID
            indicatorLayout.topToTop = ConstraintSet.PARENT_ID
            indicator.layoutParams = indicatorLayout
            if (sheet.status == 0) {
                indicator.setBackgroundResource(R.color.red)
            } else {
                indicator.setBackgroundResource(R.color.green)
            }

            constraint.addView(indicator)


/* ---------------------------------------------------
    TextView Code
----------------------------------------------------- */


            val empText = TextView(activity)
            empText.id = View.generateViewId()
            val txtlay: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
            )
            txtlay.marginStart = dpToPx(32)
            txtlay.bottomToBottom = ConstraintSet.PARENT_ID
            txtlay.topToTop = ConstraintSet.PARENT_ID
            txtlay.startToEnd = indicator.id
            empText.text = ("${sheet.first_name} ${sheet.last_name}")
            empText.setTextAppearance(R.style.fontForEmpListBlack)
            empText.layoutParams = txtlay
            constraint.addView(empText)


/* ---------------------------------------------------
    Button Code
----------------------------------------------------- */

            val timeBtn = Button(activity)
            timeBtn.id = View.generateViewId()
            val btnlay: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(
                dpToPx(150),
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
            )
            btnlay.bottomToBottom = ConstraintSet.PARENT_ID
            btnlay.endToEnd = ConstraintSet.PARENT_ID
            btnlay.topToTop = ConstraintSet.PARENT_ID
            timeBtn.layoutParams = btnlay
            if (sheet.status == 1) {
                timeBtn.text = "Check Out"
            } else {
                timeBtn.text = "Check In"
            }
            btnlay.marginEnd = dpToPx(12)
            timeBtn.setPadding(dpToPx(4))
            timeBtn.setTextAppearance(R.style.fontForEmpListWhite)
            timeBtn.setBackgroundResource(R.color.steel_blue)
            constraint.addView(timeBtn)

            /* ---------------------------------------------------
                Add Linear Invisible Buttons
            ----------------------------------------------------- */
            val invisLinLay = LinearLayout(requireActivity())
            invisLinLay.id = sheet.emp_id
            val invisConstraints: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT,
            )
            invisLinLay.gravity = Gravity.CENTER
            invisLinLay.weightSum = 10f
            invisLinLay.layoutParams = invisConstraints
            invisLinLay.elevation = 5f

            /* ---------------------------------------------------
                Button Code
            ----------------------------------------------------- */

            val manualInputButton = ConstraintLayout(requireActivity())
            val manbtnlay: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                4f
            )
            manualInputButton.layoutParams = manbtnlay
            manualInputButton.setBackgroundColor(Color.argb(0, 0, 255, 0))
            //manualInputButton.visibility = View.GONE
            invisLinLay.addView(manualInputButton)

            Log.i("TK Button", invisLinLay.id.toString())



            manualInputButton.setOnClickListener {
                Log.i("TK Button", invisLinLay.id.toString())
                val fragmentManager = this.requireParentFragment().parentFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                val fragment = EmployeePreviewFragment().newInstance()
                val arguments = Bundle()
                arguments.putInt("emp_id", invisLinLay.id)
                fragment.arguments = arguments
                fragmentTransaction.replace(
                    R.id.nav_host_fragment_activity_main,
                    fragment
                )
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }


            /* ---------------------------------------------------
              Button Code
          ----------------------------------------------------- */

            val signInButton = ConstraintLayout(requireActivity())
            val signbtnlay: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                6f
            )
            signInButton.layoutParams = signbtnlay
            signInButton.setBackgroundColor(Color.argb(0, 0, 0, 255))
            //signinButton.visibility = View.GONE
            invisLinLay.addView(signInButton)
            if (sheet.status == 1) {
                signInButton.setOnClickListener {

                    Log.i("TK Button", invisLinLay.id.toString())

                    val timeSheet = FinalTimeSheetData(
                        sheet.emp_id,
                        " ",
                        " ",
                        (timeToDouble(Calendar.getInstance().time.toString()
                            .split(" ")[3]) * 100).roundToInt() / 100.0
                    )
                    if (CacheHandler.finalSheetLogCheck(requireActivity(), timeSheet)) {
                        if (AppHandler.connection) {
                            apiHandler.postFinalTimeSheet(timeSheet)
                            Toast.makeText(
                                requireContext(),
                                "${sheet.first_name} ${sheet.last_name} Logged Out!",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            CacheHandler.addOffLineSignOut(timeSheet, requireActivity())
                            Toast.makeText(
                                requireContext(),
                                "${sheet.first_name} ${sheet.last_name} Logged Out Offline!",
                                Toast.LENGTH_LONG
                            ).show()
                            CacheHandler.printAllCache(requireActivity())
                            Log.i("TK Testing",
                                CacheHandler.getOfflineSignOutCacheList(requireActivity())
                                    .toString())
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "${sheet.first_name} ${sheet.last_name} Already In Offline Check Out!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    refreshPage()
                }
            } else {


                signInButton.setOnClickListener {

                    Log.i("TK Button", invisLinLay.id.toString())

                    val timeSheet = ActiveTimeSheetData(
                        sheet.id,
                        invisLinLay.id,
                        sheet.first_name,
                        sheet.last_name,
                        "time",
                        sheet.current_task,
                        sheet.current_location,
                        7,
                        "date"
                    )
                    if (CacheHandler.activeSheetLogCheck(requireActivity(), timeSheet)) {
                        if (AppHandler.connection) {
                            apiHandler.postActiveTimeSheet(timeSheet)
                            Toast.makeText(
                                requireActivity(),
                                "${sheet.first_name} ${sheet.last_name} Logged In!",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            CacheHandler.addOffLineSignIn(timeSheet, requireActivity())
                            Toast.makeText(
                                requireActivity(),
                                (requireView().findViewById(R.id.empSpinner) as Spinner).selectedItem.toString() + " Logged In Offline!",
                                Toast.LENGTH_LONG
                            ).show()
                            CacheHandler.printAllCache(requireActivity())
                            Log.i("TK Testing",
                                CacheHandler.getOfflineSignInCacheList(requireActivity())
                                    .toString())
                        }
                    }
                    refreshPage()
                }
            }




            constraint.addView(invisLinLay)
            linearLayout.addView(constraint, count + 1)
        }
    }

    private fun timeToDouble(time: String): Double {
        Log.i("TK Testing", (time.split(":")[0]))
        val hours = (time.split(":")[0]).toInt()
        val minutes = (time.split(":")[1]).toInt()
        return hours.toDouble() + ((minutes.toDouble() / 60))
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun createPageBreak(
        linearLayout: LinearLayout,
    ) {
        /* ---------------------------------------------------
        NAVBAR STYLE
        ----------------------------------------------------- */
        val constraint = ConstraintLayout(requireActivity())
        val constraintLayout: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            dpToPx(2)
        )
        constraintLayout.setMargins(dpToPx(5))
        constraint.layoutParams = constraintLayout
        constraint.setBackgroundColor(Color.parseColor("#77FFFFFF"))

        linearLayout.addView(constraint, 1)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @ExperimentalSerializationApi
    @RequiresApi(M)
    fun updatePage() {
        AppHandler.pageUpdate(requireActivity())
        if (AppHandler.connection) {
            CacheHandler.refreshCacheData(requireActivity())
        }
    }
}

