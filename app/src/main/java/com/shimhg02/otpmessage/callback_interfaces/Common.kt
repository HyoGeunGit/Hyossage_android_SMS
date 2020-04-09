package com.shimhg02.otpmessage.callback_interfaces

interface Common {
    interface OTPListener {
        fun onOTPReceived(otp: String)
    }
}