package com.lion.team7_shopping_mall.inputFragment

//사용자가입력한값을 잠시보관하기위한 오브젝트
object temp {
    var name: String = ""
    var price: Int = 0
    var stock: Int = 0
    var type: Int = 0 //아우터,셔츠,바지,치마
    var category: String = "" //아우터안에 코트인지패딩인지저장
    var sizes: MutableList<String> = mutableListOf()
    var colors: MutableList<String> = mutableListOf()

    // 값 초기화
    fun clear() {
        name = ""
        price = 0
        stock = 0
        type = 0
        category = ""
        sizes.clear()
        colors.clear()
    }
}
