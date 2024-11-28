package com.lion.team7_shopping_mall.showfragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.lion.team7_shopping_mall.MainActivity
import com.lion.team7_shopping_mall.databinding.FragmentShowInOutBinding
import com.lion.team7_shopping_mall.databinding.FragmentShowMainBinding
import com.lion.team7_shopping_mall.databinding.RowShowInoutBinding

class ShowInOutFragment(val showMainFragment: ShowMainFragment, val fragmentShowMainBinding: FragmentShowMainBinding) : Fragment() {

    lateinit var fragmentShowInOutBinding: FragmentShowInOutBinding
    lateinit var mainActivity: MainActivity

    var dataList = Array(30) {
        when(it % 2) {
            0 -> "입고"
            else -> "출고"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentShowInOutBinding = FragmentShowInOutBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        settingRecyclerView()
        settingToolbar()

        return fragmentShowInOutBinding.root
    }

    fun settingToolbar() {
        fragmentShowInOutBinding.apply {
            fragmentShowMainBinding.toolbarShowClothes.title = "입출력 내역"
            // 툴바 메뉴를 제거
            fragmentShowMainBinding.toolbarShowClothes.menu.clear()
            fragmentShowMainBinding.toolbarShowClothes.setNavigationOnClickListener {
                showMainFragment.removeShowFragment(ShowFragmentName.SHOW_INOUT_FRAGMENT)
            }
        }
    }

    // RecyclerView를 구성하는 메서드
    fun settingRecyclerView(){
        fragmentShowInOutBinding.apply {
            // 어뎁터
            recyclerViewShowInOutHistory.adapter = RecyclerViewShowAdapter()
            // LayoutManager
            recyclerViewShowInOutHistory.layoutManager = LinearLayoutManager(mainActivity)
            // 구분선
            val deco = MaterialDividerItemDecoration(mainActivity, MaterialDividerItemDecoration.VERTICAL)
            recyclerViewShowInOutHistory.addItemDecoration(deco)
        }
    }

    inner class RecyclerViewShowAdapter : RecyclerView.Adapter<RecyclerViewShowAdapter.ViewHolderShowInOut>() {

        inner class ViewHolderShowInOut(val rowShowInoutBinding: RowShowInoutBinding) : RecyclerView.ViewHolder(rowShowInoutBinding.root),
            OnClickListener {
            override fun onClick(v: View?) {

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderShowInOut {
            val rowShowInoutBinding = RowShowInoutBinding.inflate(layoutInflater, parent, false)
            val viewHolderMain = ViewHolderShowInOut(rowShowInoutBinding)

            rowShowInoutBinding.root.setOnClickListener(viewHolderMain)

            return viewHolderMain
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        // 이미지를 그냥 bitmap으로 접근해서 생성하면 버벅임 발생
        // 그래서 coil라이브러리를 사용해야함
        // README.md파일에 정리해놈
//        override fun onBindViewHolder(holder: ViewHolderMain, position: Int) {
//            val imageBitmapClothes = loadImageFromInternalStorage(clothesList[position].clothesName)
//            holder.recyclerViewRowMainTabBinding.imageView.setImageBitmap(imageBitmapClothes)
//            holder.recyclerViewRowMainTabBinding.textViewClothesName.text = clothesList[position].clothesName
//        }
        override fun onBindViewHolder(holder: ViewHolderShowInOut, position: Int) {
            holder.rowShowInoutBinding.textViewShowInOutCheck.apply {
                text = dataList[position]
                when(text) {
                    "입고" -> setTextColor(Color.BLUE) // 텍스트 색상을 파란색으로 설정
                    "출고" -> setTextColor(Color.RED) // 텍스트 색상을 파란색으로 설정
                }
            }
        }

    }


}