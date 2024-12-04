package com.lion.team7_shopping_mall.repository

import android.content.Context
import android.util.Log
import com.lion.team7_shopping_mall.database.historydatabase.ClothesInOutHistoryDatabase
import com.lion.team7_shopping_mall.database.historydatabase.ClothesInOutHistoryVO
import com.lion.team7_shopping_mall.viewmodel.ClothesInOutHistoryViewModel

class ClothesInOutHistoryRepository {
    companion object {

        // 옷 정보를 저장하는 메서드
        fun insertClothesInOutHistoryInfo(context: Context, clothesInOutHistoryViewModel: ClothesInOutHistoryViewModel){
            // 데이터베이스 객체를 가져온다.
            val clothesInOutHistoryDatabase = ClothesInOutHistoryDatabase.getInstance(context)
            // ViewModel에 있는 데이터를 VO에 담아준다.
            val clothesInOutHistoryVO = ClothesInOutHistoryVO(
                clothesInOutHistoryName = clothesInOutHistoryViewModel.clothesInOutHistoryName,
                clothesInOutHistoryCheckInOut = clothesInOutHistoryViewModel.clothesInOutHistoryCheckInOut,
                clothesInOutHistoryCount = clothesInOutHistoryViewModel.clothesInOutHistoryCount,
                clothesInOutHistoryPrice = clothesInOutHistoryViewModel.clothesInOutHistoryPrice,
                clothesInOutHistoryYear = clothesInOutHistoryViewModel.clothesInOutHistoryYear,
                clothesInOutHistoryMonth = clothesInOutHistoryViewModel.clothesInOutHistoryMonth,
                clothesInOutHistoryDate = clothesInOutHistoryViewModel.clothesInOutHistoryDate,
                clothesInOutHistoryHour = clothesInOutHistoryViewModel.clothesInOutHistoryHour,
                clothesInOutHistoryMinute = clothesInOutHistoryViewModel.clothesInOutHistoryMinute,
                clothesInOutHistorySecond = clothesInOutHistoryViewModel.clothesInOutHistorySecond
            )

            clothesInOutHistoryDatabase?.clothesInOutHistoryDAO()?.insertClothesInOutHistoryData(clothesInOutHistoryVO)
        }

        // 이름에 맞는 옷 입출력 데이터를 가져오는 메서드
        fun selectClothesInOutHistoryByName(context: Context, clothesInOutHistoryName:String) : MutableList<ClothesInOutHistoryViewModel>{
            val clothesInOutHistoryDataBase = ClothesInOutHistoryDatabase.getInstance(context)
            // 이름에 맞는 옷 입출력 데이터를 가져온다.
            val clothesInOutHistoryList = clothesInOutHistoryDataBase?.clothesInOutHistoryDAO()?.selectClothesInOutHistoryByName(clothesInOutHistoryName)

            val tempList = mutableListOf<ClothesInOutHistoryViewModel>()

            clothesInOutHistoryList?.forEach {
                val clothesInOutHistoryViewModel = ClothesInOutHistoryViewModel(
                    it.clothesInOutHistoryIdx, it.clothesInOutHistoryName, it.clothesInOutHistoryCheckInOut, it.clothesInOutHistoryCount ,
                    it.clothesInOutHistoryPrice, it.clothesInOutHistoryYear, it.clothesInOutHistoryMonth,
                    it.clothesInOutHistoryDate, it.clothesInOutHistoryHour, it.clothesInOutHistoryMinute,
                    it.clothesInOutHistorySecond
                )
                // 리스트에 담는다.
                tempList.add(clothesInOutHistoryViewModel)
            }

            return tempList
        }

        // 옷 입출력 데이터 전체를  가져오는 메서드
        fun selectClothesInOutHistoryDataAll(context: Context) : MutableList<ClothesInOutHistoryViewModel>{
            // 데이터를 가져온다.
            val clothesInOutHistoryDataBase = ClothesInOutHistoryDatabase.getInstance(context)
            val clothesInOutHistoryList = clothesInOutHistoryDataBase?.clothesInOutHistoryDAO()?.selectClothesInOutHistoryDataAll()

            // 옷 입출력 데이터를 담을 리스트
            val tempList = mutableListOf<ClothesInOutHistoryViewModel>()

            // 학생의 수 만큼 반복한다.
            clothesInOutHistoryList?.forEach {
                val clothesInOutHistoryViewModel = ClothesInOutHistoryViewModel(
                    it.clothesInOutHistoryIdx, it.clothesInOutHistoryName, it.clothesInOutHistoryCheckInOut, it.clothesInOutHistoryCount ,
                    it.clothesInOutHistoryPrice, it.clothesInOutHistoryYear, it.clothesInOutHistoryMonth,
                    it.clothesInOutHistoryDate, it.clothesInOutHistoryHour, it.clothesInOutHistoryMinute,
                    it.clothesInOutHistorySecond
                )
                // 리스트에 담는다.
                tempList.add(clothesInOutHistoryViewModel)
            }

            return tempList
        }

        fun deleteClothesInOutHistoryByName(context: Context, clothesInOutHistoryName: String) {
            val clothesInOutHistoryDatabase = ClothesInOutHistoryDatabase.getInstance(context)
            clothesInOutHistoryDatabase?.clothesInOutHistoryDAO()?.deleteClothesInOutHistoryByName(clothesInOutHistoryName)
        }

        // 특정 이름을 변경하는 메서드
        fun modifyClothesInOutName(context: Context, clothesName: String, modifyName: String) {
            val clothesInOutHistoryDatabase = ClothesInOutHistoryDatabase.getInstance(context)
            clothesInOutHistoryDatabase?.clothesInOutHistoryDAO()?.updateClothesInOutHistoryName(clothesName, modifyName)
        }


    }
}