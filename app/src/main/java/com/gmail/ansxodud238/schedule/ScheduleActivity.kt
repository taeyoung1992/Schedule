package com.gmail.ansxodud238.schedule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.gmail.ansxodud238.schedule.data.Subject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ScheduleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)


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

        val intent = intent
        val userid = intent.getStringExtra("userid").toInt()


        service.userHasSchedule(userid).enqueue(object : Callback<ArrayList<Subject>>{
            override fun onFailure(call: Call<ArrayList<Subject>>, t: Throwable) {
                Log.d("response","fail")
            }

            override fun onResponse(call: Call<ArrayList<Subject>>, response: Response<ArrayList<Subject>>) {
                Log.d("response",response.body().toString())

            }
        })




    }
}
