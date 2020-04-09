package com.shimhg02.otpmessage.Activity



import android.content.Intent
import android.os.Build
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.shimhg02.otpmessage.R
import com.shimhg02.otpmessage.Retrofit.Client
import com.shimhg02.otpmessage.util.BaseActivity
import kotlinx.android.synthetic.main.activity_signup.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : BaseActivity() {

    override var viewId: Int = R.layout.activity_signup
    override var toolbarId: Int? = R.id.toolbar
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        findViewById<Button>(R.id.login_btn).setOnClickListener {
            Client.retrofitService.logUp(name_tv.text.toString(), id_tv.text.toString(),pw_tv.text.toString()).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                    when (response!!.code()) {
                        200 -> {
                            Toast.makeText(this@SignUpActivity, "회원가입 성공", Toast.LENGTH_LONG).show()
                            val intent = Intent(baseContext, LoginActivity::class.java)
                            startActivity(intent)
                            finish ()
                        }
                        404 -> Toast.makeText(this@SignUpActivity, "회원가입 실패: 중복된 계정", Toast.LENGTH_LONG).show()//Toast.makeText(this@SignupProfileActivity, "회원가입 실패 : 이미 있는 계정입니다", Toast.LENGTH_LONG).show()
                        500 -> Toast.makeText(this@SignUpActivity,"서버에러", Toast.LENGTH_LONG).show()//Toast.makeText(this@SignupProfileActivity, "회원가입 실패 : 서버 오류", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Void>?, t: Throwable?) {

                }


            })
        }
    }

}