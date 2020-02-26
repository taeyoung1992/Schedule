package com.gmail.ansxodud238.schedule

import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.gmail.ansxodud238.schedule.data.Subject
import kotlinx.android.synthetic.main.activity_my_schedule.*
import okhttp3.OkHttpClient
import okhttp3.internal.Util.indexOf
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MyScheduleActivity : AppCompatActivity(), View.OnClickListener {


    private var allSubjectList : ArrayList<Subject>? = null
    private var myScheduleList : ArrayList<Subject>? = null
    private var dialogAdapter : ArrayAdapter<Subject>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_schedule)

        allSubjectList = ArrayList()
        myScheduleList = ArrayList()

        //retrofit, okhttp 연결
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

        getSubjectList(service)
        getScheduleList(service)

        initOnClickListener()



    }

    fun initOnClickListener(){
        btn_mon.setOnClickListener(this)
        btn_tue.setOnClickListener(this)
        btn_wed.setOnClickListener(this)
        btn_thu.setOnClickListener(this)
        btn_fri.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        var buttonId = v?.id
        when(buttonId){
            R.id.btn_mon -> {
                var list = getSubjectByWday(101,allSubjectList!!)


                drawDialogByWday(list)



            }
            R.id.btn_tue -> {
                var list = getSubjectByWday(102,allSubjectList!!)


                drawDialogByWday(list)

            }
            R.id.btn_wed -> {
                var list = getSubjectByWday(103,allSubjectList!!)


                drawDialogByWday(list)

            }
            R.id.btn_thu -> {
                var list = getSubjectByWday(104,allSubjectList!!)


                drawDialogByWday(list)

            }
            R.id.btn_fri -> {
                var list = getSubjectByWday(105,allSubjectList!!)


                drawDialogByWday(list)

            }
        }


    }

    //전체 삭제(MySchedule, AllView)
    fun wipeAll(){
        myScheduleList = null
        myScheduleList!!.forEach {
            wipeTable(it.wday!!)
        }

    }
    //view 삭제(요일별 view 삭제)
    fun wipeTable(wday: Int){
        myScheduleList!!.forEach {
            if(it.wday == wday){
                var textViewList = getWdayTextView(wday)
                textViewList.forEach {
                    it.text = ""
                    it.setBackgroundColor(Color.parseColor("#00ff0000"))
                }
            }
        }
    }
    //요일에 해당하는 textview id 가져오기
    fun getWdayTextView(wday: Int) : ArrayList<TextView>{
        var textViewList = ArrayList<TextView>()
        for(i in 9 .. 18){
            var textId = "text"+wday+i
            var resId = resources.getIdentifier(textId,"id",packageName)
            textViewList.add(findViewById(resId))
        }
        return textViewList
    }
    //onCreate 접근시 전체 과목 받아오기
    fun getSubjectList(service : Service){
        service.getSubejctData().enqueue(object : Callback<ArrayList<Subject>>{
            override fun onResponse(
                call: Call<ArrayList<Subject>>,
                response: Response<ArrayList<Subject>>
            ) {
                allSubjectList = response.body()!!
                Log.d("getSubjectList","전 과목 목록 받아오기 성공")

            }

            override fun onFailure(call: Call<ArrayList<Subject>>, t: Throwable) {
                Log.d("getSubjectList","전 과목 목록 받아오기 실패")
            }
        })

    }
    //onCreate 접근시 내 스케줄 받아오기
    fun getScheduleList(service: Service){
        var intent = intent
        var userId = intent.getStringExtra("userid").toInt()
        service.userHasSchedule(userId).enqueue(object : Callback<ArrayList<Subject>>{
            override fun onResponse(
                call: Call<ArrayList<Subject>>,
                response: Response<ArrayList<Subject>>
            ){
                myScheduleList = response.body()!!
                Log.d("getScheduleList","내 스케줄 받아오기 성공")
            }
            override fun onFailure(call: Call<ArrayList<Subject>>, t: Throwable) {
                Log.d("getScheduleList","내 스케줄 받아오기 실패")
            }

        })

    }
    //내 스케쥴 그리기
    fun drawTable(subjectList : ArrayList<Subject>){
        if(subjectList.size>0){
            subjectList.forEach {
                var startTime = it.starttime
                var endTime = it.endtime
                var textViewList = getWdayTextView(it.wday!!)
                for(i in startTime!!..endTime!!){
                    textViewList[i-9].text = it.subjectname
                    textViewList[i-9].setBackgroundColor(Color.parseColor(it.color!!))
                }
            }
        }

    }
    //요일별로 보여줄 리스트 반환
    fun getSubjectByWday(wday : Int, subjectList : ArrayList<Subject>) : ArrayList<Subject>{
        //리스트 생성
        var list = ArrayList<Subject>()
        //과목리스트 foreach
            //wday 일치
                //리스트 추가
        subjectList.forEach {
            if(it.wday == wday) list.add(it)
        }

        //리스트 리턴
        return list
    }
    //요일별 과목리스트 다이얼로그
    fun drawDialogByWday(list : ArrayList<Subject>){
        dialogAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,list)
        dialogAdapter!!.notifyDataSetChanged()

        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("과목을 선택해 주세요")
        builder.setAdapter(dialogAdapter,object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                if(myScheduleList!!.size>0) {
                    for (obj in myScheduleList!!) {
                        var myStartTime = obj.starttime
                        var myEndTime = obj.endtime
                        var choiceStartTime = list[which].starttime
                        var choiceEndTime = list[which].endtime
                        var index = which
                        if (choiceStartTime!! >= myStartTime!! && choiceStartTime!! <= myEndTime!! || choiceEndTime!! >= myStartTime!! && choiceEndTime!! <= myEndTime!!) {
                            val changeBuilder = AlertDialog.Builder(this@MyScheduleActivity)
                            changeBuilder.setTitle("겹치는 시간표 입니다.")
                            changeBuilder.setMessage("선택한 과목으로 변경하시겠습니까?")
                            changeBuilder.setPositiveButton("예",
                                object : DialogInterface.OnClickListener {
                                    override fun onClick(dialog: DialogInterface?, which: Int) {
                                        myScheduleList!!.removeAt(myScheduleList!!.indexOf(obj))
                                        myScheduleList!!.add(list[index])

                                        wipeTable(101)
                                        drawTable(myScheduleList!!)

                                    }

                                })
                            changeBuilder.setNegativeButton("아니오",
                                object : DialogInterface.OnClickListener {
                                    override fun onClick(dialog: DialogInterface?, which: Int) {
                                    }
                                })
                            val changeDialog = changeBuilder.create()
                            changeDialog.show()
                        } else {
                            myScheduleList!!.add(list[which])
                            wipeTable(101)
                            drawTable(myScheduleList!!)
                        }
                    }
                }else{
                    myScheduleList!!.add(list[which])
                    wipeTable(101)
                    drawTable(myScheduleList!!)
                }
            }

        })

        val dialog = builder.create()
        dialog.show()


    }




    //

//    fun initWdayLine(wday : Int,dayList : ArrayList<Subject>){
//
//        Log.d("dayList",dayList.joinToString())
//
//        var scheduleList = ArrayList<TextView>()
//        for(i in 9 .. 18){
//            var textId = "text"+wday+i
//            var resId = resources.getIdentifier(textId,"id",packageName)
//            scheduleList.add(findViewById(resId))
//        }
//
//        scheduleList.forEach{
//            it.text = ""
//            it.setBackgroundColor(Color.parseColor("#00ff0000"))
//        }
//        dayList.forEach {
//            var start = it.starttime
//            var end = it.endtime
//            for(i in start!!..end!!){
//                var timeView = scheduleList.get(i-9)
//
//                timeView.text = it.subjectname
//                timeView.setBackgroundColor(Color.parseColor(it.color!!))
//            }
//        }
//
//
//    }
}
