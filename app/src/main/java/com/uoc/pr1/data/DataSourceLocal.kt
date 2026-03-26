package com.uoc.pr1.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.uoc.data.localstorage.DbHelper
import com.uoc.pr1.R
import com.uoc.pr1.data.model.*
import java.io.FileOutputStream
import java.io.InputStream


class DataSourceLocal:  DataSource {

    private lateinit var dbHelper: DbHelper
    private lateinit  var db: SQLiteDatabase
    private lateinit  var db_read: SQLiteDatabase

    private val seminarItemList = mutableListOf<Item>()

    private val userSeminarList = mutableListOf<Seminary>()

    private lateinit  var context: Context

    private var _user_id = 0

    constructor(context: Context?) : super() {
        seminarsLiveData = MutableLiveData(userSeminarList)
        ItemsLiveData = MutableLiveData(seminarItemList)

        dbHelper = DbHelper(context!!)
        db = dbHelper.writableDatabase
        db_read = dbHelper.readableDatabase

        this.context =  context
    }

    override fun login(username: String, password: String): Result<User>? {

        lateinit var result:Result<User>


        //BEGIN-CODE-UOC-3.1

        var user_harcoded:User  = User(1, "Jane Doe")
        result =  Result.Success(user_harcoded)

        val str_sql = "SELECT * FROM user WHERE user_username = '$username' and  user_pwd='$password'"







        // END-CODE-UOC-3.1

        return result

    }

    override fun logout() {
        _user_id = 0
    }

    // ****************************************************************
    // Seminars
    override open fun selectSeminarssUser(user_id: Int) {
        userSeminarList.clear()
        // Load user's seminars
        //BEGIN-CODE-UOC-3.2
        val str_sql = "SELECT * FROM seminar,user_seminar WHERE  usersem_user_id= $user_id and usersem_seminar_id = sem_id"





        // END-CODE-UOC-3.2
    }

    // ****************************************************************
    // Items
    override fun selectItemsSeminary(id:Int){

        seminarItemList.clear()


        // Load seminar items
        //BEGIN-CODE-UOC-3.3

        val str_sql = "SELECT * FROM items WHERE item_sem_id= $id"



        // END-CODE-UOC-3.3



        ItemsLiveData.postValue(seminarItemList)
    }


    fun getNewId():Int
    {

        val str_sql = "SELECT max(sem_id)+1 as 'new_sem_id' FROM seminar"

        val cursor2 = db_read.rawQuery(
            str_sql
            , null)

        var new_sem_id = -1

        with(cursor2) {
            while (moveToNext()) {

                new_sem_id = getInt(getColumnIndexOrThrow("new_sem_id"))

            }

        }
        cursor2.close()

        return new_sem_id
    }

    // The method must force the seminar duration to be 60 and the sem level to be a 'beginner.'
    override fun addSeminary(name:String, uri:Uri?) {

        val new_id:Int = getNewId()

        var name_sql = name.replace("'","''");

        var str_sql = "INSERT INTO 'main'.'seminar' ('sem_id', 'sem_name', 'sem_duration', 'sem_level') VALUES ($new_id, '$name_sql', 60, '')"


        //BEGIN-CODE-UOC-6.6.1


        //END-CODE-UOC-6.6.1

        str_sql = "INSERT INTO 'main'.'user_seminar' ('usersem_user_id', 'usersem_seminar_id') VALUES ($_user_id, $new_id)"

        db.execSQL(str_sql)


        var image_path:String = ""

        //BEGIN-CODE-UOC-6.6.2


        //END-CODE-UOC-6.6.2

        val seminary:Seminary = Seminary(new_id,name_sql,60,"beginner" ,image_path, null, null)


        //BEGIN-CODE-UOC-6.6.3

        val currentList = seminarsLiveData.value
        if (currentList == null) {
            seminarsLiveData.postValue(mutableListOf(seminary))
        } else {
            val updatedList = currentList.toMutableList()
            updatedList.add(0, seminary)
            seminarsLiveData.postValue(updatedList)
        }

        //END-CODE-UOC-6.6.3
    }





}