package com.gmail.ansxodud238.schedule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.gmail.ansxodud238.schedule.data.Subject
import com.gmail.ansxodud238.schedule.data.User
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        btn_login.setOnClickListener {
            val getUserTypingTextId = edit_id.text.toString()
            val getUserTypingTextPw = edit_pw.text.toString()


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
            userInfo.username = getUserTypingTextId
            userInfo.userpw = getUserTypingTextPw

            service.userLogin(userInfo).enqueue(object : Callback<UserLoginResult>{
                override fun onFailure(call: Call<UserLoginResult>, t: Throwable) {
                    Toast.makeText(this@MainActivity,"서버 접속에 실패했습니다.",Toast.LENGTH_SHORT).show()
                }
                override fun onResponse(
                    call: Call<UserLoginResult>,
                    response: Response<UserLoginResult>
                ) {
                    val idCheck = response.body()?.result
                    if(idCheck == 1){
                        Toast.makeText(this@MainActivity,"로그인 성공!",Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@MainActivity,ScheduleActivity::class.java)
                        intent.putExtra("userid",response.body()?.user?.userid.toString())
                        startActivity(intent)
                    }else{
                        Toast.makeText(this@MainActivity,"로그인 실패!\n로그인 정보를 다시 확인해 주세요.${idCheck}",Toast.LENGTH_SHORT).show()
                    }
                }
            })


        }



        text_signup.setOnClickListener {
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }
    }




  }
