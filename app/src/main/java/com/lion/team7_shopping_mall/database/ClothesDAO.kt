package com.lion.team7_shopping_mall.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

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

    // 이름으로 검색하여 지정된 값과 같은 행만 가져오기
    @Query("""
        select * from ClothesTable
        where clothesName = :clothesName
        order by clothesIdx desc
    """)
    fun selectClothesDataAllByClothesName(clothesName:String) : List<ClothesVO>

    // 한 카테고리의 옷 정보를 가져오는 메서드
    @Query("""
        select * from ClothesTable
        where clothesCategory = :ClothesCategoryName
        order by clothesIdx desc
    """)
    fun selectClothesDataByCategory(ClothesCategoryName:String) : List<ClothesVO>

}