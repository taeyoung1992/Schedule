package com.gmail.ansxodud238.schedule

import android.text.LoginFilter
import retrofit2.Call
import com.gmail.ansxodud238.schedule.data.Subject
import com.gmail.ansxodud238.schedule.data.User
import retrofit2.http.*
import java.util.*
import kotlin.collections.ArrayList

//유저 회원 가입
data class UserResponse(var result : Int? = null)
data class UserInfo(var username : String = "",var userpw : String = "")
data class UserNameCheck(var username : String = "")
data class UserLoginResult(var result: Int? = null, var user: User? = null)
data class UserDataSend(var subjectid : Int? = null, var userid: Int? = null)

interface Service {

    //모든 과목 불러오기
    @GET("api/subject")
    fun getSubejctData() : Call<ArrayList<Subject>>


    //유저회원가입 결과
    @POST("api/user")
    fun addUser(@Body userInfo: UserInfo) : Call<UserResponse>

    //유저아이디 중복검사
    @POST("api/user/check")
    fun userNameCheck(@Body userNameCheck: UserNameCheck) : Call<UserResponse>

    //유저 로그인
    @POST("api/user/login")
    fun userLogin(@Body user: UserInfo) : Call<UserLoginResult>

    @GET("api/schedule/{userid}")
    fun userHasSchedule(@Path("userid")userid : Int) : Call<ArrayList<Subject>>

    @POST("api/schedule")
    fun userData(@Body schedulelist:  ArrayList<UserDataSend>) : Call<UserResponse>






}