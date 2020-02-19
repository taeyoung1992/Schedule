package com.gmail.ansxodud238.schedule

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_sign_up.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class SignUpActivity : AppCompatActivity() {

    private var flag : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        edit_sign_id.setOnFocusChangeListener(object : View.OnFocusChangeListener{
            override fun onFocusChange(v: View?, hasFocus: Boolean) {


                val getUserTypingTextId = edit_sign_id.text.toString()

                val okHttpClient = OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build()

                val baseUrl = "http://192.168.0.119:10001"

                var retrofit = Retrofit.Builder().baseUrl(baseUrl)
                    .client(okHttpClient).addConverterFactory(GsonConverterFactory.create())
                    .build()

                var service = retrofit.create(Service::class.java)

                val userNameCheck = UserNameCheck()
                userNameCheck.username = getUserTypingTextId


                service.userNameCheck(userNameCheck).enqueue(object : Callback<UserResponse>{
                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                        Toast.makeText(this@SignUpActivity,"아이디 중복 확인 요청 실패",Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(
                        call: Call<UserResponse>,
                        response: Response<UserResponse>
                    ) {

                        val result = response.body()?.result
                        if(result == 1){
                            if(getUserTypingTextId.length == 0) return
                            flag = true
                            Log.d("focus","${flag}")
                            text_idCheck.text  = "사용 가능한 아이디입니다."
                            text_idCheck.setTextColor(Color.GREEN)

                        }else{
                            text_idCheck.text = "이미 있는 아이디입니다."
                            text_idCheck.setTextColor(Color.red(1))
                            text_idCheck.setTextColor(Color.RED)
                        }
                    }
                })

            }
        })

        btn_signup.setOnClickListener {
            if (flag == true){
                val getTypingId = edit_sign_id.text.toString()
                val getTypingPw = edit_sign_pw.text.toString()

                val okHttpClient = OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build()

                val baseUrl = "http://192.168.0.119:10001"

                var retrofit = Retrofit.Builder().baseUrl(baseUrl)
                    .client(okHttpClient).addConverterFactory(GsonConverterFactory.create())
                    .build()

                var service = retrofit.create(Service::class.java)

                val userInfo = UserInfo()
                userInfo.username = getTypingId
                userInfo.userpw = getTypingPw

                service.addUser(userInfo).enqueue(object : Callback<UserResponse>{
                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                        Toast.makeText(this@SignUpActivity,"회원가입 요청 실패",Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(
                        call: Call<UserResponse>,
                        response: Response<UserResponse>
                    ) {
                        val result = response.body()?.result
                        if(result == 1){
                            Toast.makeText(this@SignUpActivity,"회원가입 성공!",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this@SignUpActivity,"회원가입 실!",Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }




        btn_cancel.setOnClickListener {
            finish()
        }


    }
}
