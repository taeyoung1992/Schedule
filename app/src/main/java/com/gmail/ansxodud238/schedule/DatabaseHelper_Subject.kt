package com.gmail.ansxodud238.schedule

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.gmail.ansxodud238.schedule.data.Subject
import com.gmail.ansxodud238.schedule.data.User

class DatabaseHelper_Subject (context : Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION ){


    private val CREATE_TABLE = "create table ${TABLE_SUBJECT}(" +
            "${COLUMN_SUBJECT_ID} INTEGER primary key AUTOINCREMENT," +
            "${COLUMN_SUBJECT_NAME} text," +
            "${COLUMN_STARTTIME} INTEGER," +
            "${COLUMN_ENDTIME} INTEGER," +
            "${COLUMN_WDAY} INTEGER," +
            "${COLUMN_COLOR} text)"

    private val DROP_USER_TABLE = "drop table if exists ${TABLE_SUBJECT}"


    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DROP_USER_TABLE)
        onCreate(db)
    }

    fun getAllSubject() : ArrayList<Subject>{
        val subjectList = ArrayList<Subject>()//불러온 모든 데이터를 저장할 리스트 생성

        val column = arrayOf(COLUMN_SUBJECT_ID, COLUMN_SUBJECT_NAME, COLUMN_STARTTIME,
                                             COLUMN_ENDTIME, COLUMN_WDAY, COLUMN_COLOR)//컬럼 이름 저장

        val db = this.writableDatabase//데이터베이스 쓰기 시작
        val order = "${COLUMN_SUBJECT_NAME} asc" //이름 순으로 정렬

        val cursor = db.query(TABLE_SUBJECT,column,null,null,null,null,order) // 데이터베이스 쿼리문 작성

        if(cursor.moveToFirst()){
            do{
                val subject = Subject(cursor.getInt(cursor.getColumnIndex(COLUMN_SUBJECT_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_SUBJECT_NAME)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_STARTTIME)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ENDTIME)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_WDAY)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_COLOR)))
                subjectList.add(subject)
            }while (cursor.moveToNext())
        } // 데이터베이스 돌면서 모든 데이터 불러오기

        cursor.close()
        db.close()
        Log.d("database","list check")

        return subjectList
    }

    fun addSubject(subject : Subject){
        val db = this.writableDatabase // 데이터베이스 쓰기 시작

        val value = ContentValues() // 데이터에 저장할 값을 data클래스에서 받아오기
        value.put(COLUMN_SUBJECT_ID,subject.subjectid)
        value.put(COLUMN_SUBJECT_NAME,subject.subjectname)
        value.put(COLUMN_STARTTIME,subject.starttime)
        value.put(COLUMN_ENDTIME,subject.endtime)
        value.put(COLUMN_WDAY,subject.wday)
        value.put(COLUMN_COLOR,subject.color)



        db.insert(TABLE_SUBJECT,null,value)//테이블에 값 추가
        db.close()//데이터베이스 종료
        Log.d("database","add check")
    }

    fun deleteSubject(subject: Subject) {

        val db = this.writableDatabase
        // delete user record by id
        db.delete(
            TABLE_SUBJECT, "$COLUMN_SUBJECT_NAME = ?",
            arrayOf(subject.subjectname))
        db.close()

    }

    fun getWDay(day : Int):ArrayList<Subject>{
        val subjectList = ArrayList<Subject>()

        val column = arrayOf(COLUMN_SUBJECT_ID, COLUMN_SUBJECT_NAME, COLUMN_STARTTIME,
            COLUMN_ENDTIME, COLUMN_WDAY, COLUMN_COLOR)

        val db = this.writableDatabase
        val selection = "${COLUMN_WDAY} = ?"
        val selectionArgs = arrayOf(day.toString())
        val order = "${COLUMN_SUBJECT_NAME} asc"

        val cursor = db.query(TABLE_SUBJECT,column,selection,selectionArgs,null,null,order)

        if(cursor.moveToFirst()){
            do{
                val subject = Subject(cursor.getInt(cursor.getColumnIndex(COLUMN_SUBJECT_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_SUBJECT_NAME)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_STARTTIME)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ENDTIME)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_WDAY)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_COLOR)))
                subjectList.add(subject)
            }while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        Log.d("database","getWDayList check")

        return subjectList
    }

    fun checkSubjectId(id : Int) : Boolean{
        val db = this.writableDatabase
        val column = arrayOf(COLUMN_SUBJECT_ID)
        val selection = "${COLUMN_SUBJECT_ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        val cursor = db.query(TABLE_SUBJECT,column,selection,selectionArgs,null,null,null)

        val cursorCount = cursor.count
        if(cursorCount > 0) return false

        return true
    }




    companion object{
        private val DATABASE_NAME = "subject.db"
        private val DATABASE_VERSION = 5

        private val TABLE_SUBJECT = "table_subject"

        private val COLUMN_SUBJECT_ID = "subjcet_id"
        private val COLUMN_SUBJECT_NAME = "subjcet_name"
        private val COLUMN_STARTTIME = "starttime"
        private val COLUMN_ENDTIME = "endtime"
        private val COLUMN_WDAY = "wday"
        private val COLUMN_COLOR = "color"
    }
}