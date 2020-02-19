package com.gmail.ansxodud238.schedule

import android.text.LoginFilter
import retrofit2.Call
import com.gmail.ansxodud238.schedule.data.Subject
import retrofit2.http.*

//유저 회원 가입
data class UserResponse(var result : Int? = null)
data class UserInfo(var username : String = "",var userpw : String = "")
data class UserNameCheck(var username : String = "")

interface Service {

    //모든 과목 불러오기
    @GET("/api/subject")
    fun getSubejctData() : Call<ArrayList<Subject>>


    //유저회원가입 결과
    @POST("/api/user")
    fun addUser(@Body userInfo: UserInfo) : Call<UserResponse>

    @POST("/api/user/check")
    fun userNameCheck(@Body userNameCheck: UserNameCheck) : Call<UserResponse>

    @POST("/api/user/login")
    fun userLogin(@Body userInfo: UserInfo) : Call<UserResponse>



}