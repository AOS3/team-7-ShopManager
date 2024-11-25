package com.lion.temp.util

// 프래그먼트를 나타내는 값
enum class FragmentName(var number:Int, var str:String){
    // 첫 화면
    MAIN_FRAGMENT(1, "MainFragment"),
    // 입력 화면
    INPUT_FRAGMENT(2, "InputFragment"),
    // 출력 화면
    SHOW_FRAGMENT(3, "ShowFragment"),
    // 수정 화면
    MODIFY_FRAGMENT(4, "ModifyFragment"),
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
