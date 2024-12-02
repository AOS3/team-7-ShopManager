package com.lion.team7_shopping_mall.database.historydatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.lion.team7_shopping_mall.database.ClothesVO

@Dao
interface ClothesInOutHistoryDAO {

    // 옷 입출고 내역 저장
    @Insert
    fun insertClothesInOutHistoryData(clothesInOutHistoryVO: ClothesInOutHistoryVO)

    // 특정 이름으로 데이터를 가져오는 메서드
    @Query("""
        SELECT * FROM ClothesInOutHistoryTable
        WHERE clothesInOutHistoryName = :clothesInOutHistoryName
    """)
    fun selectClothesInOutHistoryByName(clothesInOutHistoryName: String): List<ClothesInOutHistoryVO>

    // 옷 입춝도 내역을 가져오는 메서드
    @Query("""
        select * from ClothesInOutHistoryTable
        order by clothesInOutHistoryIdx asc""")
    fun selectClothesInOutHistoryDataAll() : List<ClothesInOutHistoryVO>

}