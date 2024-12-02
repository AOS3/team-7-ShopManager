package com.lion.team7_shopping_mall.showfragment

import android.os.Bundle
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
import com.lion.team7_shopping_mall.databinding.FragmentShowMainBinding
import com.lion.team7_shopping_mall.inputFragment.InputFragment
import com.lion.team7_shopping_mall.mainfragment.MainFragment
import com.lion.temp.util.FragmentName

class ShowMainFragment : Fragment() {

    lateinit var fragmentShowMainBinding: FragmentShowMainBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentShowMainBinding = FragmentShowMainBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        settingToolbarShow()

        val clothesIdx = arguments?.getInt("clothesIdx")
        Log.d("testIDX", "testIDX : ${clothesIdx}")
        val dataBundle = Bundle()
        dataBundle.putInt("ClothesIDX", clothesIdx!!)
        replaceShowFragment(ShowFragmentName.SHOW_FRAGMENT, false, true, dataBundle)

        return fragmentShowMainBinding.root
    }

    fun settingToolbarShow() {
        fragmentShowMainBinding.apply {
            toolbarShowClothes.title = "옷 정보"

            toolbarShowClothes.setNavigationIcon(R.drawable.arrow_back_24px)
            toolbarShowClothes.setNavigationOnClickListener {
                mainActivity.removeFragment(FragmentName.SHOW_FRAGMENT)
            }

            toolbarShowClothes.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.itemShowClothesModify -> {
                        Log.d("clothes", "수정키 클릭")
                    }
                    R.id.ItemShowClothesDelete -> {
                        Log.d("clothes", "삭제키 클릭")
                    }
                }

                true
            }
        }
    }

    // 프래그먼트를 교체하는 함수
    fun replaceShowFragment(showFragmentName: ShowFragmentName, isAddToBackStack:Boolean, animate:Boolean, dataBundle: Bundle?){
        // 프래그먼트 객체
        val newFragment = when(showFragmentName){
            ShowFragmentName.SHOW_FRAGMENT -> ShowClothesFragment(this, fragmentShowMainBinding)
            ShowFragmentName.SHOW_INOUT_FRAGMENT -> ShowInOutFragment(this, fragmentShowMainBinding)
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

            replace(R.id.fragmentContainerViewShow, newFragment)
            if(isAddToBackStack){
                addToBackStack(showFragmentName.str)
            }
        }
    }

    // 프래그먼트를 BackStack에서 제거하는 메서드
    fun removeShowFragment(showFragmentName: ShowFragmentName){
        mainActivity.supportFragmentManager.popBackStack(showFragmentName.str, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

}

enum class ShowFragmentName(var number:Int, var str:String) {
    SHOW_FRAGMENT(1, "ShowFragment"),
    SHOW_INOUT_FRAGMENT(2, "ShowInOutFragment"),
}