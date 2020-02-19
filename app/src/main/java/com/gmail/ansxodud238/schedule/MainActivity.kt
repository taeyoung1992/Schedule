package com.gmail.ansxodud238.schedule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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


    private lateinit var databaseHelper_User : DatabaseHelper_User
    private lateinit var databasehelper_Subject :DatabaseHelper_Subject



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseHelper_User = DatabaseHelper_User(this)
        databasehelper_Subject = DatabaseHelper_Subject(this)

        webView.loadUrl("http://192.168.0.119:10001/api/subject")
        

        val list : ArrayList<Subject> = databasehelper_Subject.getWDay(101)

        for(i in 0 until list.size){
            Log.d("List",list.get(i).toString())
        }






            val baseURL = "http://192.168.0.119:10001"
        try {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            var retrofit = Retrofit.Builder().baseUrl("http://192.168.0.119:10001").client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create()).build()

            var service = retrofit.create(Service::class.java)

            service.getSubejctData().enqueue(object : Callback<ArrayList<Subject>> {
                override fun onFailure(call: Call<ArrayList<Subject>>, t: Throwable) {
                    Log.d("response","fail")
                }

                override fun onResponse(
                    call: Call<ArrayList<Subject>>,
                    response: Response<ArrayList<Subject>>
                ) {
                    Log.d("response",response.body().toString())
                }


            })

            val userInfo = UserInfo()
            userInfo.username = "moooo"
            userInfo.userpw = "woooo"


            service.addUser(userInfo).enqueue(object : Callback<UserResponse>{
                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Log.d("response","fail")
                }

                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    Log.d("response",response.body().toString())

                    if(response.body()?.result == 1){
                        Log.d("response","소리질러")
                    }
                }


            })

            val userNameCheck = UserNameCheck()
            userNameCheck.username = "moooo"

            service.userNameCheck(userNameCheck).enqueue(object : Callback<UserResponse>{
                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Log.d("response","fail")
                }

                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    Log.d("response",response.body().toString())

                    if(response.body()?.result == 1){
                        Log.d("response","아이디 중복")
                    }
                }
            })

            val userInfo2 = UserInfo()
            userInfo2.username = "moo"
            userInfo2.userpw = "woo"

            service.userLogin(userInfo2).enqueue(object : Callback<UserResponse>{
                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Log.d("response","fail")
                }

                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    Log.d("response",response.body().toString())

                    if(response.body()?.result == 1){
                        Log.d("response","로그인 성공")
                    }
                }
            })
        }catch (e:Exception){
            e.printStackTrace()
        }






    }

    fun userDataJsonParse(userData : String){
        val jObject = JSONObject(userData)
        val jArray = jObject.getJSONArray("posts")

        for (i in 0 until jArray.length()) {
            var obj = jArray.getJSONObject(i)
            val user = User()
            user.userId = obj.getInt("userId")
            user.userName = obj.getString("userName")
            user.userPw = obj.getString("userPw")

            if(databaseHelper_User.checkUserId(user.userId!!))
                databaseHelper_User.addUser(user)

        }
    }

    fun subjectDataJsonParse(subjectData : String){
        val jObject = JSONObject(subjectData)
        val jArray = jObject.getJSONArray("posts")

        for(i in 0 until jArray.length()){
            var obj = jArray.getJSONObject(i)
            val subject = Subject()
            subject.subjectid = obj.getInt("subjectId")
            subject.subjectname = obj.getString("subjectName")
            subject.starttime = obj.getInt("startTime")
            subject.endtime = obj.getInt("endTime")
            subject.wday = obj.getInt("wDay")

            if(databasehelper_Subject.checkSubjectId(subject.subjectid!!))
                 databasehelper_Subject.addSubject(subject)

        }
    }




}
