package com.shimhg02.otpmessage.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.shimhg02.otpmessage.R
import com.shimhg02.otpmessage.broadcast_receivers.SMSListener
import com.shimhg02.otpmessage.callback_interfaces.Common
import com.shimhg02.otpmessage.util.Constants
import com.shimhg02.otpmessage.Service.UndeadService
import kotlinx.android.synthetic.main.activity_main.*

typealias Completion = (Boolean) -> Unit

class MainActivity : AppCompatActivity(), Common.OTPListener {

    private val tag = "MainActivity"
    private var tvDisplayMessage: TextView? = null
    val PREFERENCE = "com.shimhg02.otpmessage"
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //TODO : 아래 주석 인텐트는 해당 Notification을 눌렀을때 어떤 엑티비티를 띄울 것인지 정의.
        //val notificationIntent = Intent(this, TestActivity::class.java)
        //val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE)
        val editor = pref.edit()
        textViews.text = "메시지를 읽고있습니다 "+pref.getString("userName", "")+"님"
        findViews()
        checkReadSmsPermission()
        if (null == UndeadService.serviceIntent) {
            val intents = Intent(this, UndeadService::class.java)
            startService(intents)
            Toast.makeText(getApplicationContext(), "start service", Toast.LENGTH_LONG).show();
        } else {
            val intents = UndeadService.serviceIntent;
            Toast.makeText(getApplicationContext(), "already", Toast.LENGTH_LONG).show();
        }
        logout_btn.setOnClickListener {
            editor.putString("token", "")
            editor.apply()
            finish()
        }

    }

    private fun findViews() {
        tvDisplayMessage = findViewById(R.id.tv_display_msg)
    }

    private fun checkReadSmsPermission() {
        if (isReadSmsPermissionDenied()) {
            if (!shouldShowPermission()) {
                requestReadSmsPermission()
                return
            }

            showPermissionInfoDialog { isGranted ->
                if (!isGranted) return@showPermissionInfoDialog
                requestReadSmsPermission()
            }
        }

        bindSmsListener()
        Log.e(tag, "checkReadPermissions is called" + isReadSmsPermissionDenied())
    }

    private fun requestReadSmsPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECEIVE_SMS),
            Constants.MY_PERMISSIONS_REQUEST_RECEIVE_SMS
        )
    }

    private fun showPermissionInfoDialog(isGranted: Completion) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(getString(R.string.permission_req))
        dialogBuilder.setMessage(getString(R.string.permission_string))
        dialogBuilder.setPositiveButton(getString(R.string.grant_option)) { _, _ ->
            isGranted(true)
        }
        dialogBuilder.setNegativeButton(getString(R.string.deny_option)) { _, _ ->
            isGranted(false)
        }
    }

    private fun shouldShowPermission(): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.RECEIVE_SMS
        )
    }

    private fun isReadSmsPermissionDenied(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECEIVE_SMS
        ) != PackageManager.PERMISSION_GRANTED
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.MY_PERMISSIONS_REQUEST_RECEIVE_SMS) {
            if (grantResults.isEmpty()) {
                Log.e(tag, "grant results is empty")
                requestReadSmsPermission()
                return
            }

            Log.e(tag, "request permissions is called")
            //do your work

            bindSmsListener()
        }
    }

    private fun bindSmsListener() {
        SMSListener.bindListener(this)
    }

    override fun onOTPReceived(otp: String) {
        tvDisplayMessage!!.text = otp
    }


    override fun onDestroy() {
        var intents = Intent(this, UndeadService::class.java)
        super.onDestroy()
        if (null != intents) {
            stopService(intents)
        }
        SMSListener.unbindListener()
    }
}
