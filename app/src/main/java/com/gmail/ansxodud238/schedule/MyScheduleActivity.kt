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

        allSubjectList = getSubjectList(service)
        Log.d("all",allSubjectList!!.joinToString())
        myScheduleList = getScheduleList(service)

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
                Log.d("all",allSubjectList!!.joinToString())
                var list = getSubjectByWday(101,allSubjectList!!)

                drawDialogByWday(list)
                Log.d("myschedule",myScheduleList!!.joinToString())

            }
            R.id.btn_tue -> {

            }
            R.id.btn_wed -> {

            }
            R.id.btn_thu -> {

            }
            R.id.btn_fri -> {

            }
        }
    }

    //전체 삭제(MySchedule, AllView)
    fun wipeAll(){
        myScheduleList = null
        wipeTable()

    }
    //view 삭제(요일별 view 삭제)
    fun wipeTable(){

    }
    //onCreate 접근시 전체 과목 받아오기
    fun getSubjectList(service : Service) : ArrayList<Subject>{
        var subjectList = ArrayList<Subject>()
        service.getSubejctData().enqueue(object : Callback<ArrayList<Subject>>{
            override fun onResponse(
                call: Call<ArrayList<Subject>>,
                response: Response<ArrayList<Subject>>
            ) {
                subjectList = response.body()!!
                Log.d("getSubjectList","전 과목 목록 받아오기 성공")
            }

            override fun onFailure(call: Call<ArrayList<Subject>>, t: Throwable) {
                Log.d("getSubjectList","전 과목 목록 받아오기 실패")
            }
        })
        return subjectList

    }
    //onCreate 접근시 내 스케줄 받아오기
    fun getScheduleList(service: Service) : ArrayList<Subject>{
        var intent = intent
        var userId = intent.getStringExtra("userid").toInt()
        var scheduleList = ArrayList<Subject>()
        service.userHasSchedule(userId).enqueue(object : Callback<ArrayList<Subject>>{
            override fun onFailure(call: Call<ArrayList<Subject>>, t: Throwable) {
                Log.d("getScheduleList","내 스케줄 받아오기 실패")
            }

            override fun onResponse(
                call: Call<ArrayList<Subject>>,
                response: Response<ArrayList<Subject>>
            ) {
                scheduleList = response.body()!!
                Log.d("getScheduleList","내 스케줄 받아오기 성공")
            }
        })

        return scheduleList

    }
    //내 스케쥴 그리기
    fun drawTable(subjectList : ArrayList<Subject>){

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
                myScheduleList!!.forEach {



                    if(it.wday == list[which].wday){
                        var mySubjectStartTime = it.starttime
                        var mySubjectEndTime = it.endtime
                        var selectSubjectStartTime = list[which].starttime
                        var selectSubjectEndTime = list[which].endtime

                        //선택한
                        if(selectSubjectStartTime!! >= mySubjectStartTime!! && selectSubjectStartTime!! <= mySubjectEndTime!! || selectSubjectEndTime!! >= mySubjectStartTime!! && selectSubjectEndTime!! <= mySubjectEndTime!!){
                            val builderSelect = AlertDialog.Builder(this@MyScheduleActivity)
                            builderSelect.setTitle("선택 오류")
                            builderSelect.setMessage("선택한 과목은 이미 선택한 과목과 시간이 같습니다.\n선택한 과목으로 변경하시겠습니까?")
                            builder.setPositiveButton("예",object : DialogInterface.OnClickListener{
                                override fun onClick(dialog: DialogInterface?, which: Int) {
                                    myScheduleList!!.remove(it)
                                    myScheduleList!!.add(list[which])
                                }
                            })
                            builder.setNegativeButton("아니오",object : DialogInterface.OnClickListener{
                                override fun onClick(dialog: DialogInterface?, which: Int) {
                                }
                            })
                            val selectDialog = builderSelect.create()
                            selectDialog.show()
                        }else{
                            myScheduleList!!.add(list[which])
                        }
                    }else{
                        myScheduleList!!.add(list[which])
                    }

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
