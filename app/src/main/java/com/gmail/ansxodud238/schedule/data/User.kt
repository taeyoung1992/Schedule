package com.gmail.ansxodud238.schedule.data

import java.util.*
import kotlin.collections.HashMap

data class User(var userId : Int? = null, var userName : String = "", var userPw : String = ""){

    override fun toString(): String {
        //return super.toString()
        val result = "${userId},${userName},${userPw}"
        return result
    }

    fun requestPut(parameters : HashMap<String, Object>){
        this.userId = parameters.get("userId") as Int
        this.userName = parameters.get("userName") as String
        this.userPw = parameters.get("userPw") as String
    }
}