package com.lion.team7_shopping_mall.showfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lion.team7_shopping_mall.R
import com.lion.team7_shopping_mall.databinding.FragmentShowClothesBinding

class ShowClothesFragment : Fragment() {

    lateinit var fragmentShowClothesBinding: FragmentShowClothesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentShowClothesBinding = FragmentShowClothesBinding.inflate(inflater)

        settingImageViewTextView()
        settingToolbar()

        return fragmentShowClothesBinding.root
    }

    fun settingToolbar() {
        fragmentShowClothesBinding.apply {
            toolbarShowClothes.title = "옷 정보"

            toolbarShowClothes.setNavigationIcon(R.drawable.arrow_back_24px)
            toolbarShowClothes.setNavigationOnClickListener {

            }

            toolbarShowClothes.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.itemShowClothesModify -> imageViewShowClothesImage.setImageResource(R.drawable.padding_image)
                }

                true
            }
        }
    }

    fun settingImageViewTextView() {
        fragmentShowClothesBinding.apply {
            imageViewShowClothesImage.setImageResource(R.drawable.padding_image)
            textViewShowClothesName.setText("패딩")
        }
    }

}