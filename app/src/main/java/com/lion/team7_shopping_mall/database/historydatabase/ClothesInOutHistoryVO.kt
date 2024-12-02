package com.lion.team7_shopping_mall.database.historydatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ClothesInOutHistoryTable")
data class ClothesInOutHistoryVO(
    @PrimaryKey(autoGenerate = true)
    var clothesInOutHistoryIdx:Int = 0,
    var clothesInOutHistoryName:String = "",
    var clothesInOutHistoryCheckInOut:String = "",
    var clothesInOutHistoryCount:Int = 0,
    var clothesInOutHistoryPrice:Int = 0,
    // 입출고 내역을 저장할 때의 날짜와 시간을 담는 변수
    var clothesInOutHistoryYear:Int = 0,
    var clothesInOutHistoryMonth:Int = 0,
    var clothesInOutHistoryDate:Int = 0,
    var clothesInOutHistoryHour:Int = 0,
    var clothesInOutHistoryMinute:Int = 0,
    var clothesInOutHistorySecond:Int = 0,
)