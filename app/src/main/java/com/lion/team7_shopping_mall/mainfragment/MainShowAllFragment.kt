package com.lion.team7_shopping_mall.mainfragment

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.lion.team7_shopping_mall.MainActivity
import com.lion.team7_shopping_mall.R
import com.lion.team7_shopping_mall.databinding.FragmentMainShowAllBinding
import com.lion.team7_shopping_mall.databinding.RowBinding
import com.lion.team7_shopping_mall.repository.ClothesRepository
import com.lion.team7_shopping_mall.viewmodel.ClothesViewModel
import com.lion.temp.util.FragmentName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainShowAllFragment : Fragment() {

    lateinit var fragmentMainShowAllBinding: FragmentMainShowAllBinding
    lateinit var mainActivity: MainActivity

    /*// 확인용 데이터
    // 사용할 데이터
    // 이미지 리소스 아이
    val imageArray = arrayOf(
        R.drawable.imgflag1,
        R.drawable.imgflag2,
        R.drawable.imgflag3,
        R.drawable.imgflag4,
        R.drawable.imgflag5,
        R.drawable.imgflag6,
        R.drawable.imgflag7,
        R.drawable.imgflag8
    )

    // 문자열1
    val strArray = arrayOf(
        "토고", "프랑스", "스위스", "스페인", "일본", "독일", "브라질", "대한민국"
    )*/

    var clothesList = mutableListOf<ClothesViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentMainShowAllBinding = FragmentMainShowAllBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        // RecyclerView를 구성하는 메서드를 호출
        settingRecyclerView()

        return fragmentMainShowAllBinding.root
    }

    // recyclerView를 구성하는 메서드
    fun settingRecyclerView(){
        fragmentMainShowAllBinding.apply {
            RecyclerViewAll.adapter = RecyclerViewShowAllAdapter()
            RecyclerViewAll.layoutManager = LinearLayoutManager(mainActivity)
            val deco = MaterialDividerItemDecoration(mainActivity, MaterialDividerItemDecoration.VERTICAL)
            RecyclerViewAll.addItemDecoration(deco)

            //  데이터를 읽어와 리사이클러 뷰 갱신
            refreshRecyclerView()
        }
    }
    
    // 데이터베이스에서 데이터를 읽어와 RecyclerView를 갱신한다
    fun refreshRecyclerView(){
        CoroutineScope(Dispatchers.Main).launch {
            val work1 = async(Dispatchers.IO){
                // 데이터를 읽어온다
                ClothesRepository.selectClothesInfoAll(mainActivity)
            }
            clothesList = work1.await()


            // 리사이클러뷰 갱신
            fragmentMainShowAllBinding.RecyclerViewAll.adapter?.notifyDataSetChanged()
        }
    }


    // Recyclerview의 어뎁터
    inner class RecyclerViewShowAllAdapter : RecyclerView.Adapter<RecyclerViewShowAllAdapter.ViewHolderShowAll>(){
        // ViewHolder
        inner class ViewHolderShowAll(var rowBinding: RowBinding) : RecyclerView.ViewHolder(rowBinding.root), OnClickListener {
            override fun onClick(v: View?) {
                // 옷 번호를 담는다
                val dataBundle = Bundle()
                dataBundle.putInt("IDX",adapterPosition)

                // 옷 정보를 보는 화면으로 이동한다
                mainActivity.replaceFragment(FragmentName.SHOW_FRAGMENT,true,true, dataBundle)

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderShowAll {
            val rowBinding = RowBinding.inflate(layoutInflater, parent, false)
            val viewHolderShowAll = ViewHolderShowAll(rowBinding)
            rowBinding.root.setOnClickListener(viewHolderShowAll)
            return viewHolderShowAll
        }

        override fun getItemCount(): Int {
            return clothesList.size
        }

        override fun onBindViewHolder(holder: ViewHolderShowAll, position: Int) {
            holder.rowBinding.imageViewRow.setImageURI(Uri.parse(clothesList[position].clothesPicture))
            holder.rowBinding.textViewRow.text = clothesList[position].clothesName
        }
    }
}