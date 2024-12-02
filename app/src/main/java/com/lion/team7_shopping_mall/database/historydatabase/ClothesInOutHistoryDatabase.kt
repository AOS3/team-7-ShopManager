package com.lion.team7_shopping_mall.database.historydatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lion.team7_shopping_mall.database.ClothesDAO
import com.lion.team7_shopping_mall.database.ClothesVO

@Database(entities = [ClothesInOutHistoryVO::class], version = 1, exportSchema = true)
abstract class ClothesInOutHistoryDatabase : RoomDatabase() {
    abstract fun clothesInOutHistoryDAO(): ClothesInOutHistoryDAO

    companion object{
        var clothesInOutHistoryDatabase:ClothesInOutHistoryDatabase? = null
        @Synchronized
        fun getInstance(context: Context) : ClothesInOutHistoryDatabase?{
            synchronized(ClothesInOutHistoryDatabase::class){
                clothesInOutHistoryDatabase = Room.databaseBuilder(
                    context.applicationContext, ClothesInOutHistoryDatabase::class.java,
                    "ClothesInOutHistoryDatabase.db"
                ).build()
            }
            return clothesInOutHistoryDatabase
        }

        fun destroyInstance(){
            clothesInOutHistoryDatabase = null
        }
    }
}