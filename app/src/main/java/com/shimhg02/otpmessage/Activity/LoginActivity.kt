package com.shimhg02.otpmessage.Activity


import android.content.Intent
import android.widget.Toast
import com.shimhg02.otpmessage.Data.Login
import com.shimhg02.otpmessage.Preference.SharedPref
import com.shimhg02.otpmessage.R
import com.shimhg02.otpmessage.Retrofit.Client
import com.shimhg02.otpmessage.util.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : BaseActivity() {
    val PREFERENCE = "com.shimhg02.otpmessage"
    override var viewId: Int = R.layout.activity_login
    override var toolbarId: Int? = R.id.toolbar
     override fun onCreate() {
        SharedPref.openSharedPrep(this)
         val pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE)
         val editor = pref.edit()
         System.out.println("(LOGD) sf_token: " + pref.getString("token", ""))
         System.out.println("(LOGD) userName: " + pref.getString("userName", ""))
         if(pref.getString("token","").toString() !== ""){
             System.out.println("(LOGD) sf_token: " + pref.getString("token", ""))
             System.out.println("(LOGD) userName: " + pref.getString("userName", ""))
             val intent = Intent(baseContext, MainActivity::class.java)
             startActivity(intent)
             finish()
         }
        login_btn.setOnClickListener {
                Client.retrofitService.logIn(id_tv.text.toString(), pw_tv.text.toString()).enqueue(object : Callback<Login> {
                    override fun onResponse(call: Call<Login>?, response: Response<Login>?) {
                        when (response!!.code()) {
                            200 -> {
                                editor.putString("userName", response.body()!!.name.toString())
                                editor.putString("token", response.body()!!.token.toString())
                                editor.apply()
                                System.out.println("(LOGD) sf_token: " + pref.getString("token", ""))
                                System.out.println("(LOGD) userName: " + pref.getString("userName", ""))
                                val intent = Intent(baseContext, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                startActivity(intent)
                                finish()
                            }
                            404 ->          Toast.makeText(this@LoginActivity, "로그인 실패: PW나 ID를 다시 확인하세요.", Toast.LENGTH_LONG).show()//Toast.makeText(this@LoginActivity, "로그인 실패 : 아이디나 비번이 올바르지 않습니다", Toast.LENGTH_LONG).show()
                            500 ->          Toast.makeText(this@LoginActivity, "서버에러", Toast.LENGTH_LONG).show()//Toast.makeText(this@LoginActivity, "로그인 실패 : 서버 오류", Toast.LENGTH_LONG).show()
                        }
                    }
                    override fun onFailure(call: Call<Login>?, t: Throwable?) {

                    }
                })
        }
        signup_go.setOnClickListener { startActivity(Intent(this@LoginActivity, SignUpActivity::class.java)) }
    }

}