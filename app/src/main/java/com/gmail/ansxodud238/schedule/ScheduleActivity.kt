package com.gmail.ansxodud238.schedule

import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.gmail.ansxodud238.schedule.data.Subject
import com.gmail.ansxodud238.schedule.data.User
import kotlinx.android.synthetic.main.activity_schedule.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class ScheduleActivity : AppCompatActivity(), View.OnClickListener {

    private var mySubjectList: ArrayList<Subject>? = null
    private var allSubjectList: ArrayList<Subject>? = null
    private lateinit var mondaySubjectList: ArrayList<Subject>
    private lateinit var tuesdaySubjectList: ArrayList<Subject>
    private lateinit var wednesdaySubjectList: ArrayList<Subject>
    private lateinit var thursdaySubjectList: ArrayList<Subject>
    private lateinit var fridaySubjectList: ArrayList<Subject>
    private lateinit var arrayAdapter: ArrayAdapter<Subject>
    private lateinit var dialogList: ArrayList<Subject>
    private var selectListMon: ArrayList<Subject>? = null
    private var selectListTue: ArrayList<Subject>? = null
    private var selectListWed: ArrayList<Subject>? = null
    private var selectListThu: ArrayList<Subject>? = null
    private var selectListFri: ArrayList<Subject>? = null
    private var sendList: ArrayList<Subject>? = null
    private var sendListPorm: ArrayList<UserDataSend>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

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

        //로그인하면서 받아온 사용자 정보
        val intent = intent
        val userid = intent.getStringExtra("userid").toInt()


        //서버에서 전체 데이터 가져오기
        service.getSubejctData().enqueue(object : Callback<ArrayList<Subject>> {
            override fun onFailure(call: Call<ArrayList<Subject>>, t: Throwable) {
                Toast.makeText(
                    this@ScheduleActivity,
                    "전체 과목 목록을 가져오는데에 실패했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(
                call: Call<ArrayList<Subject>>,
                response: Response<ArrayList<Subject>>
            ) {
                allSubjectList = response.body()
            }
        })

        //다이얼로그에 넣을 리스트 생성
        dialogList = ArrayList()
        //다이얼로그에 연결할 어댑터 생성
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dialogList)

        selectListMon = ArrayList()
        selectListTue = ArrayList()
        selectListWed = ArrayList()
        selectListThu = ArrayList()
        selectListFri = ArrayList()

        initButtonOnClickListener()

        btn_save.setOnClickListener {
            sendList = ArrayList()
            if (selectListMon!!.size > 0)
                for (i in 0..selectListMon!!.size.minus(1)) {
                    sendList!!.add(selectListMon!![i])
                }
            if (selectListTue!!.size > 0)
                for (i in 0..selectListTue!!.size.minus(1)) {
                    sendList!!.add(selectListTue!![i])
                }
            if (selectListWed!!.size > 0)
                for (i in 0..selectListWed!!.size.minus(1)) {
                    sendList!!.add(selectListWed!![i])
                }
            if (selectListThu!!.size > 0)
                for (i in 0..selectListThu!!.size.minus(1)) {
                    sendList!!.add(selectListThu!![i])
                }
            if (selectListFri!!.size > 0)
                for (i in 0..selectListFri!!.size.minus(1)) {
                    sendList!!.add(selectListFri!![i])
                }

            sendListPorm = ArrayList()


            for (i in 0..sendList!!.size.minus(1)) {
                var userData = UserDataSend()
                userData.subjectid = sendList!![i].subjectid
                userData.userid = userid
                sendListPorm!!.add(userData)
            }
            service.userData(sendListPorm!!).enqueue(object : Callback<UserResponse> {
                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(
                        this@ScheduleActivity,
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
                        Toast.makeText(this@ScheduleActivity, "데이터 저장 성공!", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(
                            this@ScheduleActivity,
                            "데이터를 저장하는데 실패했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


            })
        }







        btn_load.setOnClickListener {


            //서버에서 내가 저장한 데이터 가녀오기
            service.userHasSchedule(userid).enqueue(object : Callback<ArrayList<Subject>> {
                override fun onFailure(call: Call<ArrayList<Subject>>, t: Throwable) {
                    Toast.makeText(
                        this@ScheduleActivity,
                        "저장한 과목을 불러오는데에 실패했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onResponse(
                    call: Call<ArrayList<Subject>>,
                    response: Response<ArrayList<Subject>>
                ) {
                    mySubjectList = response.body()

                }
            })


        }

    }

    //요일 버튼에 이벤트 연결
    fun initButtonOnClickListener() {
        btn_mon.setOnClickListener(this)
        btn_tue.setOnClickListener(this)
        btn_wed.setOnClickListener(this)
        btn_thu.setOnClickListener(this)
        btn_fri.setOnClickListener(this)


    }

    override fun onClick(v: View?) {
        //각각 요일별로 과목을 저장할 리스트 생성
        var getButtonid = v?.id
        mondaySubjectList = ArrayList()
        tuesdaySubjectList = ArrayList()
        wednesdaySubjectList = ArrayList()
        thursdaySubjectList = ArrayList()
        fridaySubjectList = ArrayList()

        //전체 데이터를 가져와서 요일별로 나눠서 리스트에 저장
        for (i in 0..allSubjectList!!.size - 1) {
            if (allSubjectList?.get(i)?.wday == 101) {
                mondaySubjectList?.add(allSubjectList!!.get(i))
            } else if (allSubjectList?.get(i)?.wday == 102) {
                tuesdaySubjectList?.add(allSubjectList!!.get(i))
            } else if (allSubjectList?.get(i)?.wday == 103) {
                wednesdaySubjectList?.add(allSubjectList!!.get(i))
            } else if (allSubjectList?.get(i)?.wday == 104) {
                thursdaySubjectList?.add(allSubjectList!!.get(i))
            } else if (allSubjectList?.get(i)?.wday == 105) {
                fridaySubjectList?.add(allSubjectList!!.get(i))
            } else {
                Log.d("else", "fail")
            }
        }

        when (getButtonid) {
            R.id.btn_mon -> {

                //다이얼로그 생성 전에 이미 저장되어 있는 데이터 삭제
                dialogList.removeAll(dialogList)
                //다이얼로그에 저장할 데이터 저장
                for (i in 0..mondaySubjectList.size - 1) {
                    dialogList.add(mondaySubjectList.get(i))
                }
                //어댑터 리스트 갱신
                arrayAdapter.notifyDataSetChanged()
                //다이얼로그 생성
                val builder = AlertDialog.Builder(this)
                builder.setTitle("입력할 과목을 선택해 주세요.")
                //다이얼로그와 어댑터를 연결하고 과목 선택시 이벤트 발생
                builder.setAdapter(arrayAdapter, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        if (selectListMon!!.size > 0) {
                            for (index in 0..selectListMon!!.size - 1) {
                                var oldDataStartTime = selectListMon!!.get(index).starttime
                                var oldDataEndTime = selectListMon!!.get(index).endtime
                                var newDataStartTime = dialogList[which].starttime
                                var newDataEndTime = dialogList[which].endtime

                                if (newDataStartTime!! >= oldDataStartTime!! && newDataStartTime!! <= oldDataEndTime!!) {
                                    Toast.makeText(
                                        this@ScheduleActivity,
                                        "중복된 시간을 선택하셨습니다.\n선택한 과목으로 변경합니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    selectListMon!!.removeAt(index)
                                    selectListMon!!.add(dialogList[which])
                                } else if (newDataEndTime!! >= oldDataStartTime!! && newDataEndTime!! <= oldDataEndTime!!) {
                                    Toast.makeText(
                                        this@ScheduleActivity,
                                        "중복된 시간을 선택하셨습니다.\n선택한 과목으로 변경합니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    selectListMon!!.removeAt(index)
                                    selectListMon!!.add(dialogList[which])

                                } else {
                                    selectListMon!!.add(dialogList[which])

                                }
                            }
                        } else {
                            selectListMon!!.add(dialogList[which])
                        }

                        //각각 view를 가져와서 데이터를 저장할 준비
                        val scheduleList = intArrayOf(
                            R.id.text1019, R.id.text10110, R.id.text10111,
                            R.id.text10112, R.id.text10113, R.id.text10114,
                            R.id.text10115, R.id.text10116, R.id.text10117, R.id.text10118
                        )
                        //데이터 초기화
                        for (delIndex in 0..scheduleList.size.minus(1)) {
                            var text = findViewById<TextView>(scheduleList[delIndex])
                            text.text = ""
                            text.setBackgroundColor(Color.parseColor("#00ff0000"))
                        }
                        //각각의 view에 데이터 저장
                        for (i in 0..selectListMon!!.size.minus(1)) {
                            var start = selectListMon!!.get(i).starttime!!
                            var end = selectListMon!!.get(i).endtime!!
                            for (j in start!!..end!!) {
                                val index = j - 9
                                var textViewId = scheduleList.get(index)
                                var text = findViewById<TextView>(textViewId)
                                text.text = selectListMon!!.get(i).subjectname
                                text.setBackgroundColor(Color.parseColor(selectListMon!!.get(i).color.toString()))
                            }
                        }
                    }

                })
                var dialog = builder.create()
                dialog.show()
            }
            R.id.btn_tue -> {
                //다이얼로그 생성 전에 이미 저장되어 있는 데이터 삭제
                dialogList.removeAll(dialogList)
                //다이얼로그에 저장할 데이터 저장
                for (i in 0..tuesdaySubjectList.size - 1) {
                    dialogList.add(tuesdaySubjectList.get(i))
                }
                //어댑터 리스트 갱신
                arrayAdapter.notifyDataSetChanged()
                //다이얼로그 생성
                val builder = AlertDialog.Builder(this)
                builder.setTitle("입력할 과목을 선택해 주세요.")
                //다이얼로그와 어댑터를 연결하고 과목 선택시 이벤트 발생
                builder.setAdapter(arrayAdapter, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        if (selectListTue!!.size > 0) {
                            for (index in 0..selectListTue!!.size - 1) {
                                var oldDataStartTime = selectListTue!!.get(index).starttime
                                var oldDataEndTime = selectListTue!!.get(index).endtime
                                var newDataStartTime = dialogList[which].starttime
                                var newDataEndTime = dialogList[which].endtime

                                if (newDataStartTime!! >= oldDataStartTime!! && newDataStartTime!! <= oldDataEndTime!!) {
                                    Toast.makeText(
                                        this@ScheduleActivity,
                                        "중복된 시간을 선택하셨습니다.\n선택한 과목으로 변경합니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    selectListTue!!.removeAt(index)
                                    selectListTue!!.add(dialogList[which])
                                } else if (newDataEndTime!! >= oldDataStartTime!! && newDataEndTime!! <= oldDataEndTime!!) {
                                    Toast.makeText(
                                        this@ScheduleActivity,
                                        "중복된 시간을 선택하셨습니다.\n선택한 과목으로 변경합니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    selectListTue!!.removeAt(index)
                                    selectListTue!!.add(dialogList[which])

                                } else {
                                    selectListTue!!.add(dialogList[which])

                                }
                            }
                        } else {
                            selectListTue!!.add(dialogList[which])
                        }

                        Log.d("list", selectListTue!!.size.toString())
                        //각각 view를 가져와서 데이터를 저장할 준비
                        val scheduleList = intArrayOf(
                            R.id.text1029, R.id.text10210, R.id.text10211,
                            R.id.text10212, R.id.text10213, R.id.text10214,
                            R.id.text10215, R.id.text10216, R.id.text10217, R.id.text10218
                        )
                        //데이터 초기화
                        for (delIndex in 0..scheduleList.size - 1) {
                            var text = findViewById<TextView>(scheduleList[delIndex])
                            text.text = ""
                            text.setBackgroundColor(Color.parseColor("#00ff0000"))

                        }
                        //각각의 view에 데이터 저장
                        for (i in 0..selectListTue!!.size - 1) {
                            var start = selectListTue!!.get(i).starttime!!
                            var end = selectListTue!!.get(i).endtime!!
                            for (j in start!!..end!!) {
                                val index = j - 9
                                var textViewId = scheduleList.get(index)
                                var text = findViewById<TextView>(textViewId)
                                text.text = selectListTue!!.get(i).subjectname
                                text.setBackgroundColor(Color.parseColor(selectListTue!!.get(i).color.toString()))
                            }
                        }
                    }

                })
                var dialog = builder.create()
                dialog.show()

            }
            R.id.btn_wed -> {
                //다이얼로그 생성 전에 이미 저장되어 있는 데이터 삭제
                dialogList.removeAll(dialogList)
                //다이얼로그에 저장할 데이터 저장
                for (i in 0..wednesdaySubjectList.size - 1) {
                    dialogList.add(wednesdaySubjectList.get(i))
                }
                //어댑터 리스트 갱신
                arrayAdapter.notifyDataSetChanged()
                //다이얼로그 생성
                val builder = AlertDialog.Builder(this)
                builder.setTitle("입력할 과목을 선택해 주세요.")
                //다이얼로그와 어댑터를 연결하고 과목 선택시 이벤트 발생
                builder.setAdapter(arrayAdapter, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        if (selectListWed!!.size > 0) {
                            for (index in 0..selectListWed!!.size - 1) {
                                var oldDataStartTime = selectListWed!!.get(index).starttime
                                var oldDataEndTime = selectListWed!!.get(index).endtime
                                var newDataStartTime = dialogList[which].starttime
                                var newDataEndTime = dialogList[which].endtime

                                if (newDataStartTime!! >= oldDataStartTime!! && newDataStartTime!! <= oldDataEndTime!!) {
                                    Toast.makeText(
                                        this@ScheduleActivity,
                                        "중복된 시간을 선택하셨습니다.\n선택한 과목으로 변경합니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    selectListWed!!.removeAt(index)
                                    selectListWed!!.add(dialogList[which])
                                } else if (newDataEndTime!! >= oldDataStartTime!! && newDataEndTime!! <= oldDataEndTime!!) {
                                    Toast.makeText(
                                        this@ScheduleActivity,
                                        "중복된 시간을 선택하셨습니다.\n선택한 과목으로 변경합니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    selectListWed!!.removeAt(index)
                                    selectListWed!!.add(dialogList[which])

                                } else {
                                    selectListWed!!.add(dialogList[which])

                                }
                            }
                        } else {
                            selectListWed!!.add(dialogList[which])
                        }

                        Log.d("list", selectListWed!!.size.toString())
                        //각각 view를 가져와서 데이터를 저장할 준비
                        val scheduleList = intArrayOf(
                            R.id.text1039, R.id.text10310, R.id.text10311,
                            R.id.text10312, R.id.text10313, R.id.text10314,
                            R.id.text10315, R.id.text10316, R.id.text10317, R.id.text10318
                        )
                        //데이터 초기화
                        for (delIndex in 0..scheduleList.size - 1) {
                            var text = findViewById<TextView>(scheduleList[delIndex])
                            text.text = ""
                            text.setBackgroundColor(Color.parseColor("#00ff0000"))
                        }
                        //각각의 view에 데이터 저장
                        for (i in 0..selectListWed!!.size - 1) {
                            var start = selectListWed!!.get(i).starttime!!
                            var end = selectListWed!!.get(i).endtime!!
                            for (j in start!!..end!!) {
                                val index = j - 9
                                var textViewId = scheduleList.get(index)
                                var text = findViewById<TextView>(textViewId)
                                text.text = selectListWed!!.get(i).subjectname
                                text.setBackgroundColor(Color.parseColor(selectListWed!!.get(i).color.toString()))
                            }
                        }
                    }

                })
                var dialog = builder.create()
                dialog.show()
            }
            R.id.btn_thu -> {//다이얼로그 생성 전에 이미 저장되어 있는 데이터 삭제
                dialogList.removeAll(dialogList)
                //다이얼로그에 저장할 데이터 저장
                for (i in 0..thursdaySubjectList.size - 1) {
                    dialogList.add(thursdaySubjectList.get(i))
                }
                //어댑터 리스트 갱신
                arrayAdapter.notifyDataSetChanged()
                //다이얼로그 생성
                val builder = AlertDialog.Builder(this)
                builder.setTitle("입력할 과목을 선택해 주세요.")
                //다이얼로그와 어댑터를 연결하고 과목 선택시 이벤트 발생
                builder.setAdapter(arrayAdapter, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        if (selectListThu!!.size > 0) {
                            for (index in 0..selectListThu!!.size - 1) {
                                var oldDataStartTime = selectListThu!!.get(index).starttime
                                var oldDataEndTime = selectListThu!!.get(index).endtime
                                var newDataStartTime = dialogList[which].starttime
                                var newDataEndTime = dialogList[which].endtime

                                if (newDataStartTime!! >= oldDataStartTime!! && newDataStartTime!! <= oldDataEndTime!!) {
                                    Toast.makeText(
                                        this@ScheduleActivity,
                                        "중복된 시간을 선택하셨습니다.\n선택한 과목으로 변경합니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    selectListThu!!.removeAt(index)
                                    selectListThu!!.add(dialogList[which])
                                } else if (newDataEndTime!! >= oldDataStartTime!! && newDataEndTime!! <= oldDataEndTime!!) {
                                    Toast.makeText(
                                        this@ScheduleActivity,
                                        "중복된 시간을 선택하셨습니다.\n선택한 과목으로 변경합니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    selectListThu!!.removeAt(index)
                                    selectListThu!!.add(dialogList[which])

                                } else {
                                    selectListThu!!.add(dialogList[which])

                                }
                            }
                        } else {
                            selectListThu!!.add(dialogList[which])
                        }

                        Log.d("list", selectListThu!!.size.toString())
                        //각각 view를 가져와서 데이터를 저장할 준비
                        val scheduleList = intArrayOf(
                            R.id.text1049, R.id.text10410, R.id.text10411,
                            R.id.text10412, R.id.text10413, R.id.text10414,
                            R.id.text10415, R.id.text10416, R.id.text10417, R.id.text10418
                        )
                        //데이터 초기화
                        for (delIndex in 0..scheduleList.size - 1) {
                            var text = findViewById<TextView>(scheduleList[delIndex])
                            text.text = ""
                            text.setBackgroundColor(Color.parseColor("#00ff0000"))

                        }
                        //각각의 view에 데이터 저장
                        for (i in 0..selectListThu!!.size - 1) {
                            var start = selectListThu!!.get(i).starttime!!
                            var end = selectListThu!!.get(i).endtime!!
                            for (j in start!!..end!!) {
                                val index = j - 9
                                var textViewId = scheduleList.get(index)
                                var text = findViewById<TextView>(textViewId)
                                text.text = selectListThu!!.get(i).subjectname
                                text.setBackgroundColor(Color.parseColor(selectListThu!!.get(i).color.toString()))

                            }
                        }
                    }

                })
                var dialog = builder.create()
                dialog.show()
            }
            R.id.btn_fri -> {
                //다이얼로그 생성 전에 이미 저장되어 있는 데이터 삭제
                dialogList.removeAll(dialogList)
                //다이얼로그에 저장할 데이터 저장
                for (i in 0..fridaySubjectList.size - 1) {
                    dialogList.add(fridaySubjectList.get(i))
                }
                //어댑터 리스트 갱신
                arrayAdapter.notifyDataSetChanged()
                //다이얼로그 생성
                val builder = AlertDialog.Builder(this)
                builder.setTitle("입력할 과목을 선택해 주세요.")
                //다이얼로그와 어댑터를 연결하고 과목 선택시 이벤트 발생
                builder.setAdapter(arrayAdapter, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        if (selectListFri!!.size > 0) {
                            for (index in 0..selectListFri!!.size - 1) {
                                var oldDataStartTime = selectListFri!!.get(index).starttime
                                var oldDataEndTime = selectListFri!!.get(index).endtime
                                var newDataStartTime = dialogList[which].starttime
                                var newDataEndTime = dialogList[which].endtime

                                if (newDataStartTime!! >= oldDataStartTime!! && newDataStartTime!! <= oldDataEndTime!!) {
                                    Toast.makeText(
                                        this@ScheduleActivity,
                                        "중복된 시간을 선택하셨습니다.\n선택한 과목으로 변경합니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    selectListFri!!.removeAt(index)
                                    selectListFri!!.add(dialogList[which])
                                } else if (newDataEndTime!! >= oldDataStartTime!! && newDataEndTime!! <= oldDataEndTime!!) {
                                    Toast.makeText(
                                        this@ScheduleActivity,
                                        "중복된 시간을 선택하셨습니다.\n선택한 과목으로 변경합니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    selectListFri!!.removeAt(index)
                                    selectListFri!!.add(dialogList[which])

                                } else {
                                    selectListFri!!.add(dialogList[which])

                                }
                            }
                        } else {
                            selectListFri!!.add(dialogList[which])
                        }

                        Log.d("list", selectListFri!!.size.toString())
                        //각각 view를 가져와서 데이터를 저장할 준비
                        val scheduleList = intArrayOf(
                            R.id.text1059, R.id.text10510, R.id.text10511,
                            R.id.text10512, R.id.text10513, R.id.text10514,
                            R.id.text10515, R.id.text10516, R.id.text10517, R.id.text10518
                        )
                        //데이터 초기화
                        for (delIndex in 0..scheduleList.size - 1) {
                            var text = findViewById<TextView>(scheduleList[delIndex])
                            text.text = ""
                            text.setBackgroundColor(Color.parseColor("#00ff0000"))

                        }
                        //각각의 view에 데이터 저장
                        for (i in 0..selectListFri!!.size - 1) {
                            var start = selectListFri!!.get(i).starttime!!
                            var end = selectListFri!!.get(i).endtime!!
                            for (j in start!!..end!!) {
                                val index = j - 9
                                var textViewId = scheduleList.get(index)
                                var text = findViewById<TextView>(textViewId)
                                text.text = selectListFri!!.get(i).subjectname
                                text.setBackgroundColor(Color.parseColor(selectListFri!!.get(i).color.toString()))
                            }
                        }
                    }

                })
                var dialog = builder.create()
                dialog.show()
            }

            else -> return
        }


    }

    companion object {


    }

}
