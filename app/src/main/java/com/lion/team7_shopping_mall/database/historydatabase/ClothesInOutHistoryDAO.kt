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
        ORDER BY clothesInOutHistoryIdx desc
    """)
    fun selectClothesInOutHistoryByName(clothesInOutHistoryName: String): List<ClothesInOutHistoryVO>

    // 옷 입춝도 내역을 가져오는 메서드
    @Query("""
        select * from ClothesInOutHistoryTable
        order by clothesInOutHistoryIdx desc""")
    fun selectClothesInOutHistoryDataAll() : List<ClothesInOutHistoryVO>

    // 특정 이름으로 데이터를 삭제하는 메서드
    @Query("""
    DELETE FROM ClothesInOutHistoryTable
    WHERE clothesInOutHistoryName = :clothesInOutHistoryName
    """)
    fun deleteClothesInOutHistoryByName(clothesInOutHistoryName: String)

    // 특정 이름을 수정하는 메서드
    @Query("""
    UPDATE ClothesInOutHistoryTable
    SET clothesInOutHistoryName = :modifyName
    WHERE clothesInOutHistoryName = :clothesInOutHistoryName
    """)
    fun updateClothesInOutHistoryName(clothesInOutHistoryName: String, modifyName: String)

}