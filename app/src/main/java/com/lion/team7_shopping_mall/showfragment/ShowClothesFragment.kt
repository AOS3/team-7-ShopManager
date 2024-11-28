package com.lion.team7_shopping_mall.showfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lion.team7_shopping_mall.MainActivity
import com.lion.team7_shopping_mall.R
import com.lion.team7_shopping_mall.databinding.FragmentShowClothesBinding
import com.lion.team7_shopping_mall.databinding.FragmentShowMainBinding
import com.lion.temp.util.FragmentName

class ShowClothesFragment(val showMainFragment: ShowMainFragment, val fragmentShowMainBinding: FragmentShowMainBinding) : Fragment() {

    lateinit var fragmentShowClothesBinding: FragmentShowClothesBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentShowClothesBinding = FragmentShowClothesBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        settingButtonShowInOutHistory()
        settingImageViewTextView()
        settingImageView()
        settingToolbar()

        return fragmentShowClothesBinding.root
    }

    fun settingImageView() {
        fragmentShowClothesBinding.apply {
            imageViewShowClothesImage.setImageResource(R.drawable.padding_image)
        }
    }

    fun settingToolbar() {
        fragmentShowClothesBinding.apply {
            fragmentShowMainBinding.toolbarShowClothes.title = "옷 정보"
            fragmentShowMainBinding.toolbarShowClothes.inflateMenu(R.menu.show_clothes_toolbar_menu)
            fragmentShowMainBinding.toolbarShowClothes.setNavigationOnClickListener {
                mainActivity.removeFragment(FragmentName.SHOW_FRAGMENT)
            }
        }
    }


    //////////////////////////////////입출력 버튼 부터 시작하면 됨///////////////////////////////////////
    fun settingButtonShowInventoryInput() {
        fragmentShowClothesBinding.apply {
            buttonShowInventoryInput.setOnClickListener {

            }
        }
    }

    fun settingButtonShowInventoryOutput() {
        fragmentShowClothesBinding.apply {
            buttonShowInventoryOutput.setOnClickListener {

            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun settingButtonShowInOutHistory() {
        fragmentShowClothesBinding.apply {
            buttonShowInOutHistory.setOnClickListener {
                showMainFragment.replaceShowFragment(ShowFragmentName.SHOW_INOUT_FRAGMENT, true, true, null)
            }
        }
    }

    fun settingImageViewTextView() {
        fragmentShowClothesBinding.apply {
//            CoroutineScope(Dispatchers.Main).launch {
//                val work1 = async(Dispatchers.IO) {
//                    // 학생 번호를 가져온다.
//                    // val studentIdx = arguments?.getInt("studentIdx")
//                    // 테스트용
//                    val clothesIdx = 1
//
//                    // 학생 데이터를 가져온다.
//                    ClothesRepository.selectClothesInfoByClothesIdx(mainActivity, clothesIdx)
//                }
//                val clothesModel = work1.await()
//
//                Log.d("clothesModel", "${clothesModel}")
//
//                imageViewShowClothesImage.setImageResource(R.drawable.padding_image)
//                textViewShowClothesName.text = clothesModel.clothesName
//                textViewShowClothesPrice.setText("${clothesModel.clothesPrice} 원")
//                textViewSHowClothesInventory.setText("${clothesModel.clothesInventory} 개")
//                textViewSHowClothesColor.setText(clothesModel.clothesColor)
//                textViewSHowClothesSize.setText(clothesModel.clothesSize)
//                textViewSHowClothesTypeByCategory.setText(clothesModel.clothesTypeByCategory)
//            }

            imageViewShowClothesImage.setImageResource(R.drawable.padding_image)
            textViewShowClothesName.text = "오리털 파카"
            textViewShowClothesPrice.setText("10000 원")
            textViewSHowClothesInventory.setText("100 개")
            textViewSHowClothesColor.setText("검정색")
            textViewSHowClothesSize.setText("L")
            textViewSHowClothesTypeByCategory.setText("숏패딩")
        }
    }

}