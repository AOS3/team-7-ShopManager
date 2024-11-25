package com.lion.team7_shopping_mall.mainfragment

import android.os.Bundle
import android.text.TextUtils.replace
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.google.android.material.transition.MaterialSharedAxis
import com.lion.team7_shopping_mall.MainActivity
import com.lion.team7_shopping_mall.R
import com.lion.team7_shopping_mall.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    lateinit var fragmentMainBinding: FragmentMainBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMainBinding = FragmentMainBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        // 첫 화면이 보이도록 설정한다.
        replaceFragment(SubFragmentName.CLOTHES_LIST_FRAGMENT, false, false, null)

        return fragmentMainBinding.root
    }


    // 프래그먼트를 교체하는 함수
    fun replaceFragment(fragmentName: SubFragmentName, isAddToBackStack:Boolean, animate:Boolean, dataBundle: Bundle?){
        // 프래그먼트 객체
        val newFragment = when(fragmentName){
            // 학생 목록 화면
            SubFragmentName.CLOTHES_LIST_FRAGMENT -> ClothesListFragment(this)
            // 학생 정보 검색 화면
            SubFragmentName.SEARCH_CLOTHES_FRAGMENT -> SearchClothesFragment(this)
            // 옷 상세 정보 보는 화면

            // 옷 정보 수정 화면

        }

        // bundle 객체가 null이 아니라면
        if(dataBundle != null){
            newFragment.arguments = dataBundle
        }

        // 프래그먼트 교체
        mainActivity.supportFragmentManager.commit {

            if(animate) {
                newFragment.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
                newFragment.reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
                newFragment.enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
                newFragment.returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
            }

            replace(R.id.fragmentContainerViewMain2, newFragment)
            if(isAddToBackStack){
                addToBackStack(fragmentName.str)
            }
        }
    }

    // 프래그먼트를 BackStack에서 제거하는 메서드
    fun removeFragment(fragmentName: SubFragmentName){
        mainActivity.supportFragmentManager.popBackStack(fragmentName.str, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

}

// MainFragment를 통해 보여줄 Fragment들의 이름

enum class SubFragmentName(val number:Int, val str:String){

    // 학생 목록 화면
    CLOTHES_LIST_FRAGMENT(1, "ClothesListFragment"),

    // 옷 검색 화면
    SEARCH_CLOTHES_FRAGMENT(2, "SearchClothesFragment")


}