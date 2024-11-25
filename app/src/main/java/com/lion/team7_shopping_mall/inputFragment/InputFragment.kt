package com.lion.team7_shopping_mall.inputFragment


import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import com.lion.team7_shopping_mall.MainActivity
import com.lion.team7_shopping_mall.R
import com.lion.team7_shopping_mall.databinding.FragmentInputBinding
import com.lion.team7_shopping_mall.databinding.FragmentInputOuterBinding
import com.lion.team7_shopping_mall.databinding.FragmentInputPantsBinding
import com.lion.team7_shopping_mall.databinding.FragmentInputShirtBinding
import com.lion.team7_shopping_mall.databinding.FragmentInputSkirtBinding
import com.lion.team7_shopping_mall.inputFragment.category.InputOuterFragment
import com.lion.team7_shopping_mall.inputFragment.category.InputPantsFragment
import com.lion.team7_shopping_mall.inputFragment.category.InputShirtFragment
import com.lion.team7_shopping_mall.inputFragment.category.InputSkirtFragment
import com.lion.temp.util.FragmentName

import com.lion.temp.util.InputFragmentName


class InputFragment() : Fragment() {
    lateinit var fragmentInputBinding: FragmentInputBinding
    lateinit var mainActivity: MainActivity
    private var currentFragmentState: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentInputBinding = FragmentInputBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        settingFragment()

        return fragmentInputBinding.root
    }

    //저장눌렀을때 다이얼로그-> 세이브
    private fun showDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("저장")
            .setMessage("저장하시겠습니까?")
            .setPositiveButton("네") { dialog, _ ->
                Log.d("InputData","temp : ${temp.name}")
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }



    //각각의 프래그먼트로 이동하는 부분
    private fun settingFragment() {
        fragmentInputBinding.apply {
            materialToolbarInput.title = "상품입력"
            materialToolbarInput.isTitleCentered = true
            materialToolbarInput.inflateMenu(R.menu.menu_input)

            //초기설정
            replaceInputFragment(InputFragmentName.INPUT_OUTER_FRAGMENT, false, null)
            currentFragmentState = InputFragmentName.INPUT_OUTER_FRAGMENT.number

            materialToolbarInput.setOnMenuItemClickListener {

                when (it.itemId) {
                    R.id.itemSave -> {
                        showDialog()
                    }
                    R.id.input_outer_menu -> {
                        if (currentFragmentState != InputFragmentName.INPUT_OUTER_FRAGMENT.number) {
                            showMoveDialog() { result ->
                                if (result) {
                                    replaceInputFragment(
                                        InputFragmentName.INPUT_OUTER_FRAGMENT,
                                        false,
                                        null
                                    )
                                    currentFragmentState =
                                        InputFragmentName.INPUT_OUTER_FRAGMENT.number
                                }
                            }
                        }
                    }

                    R.id.input_shirt_menu -> {
                        if (currentFragmentState != InputFragmentName.INPUT_SHIRT_FRAGMENT.number) {
                            showMoveDialog() { result ->
                                if (result) {
                                    replaceInputFragment(
                                        InputFragmentName.INPUT_SHIRT_FRAGMENT,
                                        false,
                                        null
                                    )
                                    currentFragmentState =
                                        InputFragmentName.INPUT_SHIRT_FRAGMENT.number
                                }
                            }
                        }
                    }

                    R.id.input_pants_menu -> {
                        if (currentFragmentState != InputFragmentName.INPUT_PANTS_FRAGMENT.number) {
                            showMoveDialog() { result ->
                                if (result) {
                                    replaceInputFragment(
                                        InputFragmentName.INPUT_PANTS_FRAGMENT,
                                        false,
                                        null
                                    )
                                    currentFragmentState =
                                        InputFragmentName.INPUT_PANTS_FRAGMENT.number
                                }
                            }
                        }
                    }

                    R.id.input_skirt_menu -> {
                        if (currentFragmentState != InputFragmentName.INPUT_SKIRT_FRAGMENT.number) {
                            showMoveDialog() { result ->
                                if (result) {
                                    replaceInputFragment(
                                        InputFragmentName.INPUT_SKIRT_FRAGMENT,
                                        false,
                                        null
                                    )
                                    currentFragmentState =
                                        InputFragmentName.INPUT_SKIRT_FRAGMENT.number
                                }
                            }
                        }
                    }
                }
                true
            }
        }
    }


    //프래그먼트끼리이동할때 고지하는 다이얼로그
    private fun showMoveDialog(callback: (Boolean) -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("안내")
            .setMessage(
                "입력도중 카테고리를 넘어가면\n" +
                        "기존에 입력하신내용은 사라집니다"
            )
            .setPositiveButton("네") { dialog, _ ->
                callback(true) // 확인 버튼 클릭 시 true 전달
                //다른프래그먼트로 이동했을때 temp를 비워준다
                temp.clear()
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                callback(false) // 취소 버튼 클릭 시 false 전달
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }


    // 입력프래그먼트안에서 프래그먼트를 교체하는 함수
    fun replaceInputFragment(
        inputFragmentName: InputFragmentName,
        isAddToBackStack: Boolean,
        dataBundle: Bundle?
    ) {
        // 프래그먼트 객체
        val newFragment = when (inputFragmentName) {
            InputFragmentName.INPUT_OUTER_FRAGMENT -> InputOuterFragment()
            InputFragmentName.INPUT_SHIRT_FRAGMENT -> InputShirtFragment()
            InputFragmentName.INPUT_PANTS_FRAGMENT -> InputPantsFragment()
            InputFragmentName.INPUT_SKIRT_FRAGMENT -> InputSkirtFragment()
        }

        // bundle 객체가 null이 아니라면
        if (dataBundle != null) {
            newFragment.arguments = dataBundle
        }

        // 프래그먼트 교체
        mainActivity.supportFragmentManager.commit {

//            newFragment.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
//            newFragment.reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
//            newFragment.enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
//            newFragment.returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)

            replace(R.id.fragmentContainerViewInput, newFragment)
            if (isAddToBackStack) {
                addToBackStack(inputFragmentName.str)
            }
        }
    }


}