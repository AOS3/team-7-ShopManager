package com.lion.team7_shopping_mall.modifyFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.google.android.material.transition.MaterialSharedAxis
import com.lion.team7_shopping_mall.MainActivity
import com.lion.team7_shopping_mall.R
import com.lion.team7_shopping_mall.databinding.FragmentModifyBinding
import com.lion.team7_shopping_mall.inputFragment.InputFragment
import com.lion.team7_shopping_mall.mainfragment.MainFragment
import com.lion.team7_shopping_mall.modifyFragment.category.ModifyPantsFragment
import com.lion.team7_shopping_mall.modifyFragment.category.ModifyShirtFragment
import com.lion.team7_shopping_mall.modifyFragment.category.ModifySkirtFragment
import com.lion.temp.util.ClothesCategoryName
import com.lion.temp.util.FragmentName
import com.lion.temp.util.ModifyFragmentName

class ModifyFragment() : Fragment() {
    lateinit var fragmentModifyBinding: FragmentModifyBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentModifyBinding = FragmentModifyBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        settingModifyFragment()
        return fragmentModifyBinding.root
    }
    private fun settingModifyFragment(){
        val clothesCategory = arguments?.getString("ClothesCategory")!!
        val clothesIdx =  arguments?.getInt("ClothesIDX")!!

        val dataBundle = Bundle()
        dataBundle.putInt("ClothesIDX",clothesIdx)

        when(clothesCategory){
            ClothesCategoryName.OUTER.str ->{
                replaceFragment(ModifyFragmentName.MODIFY_OUTER_FRAGMENT,true,false, dataBundle)
            }
            ClothesCategoryName.SHIRT.str ->{
                replaceFragment(ModifyFragmentName.MODIFY_SHIRT_FRAGMENT,true,false, dataBundle)
            }
            ClothesCategoryName.PANTS.str ->{
                replaceFragment(ModifyFragmentName.MODIFY_PANTS_FRAGMENT,true,false, dataBundle)
            }
            ClothesCategoryName.SKIRT.str ->{
                replaceFragment(ModifyFragmentName.MODIFY_SKIRT_FRAGMENT,true,false, dataBundle)
            }
        }


    }

    // 프래그먼트를 교체하는 함수
    fun replaceFragment(modifyFragmentName: ModifyFragmentName, isAddToBackStack:Boolean, animate:Boolean, dataBundle: Bundle?){
        // 프래그먼트 객체
        val newFragment = when(modifyFragmentName){
            ModifyFragmentName.MODIFY_OUTER_FRAGMENT -> ModifyOuterFragment(this)
            ModifyFragmentName.MODIFY_SHIRT_FRAGMENT -> ModifyShirtFragment(this)
            ModifyFragmentName.MODIFY_PANTS_FRAGMENT -> ModifyPantsFragment(this)
            ModifyFragmentName.MODIFY_SKIRT_FRAGMENT -> ModifySkirtFragment(this)
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

            replace(R.id.ModifyFragmentContainerView, newFragment)
            if(isAddToBackStack){
                addToBackStack(modifyFragmentName.str)
            }
        }
    }

    // 프래그먼트를 BackStack에서 제거하는 메서드
    fun removeFragment(modifyFragmentName: ModifyFragmentName){
        mainActivity.supportFragmentManager.popBackStack(modifyFragmentName.str, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }




}