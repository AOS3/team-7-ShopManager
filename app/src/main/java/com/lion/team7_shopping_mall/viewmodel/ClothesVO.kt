package com.lion.team7_shopping_mall.viewmodel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ClothesTable")
data class ClothesVO(
    @PrimaryKey(autoGenerate = true)
    var clothesIdx:Int = 0,
    var clothesPicture:String = "",
    var clothesName:String = "",
    var clothesPrice:Int = 0,
    var clothesInventory:Int = 0,
    var clothesColor:String = "",
    var clothesSize:String = "",
    var clothesCategory:String = "",
    var clothesTypeByCategory:String = ""
)
