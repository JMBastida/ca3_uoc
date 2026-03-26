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

        val str_sql = "SELECT * FROM user WHERE user_username = '$username' and  user_pwd='$password'"

        val cursor = db_read.rawQuery(str_sql, null)

        if (cursor.moveToFirst()) {
            val userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"))
            val displayName = cursor.getString(cursor.getColumnIndexOrThrow("user_display_name"))
            val user = User(userId, displayName)

            _user_id = userId // Store the logged-in user ID
            result = Result.Success(user)
        } else {
            result = Result.Error(Exception("Invalid credentials"))
        }

        cursor.close()

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

        val cursor = db_read.rawQuery(str_sql, null)

        with(cursor) {
            while (moveToNext()) {
                val semId = getInt(getColumnIndexOrThrow("sem_id"))
                val semName = getString(getColumnIndexOrThrow("sem_name"))
                val semDuration = getInt(getColumnIndexOrThrow("sem_duration"))
                val semLevel = getString(getColumnIndexOrThrow("sem_level"))

                // Construct the local image path
                val imagePath = context.filesDir.path + "/media/seminar/" + semId + ".jpg"

                val seminar = Seminary(
                    id = semId,
                    name = semName,
                    duration = semDuration,
                    level = semLevel,
                    image_path = imagePath,
                    image = null,
                    view = null
                )
                userSeminarList.add(seminar)
            }
        }
        cursor.close()

        // Let the LiveData know about the change (optional depending on base class but highly recommended)
        seminarsLiveData.postValue(userSeminarList)
        // END-CODE-UOC-3.2
    }

    // ****************************************************************
    // Items
    override fun selectItemsSeminary(id:Int){

        seminarItemList.clear()


        // Load seminar items
        //BEGIN-CODE-UOC-3.3
        val str_sql = "SELECT * FROM items WHERE item_sem_id= $id"

        val cursor = db_read.rawQuery(str_sql, null)

        with(cursor) {
            while (moveToNext()) {
                val itemId = getInt(getColumnIndexOrThrow("item_id"))
                val itemTypeInt = getInt(getColumnIndexOrThrow("item_type"))
                val itemQuestion = getString(getColumnIndexOrThrow("item_question"))
                val itemLink = getString(getColumnIndexOrThrow("item_link"))

                // Read the newly added answer columns from Task 1
                val itemCorrectAnswer = getInt(getColumnIndexOrThrow("item_correct_answer"))
                val itemAnswer1 = getString(getColumnIndexOrThrow("item_answer1"))
                val itemAnswer2 = getString(getColumnIndexOrThrow("item_answer2"))
                val itemAnswer3 = getString(getColumnIndexOrThrow("item_answer3"))
                val itemAnswer4 = getString(getColumnIndexOrThrow("item_answer4"))

                // Match integer from database to the Enum
                val itemType = ItemType.values().find { it.v1 == itemTypeInt } ?: ItemType.BASIC

                val item = Item(
                    type = itemType,
                    id = itemId,
                    question = itemQuestion,
                    link = itemLink,
                    correct_answer = itemCorrectAnswer,
                    answer1 = itemAnswer1,
                    answer2 = itemAnswer2,
                    answer3 = itemAnswer3,
                    answer4 = itemAnswer4
                )
                seminarItemList.add(item)
            }
        }
        cursor.close()
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
        db.execSQL(str_sql)

        //END-CODE-UOC-6.6.1

        str_sql = "INSERT INTO 'main'.'user_seminar' ('usersem_user_id', 'usersem_seminar_id') VALUES ($_user_id, $new_id)"

        db.execSQL(str_sql)


        var image_path:String = ""

        //BEGIN-CODE-UOC-6.6.2
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val directory_path = context.filesDir.path + "/media/seminar/"
                image_path = directory_path + new_id + ".jpg"

                val outputStream = java.io.FileOutputStream(image_path)
                inputStream?.copyTo(outputStream)

                inputStream?.close()
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        //END-CODE-UOC-6.6.2

        val seminary:Seminary = Seminary(new_id,name_sql,60,"beginner" ,image_path, null, null)


        //BEGIN-CODE-UOC-6.6.3
        /*
                This snippet updates the LiveData that holds our list of seminars.
                Instead of querying the entire database again, it takes the current list
                being displayed, adds the newly created seminar to the very beginning
                (index 0), and posts the updated list to the LiveData. This triggers
                the UI (RecyclerView) to immediately display the newly added seminar.
                */
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