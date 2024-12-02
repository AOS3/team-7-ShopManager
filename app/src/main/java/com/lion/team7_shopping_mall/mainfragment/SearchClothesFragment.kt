package com.lion.team7_shopping_mall.mainfragment

import android.net.Uri
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
import com.lion.team7_shopping_mall.R
import com.lion.team7_shopping_mall.databinding.FragmentSearchClothesBinding
import com.lion.team7_shopping_mall.databinding.RowBinding
import com.lion.team7_shopping_mall.repository.ClothesRepository
import com.lion.team7_shopping_mall.viewmodel.ClothesViewModel
import com.lion.temp.util.FragmentName
import com.lion.temp.util.SubFragmentName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SearchClothesFragment(val mainFragment: MainFragment) : Fragment() {

    lateinit var fragmentSearchClothesBinding: FragmentSearchClothesBinding
    lateinit var mainActivity: MainActivity

//    // 사용할 데이터
//    val imageArray = arrayOf(
//        R.drawable.imgflag1,
//        R.drawable.imgflag2,
//        R.drawable.imgflag3,
//        R.drawable.imgflag4,
//        R.drawable.imgflag5,
//        R.drawable.imgflag6,
//        R.drawable.imgflag7,
//        R.drawable.imgflag8,
//    )
//
//    // 문자열
//    val strArray = arrayOf(
//        "토고", "프랑스", "스위스", "스페인", "일본", "독일", "브라질", "대한민국"
//    )

    var clothesList = mutableListOf<ClothesViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        fragmentSearchClothesBinding = FragmentSearchClothesBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity

        // 툴바를 구성하는 메서드 호출
        settingToolbarSearchClothes()
        // RecyclerView를 구성하는 메서드 호출
        settingRecyclerViewSearchClothes()
        // 입력 요소 설정 메서드를 호출한다.
        settingTextField()

        return fragmentSearchClothesBinding.root
    }

    // 툴바를 구성하는 메서드
    fun settingToolbarSearchClothes(){
        fragmentSearchClothesBinding.apply {
            toolbarSearchClothes.title = "정보 검색"

            toolbarSearchClothes.setNavigationIcon(R.drawable.arrow_back_24px)
            toolbarSearchClothes.setNavigationOnClickListener{
                mainFragment.removeFragment(SubFragmentName.SEARCH_CLOTHES_FRAGMENT)
            }
        }
    }



    // recyclerView를 구성하는 메서드
    fun settingRecyclerViewSearchClothes(){
        fragmentSearchClothesBinding.apply {
            recyclerViewSearchClothes.adapter = RecyclerViewClothesSearchAdapter()
            recyclerViewSearchClothes.layoutManager = LinearLayoutManager(mainActivity)
            val deco = MaterialDividerItemDecoration(mainActivity, MaterialDividerItemDecoration.VERTICAL)
            recyclerViewSearchClothes.addItemDecoration(deco)
        }
    }


    // Recyclerview의 어뎁터
    inner class RecyclerViewClothesSearchAdapter : RecyclerView.Adapter<RecyclerViewClothesSearchAdapter.ViewHolderClothesSearch>(){
        inner class ViewHolderClothesSearch(var rowBinding: RowBinding) : RecyclerView.ViewHolder(rowBinding.root),OnClickListener {
            override fun onClick(v: View?) {
                // 세부 정보를 보는 화면으로 이동한다.
                val dataBundle = Bundle()
                dataBundle.putInt("clothesIdx",clothesList[adapterPosition].clothesIdx)

                // 옷 정보를 보는 화면으로 이동한다
                mainActivity.replaceFragment(FragmentName.SHOW_FRAGMENT,true,true,dataBundle)

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClothesSearch {
            val rowBinding = RowBinding.inflate(layoutInflater, parent, false)
            val viewHolderClothesSearch = ViewHolderClothesSearch(rowBinding)
            rowBinding.root.setOnClickListener(viewHolderClothesSearch)
            return viewHolderClothesSearch
        }

        override fun getItemCount(): Int {
            return clothesList.size
        }

        override fun onBindViewHolder(holder: ViewHolderClothesSearch, position: Int) {
            holder.rowBinding.imageViewRow.setImageURI( Uri.parse(clothesList[position].clothesPicture))
            holder.rowBinding.textViewRow.text = clothesList[position].clothesName
        }
    }

    // 입력 요소 설정
    fun settingTextField(){
        fragmentSearchClothesBinding.apply {
            // 검색창에 포커스를 준다.
            mainActivity.showSoftInput(textFieldSearchClothesName.editText!!)
            // 키보드의 엔터를 누르면 동작하는 리스너
            textFieldSearchClothesName.editText?.setOnEditorActionListener { v, actionId, event ->
                // 검색 데이터를 가져와 보여준다.
                CoroutineScope(Dispatchers.Main).launch {
                    val work1 = async(Dispatchers.IO){
                        val keyword = textFieldSearchClothesName.editText?.text.toString()
                        ClothesRepository.selectClothesDataAllByClothesName(mainActivity,keyword)
                    }
                    clothesList = work1.await()
                    recyclerViewSearchClothes.adapter?.notifyDataSetChanged()
                }
                mainActivity.hideSoftInput()
                true
            }
        }
    }

}