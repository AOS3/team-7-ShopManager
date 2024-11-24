package com.lion.team7_shopping_mall.viewmodel

data class ClothesViewModel(
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