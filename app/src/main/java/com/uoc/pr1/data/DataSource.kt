package com.uoc.pr1.data

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.uoc.pr1.data.model.Seminary
import com.uoc.pr1.data.model.Item
import com.uoc.pr1.data.model.User

class DataSourceException(message: String) : Exception(message) {

}


open class  DataSource {

    protected lateinit var ItemsLiveData: MutableLiveData<MutableList<Item>>
    protected lateinit var seminarsLiveData: MutableLiveData<MutableList<Seminary>>

    open fun login(username: String, password: String): Result<User>?{
        return null
    }
    open fun logout(){}

    // *********************

    open fun selectSeminarssUser(user_id:Int){

    }

    fun getSeminars():MutableLiveData<MutableList<Seminary>>? {

        return seminarsLiveData
    }

    open fun addSeminary(name:String, uri: Uri?) {}


    // *******************
    open fun addItem(title:String, descripton:String,uri: Uri?){}
    open fun addItem(Item: Item) {}
    open fun removeItem(Item: Item){}
    open fun getItemForId(id: Long): Item?{
        return null
    }

    fun getCorrects(answers:Array<Int>):Int
    {
        var result:Int = 0
        var pos:Int = 0
        for (item: Item in ItemsLiveData?.value!!) {
            if(item.correct_answer==answers[pos]){
                result++;

            }
            pos++;
        }


        return result
    }

    fun getItemPos(pos:Int):Item
    {
        return ItemsLiveData?.value!![pos]
    }

    fun getCountItems():Int
    {
        return ItemsLiveData?.value!!.count()
    }

    fun getItemList():MutableLiveData<MutableList<Item>>?{
        return ItemsLiveData
    }


    open fun selectItemsSeminary(seminary_id:Int){

    }

    public fun seminarRecommendation(duration:Int, skill: String, experience:Boolean):String
    {


        if(duration<20){
            return "beginner"
        }
        else if(duration>20){
            if(skill.equals("programming")){
                return "advanced"
            }
            else{

                return "beginner"
            }
        }
        else{
            if(duration>50){
                if(!experience)
                    return "intermediate"
                else
                    return "advanced"
            }
            else{
                return "beginner"
            }

        }
    }

    companion object DataSourceFactory{

        enum class DataSourceType {
            Hardcode, Room, Remote, Local,
        }

        //BEGIN-CODE-UOC-4
        val Default = DataSourceType.Local
        /*
        (a) This is possible due to polymorphism. Both DataSourceLocal and DataSourceHardcode
            are subclasses of the parent class DataSource, so they can be safely returned
            wherever an object of the parent type DataSource is expected.

        (b) No, it would not affect the user interface. The UI interacts with the generic
            DataSource parent class (via the Factory pattern), so the underlying implementation
            details (how data is fetched) are completely isolated from the UI layer.

        (c) Another local DataSource kind we could add is Room (a modern SQLite object-mapping
            library by Android) or SharedPreferences for simpler key-value pair data.
        */
        //BEGIN-END-UOC-4

        private var INSTANCE: DataSource? = null

        fun getDataSource(type:DataSourceType, context: Context? = null): DataSource {
            return synchronized(DataSource::class) {

                if(type==DataSourceType.Hardcode) {
                    val newInstance = INSTANCE ?: DataSourceHardcode()
                    INSTANCE = newInstance
                    newInstance
                }
                else if(type==DataSourceType.Local) {
                    val newInstance = INSTANCE ?: DataSourceLocal(context)
                    INSTANCE = newInstance
                    newInstance
                }
                else{
                    val newInstance = INSTANCE ?: DataSourceHardcode()
                    INSTANCE = newInstance
                    newInstance
                }
            }
        }
    }

}