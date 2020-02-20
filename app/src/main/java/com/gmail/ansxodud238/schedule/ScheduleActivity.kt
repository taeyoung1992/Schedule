package com.gmail.ansxodud238.schedule

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.gmail.ansxodud238.schedule.data.Subject
import kotlinx.android.synthetic.main.activity_schedule.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ScheduleActivity : AppCompatActivity(),View.OnClickListener {

    private var mySubjectList : ArrayList<Subject>? = null
    private var allSubjectList : ArrayList<Subject>? = null
    private lateinit var mondaySubjectList : ArrayList<Subject>
    private lateinit var tuesdaySubjectList : ArrayList<Subject>
    private lateinit var wednesdaySubjectList : ArrayList<Subject>
    private lateinit var thursdaySubjectList : ArrayList<Subject>
    private lateinit var fridaySubjectList : ArrayList<Subject>
    private lateinit var arrayAdapter: ArrayAdapter<Subject>
    private lateinit var dialogList : ArrayList<Subject>
    private var selectListMon : ArrayList<Subject>? = null



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

        //서버에서 내가 저장한 데이터 가녀오기
        service.userHasSchedule(userid).enqueue(object : Callback<ArrayList<Subject>> {
            override fun onFailure(call: Call<ArrayList<Subject>>, t: Throwable) {
                Toast.makeText(this@ScheduleActivity,"저장한 과목을 불러오는데에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<ArrayList<Subject>>,
                response: Response<ArrayList<Subject>>
            ) {
                mySubjectList = response.body()

            }
        })

        //서버에서 전체 데이터 가져오기
        service.getSubejctData().enqueue(object : Callback<ArrayList<Subject>>{
            override fun onFailure(call: Call<ArrayList<Subject>>, t: Throwable) {
                Toast.makeText(this@ScheduleActivity,"전체 과목 목록을 가져오는데에 실패했습니다.",Toast.LENGTH_SHORT).show()
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
        arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,dialogList)

        selectListMon = ArrayList()


        initButtonOnClickListener()

    }
    //요일 버튼에 이벤트 연결
    fun initButtonOnClickListener(){
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
        for (i in 0 .. allSubjectList!!.size-1){
            if(allSubjectList?.get(i)?.wday == 101){
                mondaySubjectList?.add(allSubjectList!!.get(i))
            }else if(allSubjectList?.get(i)?.wday == 102){
                tuesdaySubjectList?.add(allSubjectList!!.get(i))
            }else if(allSubjectList?.get(i)?.wday == 103){
                wednesdaySubjectList?.add(allSubjectList!!.get(i))
            }else if(allSubjectList?.get(i)?.wday == 104){
                thursdaySubjectList?.add(allSubjectList!!.get(i))
            }else if(allSubjectList?.get(i)?.wday == 105){
                fridaySubjectList?.add(allSubjectList!!.get(i))
            }else{
                Log.d("else","fail")
            }
        }

        when(getButtonid){
            R.id.btn_mon -> {
                //다이얼로그 생성 전에 이미 저장되어 있는 데이터 삭제
                dialogList.removeAll(dialogList)
                //다이얼로그에 저장할 데이터 저장
                for(i in 0..mondaySubjectList.size-1){
                    dialogList.add(mondaySubjectList.get(i))
                }
                //어댑터 리스트 갱신
                arrayAdapter.notifyDataSetChanged()
                //다이얼로그 생성
                val builder = AlertDialog.Builder(this)
                builder.setTitle("입력할 과목을 선택해 주세요.")
                //다이얼로그와 어댑터를 연결하고 과목 선택시 이벤트 발생
                builder.setAdapter(arrayAdapter,object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        if(selectListMon!!.size > 0){
                            for(index in 0..selectListMon!!.size-1){
                                var oldDataStartTime= selectListMon!!.get(index).starttime
                                var oldDataEndTime = selectListMon!!.get(index).endtime
                                var newDataStartTime = dialogList[which].starttime
                                var newDataEndTime = dialogList[which].endtime

                                if(newDataStartTime!! >= oldDataStartTime!! && newDataStartTime!! <= oldDataEndTime!!){
                                    Toast.makeText(this@ScheduleActivity,"중복된 시간을 선택하셨습니다.\n선택한 과목으로 변경합니다.",Toast.LENGTH_SHORT).show()
                                    selectListMon!!.removeAt(index)
                                    selectListMon!!.add(dialogList[which])
                                    Log.d("list","처음")
                                }else if(newDataEndTime!! >= oldDataStartTime!! && newDataEndTime!! <= oldDataEndTime!!){
                                    Toast.makeText(this@ScheduleActivity,"중복된 시간을 선택하셨습니다.\n선택한 과목으로 변경합니다.",Toast.LENGTH_SHORT).show()
                                    selectListMon!!.removeAt(index)
                                    selectListMon!!.add(dialogList[which])
                                    Log.d("list","중간")

                                }else{
                                    selectListMon!!.add(dialogList[which])
                                    Log.d("list","나중")

                                }
                            }
                        }else{
                            selectListMon!!.add(dialogList[which])
                        }

                        Log.d("list",selectListMon!!.size.toString())
                        //각각 view를 가져와서 데이터를 저장할 준비
                        val scheduleList = intArrayOf(
                            R.id.text1019, R.id.text10110, R.id.text10111,
                            R.id.text10112, R.id.text10113, R.id.text10114,
                            R.id.text10115, R.id.text10116, R.id.text10117,R.id.text10118
                        )
                        //데이터 초기화
                        for(delIndex in 0..scheduleList.size-1){
                            var text = findViewById<TextView>(scheduleList[delIndex])
                            text.text = ""
                        }
                        //각각의 view에 데이터 저장
                        for (i in 0..selectListMon!!.size-1) {
                            var start = selectListMon!!.get(i).starttime!!
                            var end = selectListMon!!.get(i).endtime!!
                            for (j in start!!..end!!) {
                                val index = j - 9
                                var textViewId = scheduleList.get(index)
                                var text = findViewById<TextView>(textViewId)
                                text.text = selectListMon!!.get(i).subjectname

                            }
                        }
                    }

                })
                var dialog = builder.create()
                dialog.show()
            }
            R.id.btn_tue -> Log.d("tue",tuesdaySubjectList!!.joinToString())
            R.id.btn_wed -> Log.d("wed",wednesdaySubjectList!!.joinToString())
            R.id.btn_thu -> Log.d("thu",thursdaySubjectList!!.joinToString())
            R.id.btn_fri -> Log.d("fri",fridaySubjectList!!.joinToString())
            else -> return
        }





    }
    companion object{



    }

}
