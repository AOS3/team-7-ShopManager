package com.lion.team7_shopping_mall.inputFragment


//사용자가입력한값을 잠시보관하기위한 오브젝트
object temp {
    var clothesPicture:String = ""
    var clothesName:String = ""
    var clothesPrice:Int = 0
    var clothesInventory:Int = 0
    var clothesColor:String = ""
    var clothesSize:String = ""
    var clothesCategory:String = ""
    var clothesTypeByCategory:String = ""

    // 값 초기화
    fun clear() {
        clothesPicture = ""
        clothesName = ""
        clothesPrice = 0
        clothesInventory = 0
        clothesColor = ""
        clothesSize = ""
        clothesCategory = ""
        clothesTypeByCategory = ""
    }
}


//@Entity(tableName = "ClothesTable")
//data class ClothesVO(
//    @PrimaryKey(autoGenerate = true)
//    var clothesIdx:Int = 0,
//    var clothesPicture:String = "",
//    var clothesName:String = "",
//    var clothesPrice:Int = 0,
//    var clothesInventory:Int = 0,
//    var clothesColor:String = "",
//    var clothesSize:String = "",
//    var clothesCategory:String = "",
//    var clothesTypeByCategory:String = ""
//)

