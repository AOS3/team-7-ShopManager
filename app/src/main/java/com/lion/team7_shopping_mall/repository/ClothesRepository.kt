package com.lion.team7_shopping_mall.repository

import android.content.Context
import android.util.Log
import com.lion.team7_shopping_mall.database.ClothesDatabase
import com.lion.team7_shopping_mall.database.ClothesVO
import com.lion.team7_shopping_mall.viewmodel.ClothesViewModel

class ClothesRepository {

    companion object {

        // 옷 정보를 저장하는 메서드
        fun insertClothesInfo(context: Context, clothesViewModel: ClothesViewModel){
            // 데이터베이스 객체를 가져온다.
            val clothesDatabase = ClothesDatabase.getInstance(context)
            // ViewModel에 있는 데이터를 VO에 담아준다.
            val clothesPicture = clothesViewModel.clothesPicture
            val clothesName = clothesViewModel.clothesName
            val clothesPrice = clothesViewModel.clothesPrice
            val clothesInventory = clothesViewModel.clothesInventory
            val clothesColor = clothesViewModel.clothesColor
            val clothesSize = clothesViewModel.clothesSize
            val clothesCategory = clothesViewModel.clothesCategory
            val clothesTypeByCategory = clothesViewModel.clothesTypeByCategory

            val clothesVO = ClothesVO(
                clothesPicture = clothesPicture,
                clothesName = clothesName,
                clothesPrice = clothesPrice,
                clothesInventory = clothesInventory,
                clothesColor = clothesColor,
                clothesSize = clothesSize,
                clothesCategory = clothesCategory,
                clothesTypeByCategory = clothesTypeByCategory
            )
            clothesDatabase?.clothesDAO()?.insertClothesData(clothesVO)
        }

        // 옷 정보 전체를 가져오는 메서드
        fun selectClothesInfoAll(context: Context) : MutableList<ClothesViewModel>{
            // 데이터 베이스 객체
            val clothesDatabase = ClothesDatabase.getInstance(context)
            // 옷 데이터 전체를 가져온다
            val clothesVoList = clothesDatabase?.clothesDAO()?.selectClothesDataAll()
            // 옷 데이터를 담을 리스트
            val clothesViewModelList = mutableListOf<ClothesViewModel>()
            // 옷의 수 만큼 반복한다.
            clothesVoList?.forEach {
                // 옷 데이터를 추출한다.
                val clothesIdx = it.clothesIdx
                val clothesPicture = it.clothesPicture
                val clothesName = it.clothesName
                val clothesPrice = it.clothesPrice
                val clothesInventory = it.clothesInventory
                val clothesColor = it.clothesColor
                val clothesSize = it.clothesSize
                val clothesCategory = it.clothesCategory
                val clothesTypeByCategory = it.clothesTypeByCategory

                // 객체에 담는다.
                val clothesViewModel = ClothesViewModel(
                    clothesIdx,
                    clothesPicture,
                    clothesName,
                    clothesPrice,
                    clothesInventory,
                    clothesColor,
                    clothesSize,
                    clothesCategory,
                    clothesTypeByCategory
                )

                // 리스트에 담는다.
                clothesViewModelList.add(clothesViewModel)
                Log.d("test100", "${clothesViewModel}")
            }
            return clothesViewModelList
        }

        // 옷 한개의 정보를 가져오는 메서드
        fun selectClothesInfoByClothesIdx(context: Context, clothesIdx:Int) : ClothesViewModel{
            val clothesDatabase = ClothesDatabase.getInstance(context)
            // 옷 한개의 정보를 가져온다.
            val clothesVO = clothesDatabase?.clothesDAO()?.selectClothesDataByClothesIdx(clothesIdx)
            // 옷 객체에 담는다
            val clothesPicture = clothesVO?.clothesPicture
            val clothesName = clothesVO?.clothesName
            val clothesPrice = clothesVO?.clothesPrice
            val clothesInventory = clothesVO?.clothesInventory
            val clothesColor = clothesVO?.clothesColor
            val clothesSize = clothesVO?.clothesSize
            val clothesCategory = clothesVO?.clothesCategory
            val clothesTypeByCategory = clothesVO?.clothesTypeByCategory

            val clothesViewModel = ClothesViewModel(
                clothesIdx,
                clothesPicture!!,
                clothesName!!,
                clothesPrice!!,
                clothesInventory!!,
                clothesColor!!,
                clothesSize!!,
                clothesCategory!!,
                clothesTypeByCategory!!
            )

            return clothesViewModel
        }

        // 옷 정보 삭제
        fun deleteClothesInfoByClothesIdx(context: Context, clothesIdx: Int){
            val clothesDatabase = ClothesDatabase.getInstance(context)
            // 삭제할 옷 번호를 담고 있을 객체를 생성한다.
            val clothesVO = ClothesVO(clothesIdx = clothesIdx)
            // 삭제한다
            clothesDatabase?.clothesDAO()?.deleteClothesData(clothesVO)
        }

        // 옷 정보를 수정하는 메서드
        fun updateClothesInfo(context: Context, clothesViewModel: ClothesViewModel){
            val clothesDatabase = ClothesDatabase.getInstance(context)
            // VO에 객체에 담아준다
            val clothesIdx = clothesViewModel.clothesIdx
            val clothesPicture = clothesViewModel.clothesPicture
            val clothesName = clothesViewModel.clothesName
            val clothesPrice = clothesViewModel.clothesPrice
            val clothesInventory = clothesViewModel.clothesInventory
            val clothesColor = clothesViewModel.clothesColor
            val clothesSize = clothesViewModel.clothesSize
            val clothesCategory = clothesViewModel.clothesCategory
            val clothesTypeByCategory = clothesViewModel.clothesTypeByCategory
            val clothesVO = ClothesVO(
                clothesIdx,
                clothesPicture,
                clothesName,
                clothesPrice,
                clothesInventory,
                clothesColor,
                clothesSize,
                clothesCategory,
                clothesTypeByCategory
            )
            // 수정한다.
            clothesDatabase?.clothesDAO()?.updateClothesData(clothesVO)
        }

        // 옷 이름으로 검색하여 옷 데이터 전체를 가져오는 메서드
        fun selectClothesDataAllByClothesName(context: Context, clothesName:String) : MutableList<ClothesViewModel>{
            // 데이터를 가져온다.
            val clothesDataBase = ClothesDatabase.getInstance(context)
            val clothesList = clothesDataBase?.clothesDAO()?.selectClothesDataAllByClothesName(clothesName)

            // 옷 데이터를 담을 리스트
            val tempList = mutableListOf<ClothesViewModel>()

            // 학생의 수 만큼 반복한다.
            clothesList?.forEach {
                val clothesModel = ClothesViewModel(
                    it.clothesIdx,
                    it.clothesPicture,
                    it.clothesName,
                    it.clothesPrice,
                    it.clothesInventory,
                    it.clothesColor,
                    it.clothesSize,
                    it.clothesCategory,
                    it.clothesTypeByCategory
                )
                // 리스트에 담는다.
                tempList.add(clothesModel)
            }
            return tempList
        }

        // 특정 카테고리의 정보를 가져오는 메서드
        fun selectClothesInfoByCategory(context: Context,clothesCategory:String) : MutableList<ClothesViewModel>{
            // 데이터 베이스 객체
            val clothesDatabase = ClothesDatabase.getInstance(context)
            //특정 카테고리의 옷 데이터 전체를 가져온다
            val clothesVoList = clothesDatabase?.clothesDAO()?.selectClothesDataByCategory(clothesCategory)
            // 옷 데이터를 담을 리스트
            val clothesViewModelList = mutableListOf<ClothesViewModel>()
            // 옷의 수 만큼 반복한다.
            clothesVoList?.forEach {
                // 옷 데이터를 추출한다.
                val clothesIdx = it.clothesIdx
                val clothesPicture = it.clothesPicture
                val clothesName = it.clothesName
                val clothesPrice = it.clothesPrice
                val clothesInventory = it.clothesInventory
                val clothesColor = it.clothesColor
                val clothesSize = it.clothesSize
                val clothesCategory = it.clothesCategory
                val clothesTypeByCategory = it.clothesTypeByCategory

                // 객체에 담는다.
                val clothesViewModel = ClothesViewModel(
                    clothesIdx,
                    clothesPicture,
                    clothesName,
                    clothesPrice,
                    clothesInventory,
                    clothesColor,
                    clothesSize,
                    clothesCategory,
                    clothesTypeByCategory
                )

                // 리스트에 담는다.
                clothesViewModelList.add(clothesViewModel)
            }
            return clothesViewModelList
        }

    }
}