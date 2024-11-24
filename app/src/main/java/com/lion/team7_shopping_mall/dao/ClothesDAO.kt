package com.lion.team7_shopping_mall.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.lion.team7_shopping_mall.viewmodel.ClothesVO

@Dao
interface ClothesDAO {

    // 옷 정보 저장
    @Insert
    fun insertClothesData(clothesVO: ClothesVO)

    // 옷 정보를 가져오는 메서드
    @Query("""
        select * from ClothesTable 
        order by clothesIdx desc""")
    fun selectClothesDataAll() : List<ClothesVO>

    // 옷 한개의 정보를 가져오는 메서드
    @Query("""
        select * from ClothesTable
        where clothesIdx = :clothesIdx
    """)
    fun selectClothesDataByClothesIdx(clothesIdx:Int) : ClothesVO

    // 옷 한개의 정보를 삭제하는 메서드
    @Delete
    fun deleteClothesData(clothesVO: ClothesVO)

    // 옷 한개의 정보를 수정하는 메서드
    @Update
    fun updateClothesData(clothesVO: ClothesVO)
}