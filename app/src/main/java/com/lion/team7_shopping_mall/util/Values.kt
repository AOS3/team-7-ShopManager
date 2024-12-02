package com.lion.temp.util

// 프래그먼트를 나타내는 값
enum class FragmentName(var number:Int, var str:String){
    // 첫 화면
    MAIN_FRAGMENT(1, "MainFragment"),
    // 입력 화면
    INPUT_FRAGMENT(2, "InputFragment"),
    // 출력 화면
    SHOW_FRAGMENT(3, "ShowMainFragment"),       // 이부분 수정 2024.11.27
    // 수정 화면
    MODIFY_FRAGMENT(4, "ModifyFragment"),
}

// 프래그먼트를 나타내는 값
enum class SubFragmentName(val number:Int, val str:String){

    // 옷 목록 화면
    CLOTHES_LIST_FRAGMENT(1, "ClothesListFragment"),

    // 옷 검색 화면
    SEARCH_CLOTHES_FRAGMENT(2, "SearchClothesFragment"),

    // 옷 상세 정보 화면
    SHOW_STUDENT_FRAGMENT(3,"ShowClothesFragment")
}

//프래그먼트를 나타내는값
enum class InputFragmentName(var number:Int, var str:String){
    // 아우터입력
    INPUT_OUTER_FRAGMENT(1, "InputOuterFragment"),
    // 셔츠입력
    INPUT_SHIRT_FRAGMENT(2, "InputShirtFragment"),
    // 바지입력
    INPUT_PANTS_FRAGMENT(3, "InputPantsFragment"),
    // 치마입력
    INPUT_SKIRT_FRAGMENT(4, "InputSkirtFragment"),
}

enum class ClothesTypeByCategoryName(var number:Int,var str:String){
    COAT(1,"Coat"),
    LONG_PADDING(2,"Long padding"),
    SHORT_PADDING(3,"Short padding"),
    JEANS(4,"Jeans"),
    COTTON_PANTS(5,"Cotton Pants"),
    SHORT_PANTS(6,"Shorts Pants"),
    LONG_SKIRT(7,"Long skirt"),
    MIDI_SKIRT(8,"Midi skirt"),
    MINI_SKIRT(9,"Mini skirt"),
    HOODIE(10,"Hoodie"),
    SWEAT_SHIRT(11,"Sweat shirt"),
    SHORT_SLEEVE_T_SHIRT(12,"Short sleeve t-shirt")
}

enum class ClothesCategoryName(var number:Int,var str:String){
    OUTER(1,"Outer"),
    SHIRT(2,"Shirt"),
    PANTS(3,"Pants"),
    SKIRT(4,"Skirt")
}
