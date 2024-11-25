package com.lion.team7_shopping_mall.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ClothesVO::class], version = 1, exportSchema = true)
abstract class ClothesDatabase : RoomDatabase(){
    abstract fun clothesDAO() : ClothesDAO

    companion object{
        var clothesDatabase:ClothesDatabase? = null
        @Synchronized
        fun getInstance(context: Context) : ClothesDatabase?{
            synchronized(ClothesDatabase::class){
                clothesDatabase = Room.databaseBuilder(
                    context.applicationContext, ClothesDatabase::class.java,
                    "Clothes.db"
                ).build()
            }
            return clothesDatabase
        }

        fun destroyInstance(){
            clothesDatabase = null
        }
    }
}