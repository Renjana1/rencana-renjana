package com.example.renjana

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONObject


class PreCheckoutActivity : AppCompatActivity() {
    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    @BindView(R.id.etDates)
    lateinit var etDates: EditText

    @BindView(R.id.etTimes)
    lateinit var etTimes: EditText

    @BindView(R.id.etPromoCode)
    lateinit var etPromoCode: EditText

    @BindView(R.id.etMatter)
    lateinit var etMatter: EditText

    @BindView(R.id.etGoal)
    lateinit var etGoal: EditText

    @BindView(R.id.rvConsultant)
    lateinit var rvConsultant: RecyclerView

    lateinit var btnContinue: Button

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.coordinatorLayout)
    lateinit var coordinatorLayout: CoordinatorLayout

    private var consultacyType: String ?= null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private var TAG:String ?= PreCheckoutActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_checkout)
        ButterKnife.bind(this)

        loadConsultant()
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        val type: String = intent.getStringExtra("type").toString()

        val bottomSheetBehavior: BottomSheetBehavior<*>?
        val bottomSheet: View  = findViewById(R.id.bottom_sheet)
        val txtBtmTotal: TextView = bottomSheet.findViewById(R.id.txtBtmTotal)
        val txtConsultancyType: TextView = bottomSheet.findViewById(R.id.txtBtmType)

        when (type) {
            "1" -> {
                consultacyType = "Renjana Pro"
                txtConsultancyType.text = consultacyType
                txtBtmTotal.text = "Rp. 100.000"
            }
            "2" -> {
                consultacyType = "Renjana Cendekia"
                txtConsultancyType.text = consultacyType
                txtBtmTotal.text = "Rp. 90.000"
            }
            else -> {
                consultacyType = "Rencana Renjana"
                txtConsultancyType.text = consultacyType
                txtBtmTotal.text = getString(R.string.eightythousand)
            }
        }

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = consultacyType.toString()

            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        btnContinue = findViewById(R.id.btnContinue)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        btnContinue.setOnClickListener {
            when {
                etDates.text.trim().toString().isEmpty() -> {
                    Snackbar.make(coordinatorLayout,"Please input the dates", Snackbar.LENGTH_SHORT).show()
                }
                etTimes.text.trim().toString().isEmpty() -> {
                    Snackbar.make(coordinatorLayout,"Please input the times", Snackbar.LENGTH_SHORT).show()
                }
                etMatter.text.trim().toString().isEmpty()->{
                    Snackbar.make(coordinatorLayout,"Please input the consultacy matter", Snackbar.LENGTH_SHORT).show()
                }
                else -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    val btnSubmit: Button = bottomSheet.findViewById(R.id.btnBtmSubmit)
                    val txtBtmDates: TextView = bottomSheet.findViewById(R.id.txtBtmDates)
                    val txtBtmTimes: TextView = bottomSheet.findViewById(R.id.txtBtmTimes)
                    val txtBtmMatter: TextView = bottomSheet.findViewById(R.id.txtBtmMatter)

                    txtBtmMatter.text = etMatter.text
                    txtBtmDates.text = etDates.text
                    txtBtmTimes.text = etTimes.text
                    btnSubmit.setOnClickListener {
                        addTransaction(etDates.text.toString(), etTimes.text.toString(),
                            etMatter.text.toString(), etGoal.text.toString(),
                            etPromoCode.text.toString())
                    }
                }
            }
        }

        etDates.setOnClickListener { datePicker() }

        etTimes.setOnClickListener { timePicker() }

    }

    private fun loadConsultant() {
        val postUrl = "http://api.renjanaconsulting.com/api/loadconsultant.php"
        val volleyRequestQueue = Volley.newRequestQueue(this)

        val strReq: StringRequest = object : StringRequest(
            Method.GET,postUrl,
            Response.Listener { response ->
                Log.e(TAG, "response: $response")
                // Handle Server response here
                try {
                    val responseObj = JSONObject(response)
                    val message = responseObj.getString("message")
                    if (responseObj.has("array")) {
                        //val data = responseObj.getJSONObject("data")
                        // Handle your server response data here

                        val data = responseObj.getJSONObject("array")

                    }
                    Log.e(TAG, message)

                } catch (e: Exception) { // caught while parsing the response
                    Log.e(TAG, "problem occurred")
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { volleyError -> // error occurred
                Log.e(TAG, "problem occurred, volley error: " + volleyError.message)
            }) {


            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {

                val headers: MutableMap<String, String> = HashMap()
                // Add your Header paramters here
                return headers
            }
        }
        volleyRequestQueue.add(strReq)
    }

    private fun addTransaction(dates: String?, times: String?, matter: String?, goal: String?, promoCode: String?) {
        val postUrl = "http://api.renjanaconsulting.com/api/addtransaction.php"
        val volleyRequestQueue = Volley.newRequestQueue(this)

        val parameters: MutableMap<String, String> = HashMap()
        // Add your parameters in HashMap
        parameters["dates"] = dates.toString()
        parameters["times"] = times.toString()
        parameters["matter"] = matter.toString()
        parameters["goal"] = goal.toString()
        parameters["promoCode"] = promoCode.toString()
        parameters["uid"] = mAuth.currentUser?.uid.toString()


        val strReq: StringRequest = object : StringRequest(
            Method.POST,postUrl,
            Response.Listener { response ->
                Log.e(TAG, "response: $response")
                // Handle Server response here
                try {
                    val responseObj = JSONObject(response)
                    val message = responseObj.getString("message")
                    if (responseObj.has("data")) {
                        //val data = responseObj.getJSONObject("data")
                        // Handle your server response data here
                    }
                    Log.e(TAG, message)

                } catch (e: Exception) { // caught while parsing the response
                    Log.e(TAG, "problem occurred")
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { volleyError -> // error occurred
                Log.e(TAG, "problem occurred, volley error: " + volleyError.message)
            }) {

            override fun getParams(): MutableMap<String, String> {
                return parameters
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {

                val headers: MutableMap<String, String> = HashMap()
                // Add your Header paramters here
                return headers
            }
        }
        volleyRequestQueue.add(strReq)
    }

    @SuppressLint("SetTextI18n")
    private fun timePicker() {
        val c: Calendar = Calendar.getInstance()
        var mHour: Int = c.get(Calendar.HOUR)
        var mMinute : Int = c.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(this,
            { view, hourOfDay, minute ->
                mHour = hourOfDay
                mMinute = minute
                etTimes.setText("$hourOfDay:$minute")
            }, mHour, mMinute, true
        )
        timePickerDialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n")
    private fun datePicker() {

        // Get Current Date
        val c: Calendar = Calendar.getInstance()
        val mYear: Int = c.get(Calendar.YEAR)
        val mMonth: Int = c.get(Calendar.MONTH)
        val mDay: Int = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this,
            { view, year, monthOfYear, dayOfMonth ->
                etDates.setText(dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                //*************Call Time Picker Here ********************
            }, mYear, mMonth, mDay
        )
        datePickerDialog.show()
    }
}