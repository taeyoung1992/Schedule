package com.gmail.ansxodud238.schedule.data

data class Subject(var subjectid : Int? = null,
                   var subjectname : String = "",
                   var starttime : Int? = null,
                   var endtime : Int? = null,
                   var wday : Int? = null,
                   var color : String =""){
    override fun toString(): String {
        val result = "과목명:${subjectname}/시간:${starttime}~${endtime}"
        return result
    }
}
