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
import kotlinx.android.synthetic.main.activity_my_schedule.btn_save

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import kotlin.properties.Delegates

class MyScheduleActivity : AppCompatActivity(), View.OnClickListener {

    private var allSubjectList: ArrayList<Subject>? = null
    private var myScheduleList: ArrayList<Subject>? = null
    private var dialogAdapter: ArrayAdapter<Subject>? = null
    private lateinit var service: Service
    private var userId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_schedule)

        allSubjectList = ArrayList()
        myScheduleList = ArrayList()

        val mainActivity = MainActivity()
        service = mainActivity.initService()

        getSubjectList(service)
        getScheduleList(service)

        Log.d("allSubject", allSubjectList!!.joinToString())
        Log.d("mySubject", myScheduleList!!.joinToString())
        initOnClickListener()


    }

    fun initOnClickListener() {
        btn_mon.setOnClickListener(this)
        btn_tue.setOnClickListener(this)
        btn_wed.setOnClickListener(this)
        btn_thu.setOnClickListener(this)
        btn_fri.setOnClickListener(this)
        btn_save.setOnClickListener(this)
        btn_clear.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        var buttonId = v?.id
        when (buttonId) {
            R.id.btn_mon -> {

                var list = getSubjectByWday(101, allSubjectList!!)

                drawDialogByWday(list)

            }
            R.id.btn_tue -> {
                var list = getSubjectByWday(102, allSubjectList!!)

                drawDialogByWday(list)

            }
            R.id.btn_wed -> {
                var list = getSubjectByWday(103, allSubjectList!!)

                drawDialogByWday(list)

            }
            R.id.btn_thu -> {
                var list = getSubjectByWday(104, allSubjectList!!)

                drawDialogByWday(list)

            }
            R.id.btn_fri -> {
                var list = getSubjectByWday(105, allSubjectList!!)

                drawDialogByWday(list)

            }
            R.id.btn_save -> saveMySchduleList(service)
            R.id.btn_clear -> wipeAll()
        }


    }

    //전체 삭제(MySchedule, AllView)
    fun wipeAll() {
        wipeTable()
        myScheduleList = ArrayList()

    }

    //view 삭제(textview 삭제)
    fun wipeTable() {
        myScheduleList!!.forEach {

            var textViewList = getWdayTextView(it.wday!!)
            textViewList.forEach {
                it.text = ""
                it.setBackgroundColor(Color.parseColor("#00ff0000"))
            }

        }
    }

    //요일에 해당하는 textview id 가져오기
    fun getWdayTextView(wday: Int): ArrayList<TextView> {
        var textViewList = ArrayList<TextView>()
        for (i in 9..18) {
            var textId = "text" + wday + i
            var resId = resources.getIdentifier(textId, "id", packageName)
            textViewList.add(findViewById(resId))
        }
        return textViewList
    }

    //onCreate 접근시 전체 과목 받아오기
    fun getSubjectList(service: Service) {
        service.getSubejctData().enqueue(object : Callback<ArrayList<Subject>> {
            override fun onResponse(
                call: Call<ArrayList<Subject>>,
                response: Response<ArrayList<Subject>>
            ) {
                allSubjectList = response.body()!!
                Log.d("getSubjectList", "전 과목 목록 받아오기 성공")

            }

            override fun onFailure(call: Call<ArrayList<Subject>>, t: Throwable) {
                Log.d("getSubjectList", "전 과목 목록 받아오기 실패")
                Log.d("getSubjectList", t.toString())

            }
        })

    }

    //onCreate 접근시 내 스케줄 받아오기
    fun getScheduleList(service: Service) {
        var intent = intent
        userId = intent.getStringExtra("userid").toInt()
        service.userHasSchedule(userId).enqueue(object : Callback<ArrayList<Subject>> {
            override fun onResponse(
                call: Call<ArrayList<Subject>>,
                response: Response<ArrayList<Subject>>
            ) {
                myScheduleList = response.body()!!
                wipeTable()
                drawTable(myScheduleList!!)
                Log.d("getScheduleList", "내 스케줄 받아오기 성공")
            }

            override fun onFailure(call: Call<ArrayList<Subject>>, t: Throwable) {
                Log.d("getScheduleList", "내 스케줄 받아오기 실패")
                Log.d("getScheduleList", t.toString())
            }

        })

    }

    //내 스케쥴 그리기
    fun drawTable(subjectList: ArrayList<Subject>) {
        if (subjectList.size > 0) {
            subjectList.forEach {
                var startTime = it.starttime
                var endTime = it.endtime
                var textViewList = getWdayTextView(it.wday!!)
                for (i in startTime!!..endTime!!) {
                    textViewList[i - 9].text = it.subjectname
                    textViewList[i - 9].setBackgroundColor(Color.parseColor(it.color!!))
                }
            }
        }

    }

    //요일별로 보여줄 리스트 반환
    fun getSubjectByWday(wday: Int, subjectList: ArrayList<Subject>): ArrayList<Subject> {
        //리스트 생성
        var list = ArrayList<Subject>()
        //과목리스트 foreach
        //wday 일치
        //리스트 추가
        subjectList.forEach {
            if (it.wday == wday) list.add(it)
        }

        //리스트 리턴
        return list
    }

    //요일별 과목리스트 다이얼로그
    fun drawDialogByWday(list: ArrayList<Subject>) {
        dialogAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        dialogAdapter!!.notifyDataSetChanged()



        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("과목을 선택해 주세요")
        builder.setAdapter(dialogAdapter, object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                if (myScheduleList?.size!! > 0) {
                    var myWdayList = getSubjectByWday(list[which].wday!!, myScheduleList!!)
                    if (myWdayList.size == 0) {
                        myScheduleList!!.add(list[which])
                        wipeTable()
                        drawTable(myScheduleList!!)
                        return
                    }
                    var overlapList:ArrayList<Subject> = ArrayList()
                    for (i in 0..myWdayList.size - 1) {
                        var myStartTime = myWdayList[i].starttime
                        var myEndTime = myWdayList[i].endtime
                        var choiceStartTime = list[which].starttime
                        var choiceEndTime = list[which].endtime
                        if (choiceStartTime!! >= myStartTime!! && choiceStartTime!! <= myEndTime!! || choiceEndTime!! >= myStartTime!! && choiceEndTime!! <= myEndTime!!) {
                            overlapList.add(myWdayList[i])
                        }
                    }
                    if(overlapList.size>0){
                        val changeBuilder = AlertDialog.Builder(this@MyScheduleActivity)
                        changeBuilder.setTitle("겹치는 시간표 입니다.")
                        changeBuilder.setMessage("선택한 과목으로 변경하시겠습니까?")
                        changeBuilder.setPositiveButton("예",
                            object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface?, index: Int) {
                                    for(i in 0..overlapList.size-1){
                                        myScheduleList!!.remove(overlapList.get(i))
                                }
                                    myScheduleList!!.add(list[which])
                                    wipeTable()
                                    drawTable(myScheduleList!!)
                                }
                            })
                        changeBuilder.setNegativeButton("아니오",
                            object : DialogInterface.OnClickListener {
                                override fun onClick(dialog: DialogInterface?, index: Int) {

                                }
                            })
                        val changeDialog = changeBuilder.create()
                        changeDialog.show()
                    }else{
                        myScheduleList!!.add(list[which])
                        wipeTable()
                        drawTable(myScheduleList!!)
                    }

                } else {
                    myScheduleList!!.add(list[which])
                    wipeTable()
                    drawTable(myScheduleList!!)
                }
            }

        })


        val dialog = builder.create()
        dialog.show()
    }

    fun saveMySchduleList(service: Service) {
        var saveMyScheduleList = ArrayList<UserDataSend>()
        if(myScheduleList != null) {
            for (i in 0..myScheduleList!!.size.minus(1)) {
                var userData = UserDataSend()
                userData.subjectid = myScheduleList!![i].subjectid
                userData.userid = userId
                saveMyScheduleList!!.add(userData)

                service.userData(saveMyScheduleList).enqueue(object : Callback<UserResponse> {
                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                        Toast.makeText(
                            this@MyScheduleActivity,
                            "서버와 접속할 수 없어서 데이터를 저장하지 못하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onResponse(
                        call: Call<UserResponse>,
                        response: Response<UserResponse>
                    ) {
                        var resultCheck = response.body()!!.result
                        if (resultCheck == 1) {
                            Toast.makeText(this@MyScheduleActivity, "데이터 저장 성공!", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(
                                this@MyScheduleActivity,
                                "데이터를 저장하는데 실패했습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
            }
        }else{
            Toast.makeText(this,"저장할 과목을 선택하세요.",Toast.LENGTH_SHORT).show()
        }


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
