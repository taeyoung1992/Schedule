package com.gmail.ansxodud238.schedule

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.gmail.ansxodud238.schedule.data.User

class DatabaseHelper_User(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION ){

    private lateinit var activity :MainActivity

    private val CREATE_TABLE = "create table ${TABLE_USER}(" +
            "${COLUMN_USER_ID} INTEGER primary key AUTOINCREMENT," +
            "${COLUMN_USER_NAME} text," +
            "${COLUMN_USER_PASSWORD} text)"

    private val DROP_USER_TABLE = "drop table if exists ${TABLE_USER}"


    override fun onCreate(db: SQLiteDatabase?) {
        if(db!=null) {
            db?.execSQL(CREATE_TABLE)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DROP_USER_TABLE)
        onCreate(db)
    }

    fun getAllUser() : ArrayList<User>{
        val userList = ArrayList<User>()//불러온 모든 데이터를 저장할 리스트 생성

        val column = arrayOf(COLUMN_USER_ID,COLUMN_USER_NAME, COLUMN_USER_PASSWORD )//컬럼 이름 저장

        val db = this.writableDatabase//데이터베이스 쓰기 시작
        val order = "${COLUMN_USER_NAME} asc" //이름 순으로 정렬

        val cursor = db.query(TABLE_USER,column,null,null,null,null,order) // 데이터베이스 쿼리문 작성

        if(cursor.moveToFirst()){
            do{
                val user = User(cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)))
                userList.add(user)
            }while (cursor.moveToNext())
        } // 데이터베이스 돌면서 모든 데이터 불러오기

        cursor.close()
        db.close()
        Log.d("database","list check")

        return userList
    }

    fun addUser(user : User){
        val db = this.writableDatabase // 데이터베이스 쓰기 시작

        val value = ContentValues() // 데이터에 저장할 값을 data클래스에서 받아오기
        value.put(COLUMN_USER_ID,user.userid)
        value.put(COLUMN_USER_NAME,user.username)
        value.put(COLUMN_USER_PASSWORD,user.userpw)

        db.insert(TABLE_USER,null,value)//테이블에 값 추가
        db.close()//데이터베이스 종료
        Log.d("database","add check")
    }

    fun updateUser(user: User) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_USER_ID, user.userid)
        values.put(COLUMN_USER_NAME, user.username)
        values.put(COLUMN_USER_PASSWORD, user.userpw)

        // updating row
        db.update(TABLE_USER, values, "$COLUMN_USER_NAME = ?",
            arrayOf(user.username.toString()))
        db.close()
    }

    fun deleteUser(user: User) {

        val db = this.writableDatabase
        // delete user record by id
        db.delete(TABLE_USER, "$COLUMN_USER_NAME = ?",
            arrayOf(user.username.toString()))
        db.close()


    }

    fun checkUser(name: String, password: String): Boolean {

        val columns = arrayOf(COLUMN_USER_NAME)

        val db = this.readableDatabase

        val selection = "$COLUMN_USER_NAME = ? AND $COLUMN_USER_PASSWORD = ?"

        val selectionArgs = arrayOf(name, password)

        val cursor = db.query(TABLE_USER, //Table to query
            columns, //columns to return
            selection, //columns for the WHERE clause
            selectionArgs, //The values for the WHERE clause
            null,  //group the rows
            null, //filter by row groups
            null) //The sort order

        val cursorCount = cursor.count
        cursor.close()
        db.close()

        if (cursorCount > 0)
            return true

        return false

    }

    fun checkUserId(id : Int) : Boolean{
        val db = this.writableDatabase

        val column = arrayOf(COLUMN_USER_ID)
        val selection = "${COLUMN_USER_ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        val cursor = db.query(TABLE_USER,column,selection,selectionArgs,null,null,null)
        val cursorCount = cursor.count
        if(cursorCount > 0) return false

        return true
    }

    companion object{
        private val DATABASE_NAME = "user.db"
        private val DATABASE_VERSION = 5

        private val TABLE_USER = "table_user"

        private val COLUMN_USER_ID = "user_id"
        private val COLUMN_USER_PASSWORD = "user_password"
        private val COLUMN_USER_NAME = "user_name"





    }


}