package com.lion.team7_shopping_mall.mainfragment

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
import com.lion.temp.util.FragmentName

class MainShowAllFragment : Fragment() {

    lateinit var fragmentMainShowAllBinding: FragmentMainShowAllBinding
    lateinit var mainActivity: MainActivity

    // 확인용 데이터
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
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentMainShowAllBinding = FragmentMainShowAllBinding.inflate(inflater)
        mainActivity = activity as MainActivity

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
        }
    }


    // Recyclerview의 어뎁터
    inner class RecyclerViewShowAllAdapter : RecyclerView.Adapter<RecyclerViewShowAllAdapter.ViewHolderShowAll>(){
        inner class ViewHolderShowAll(var rowBinding: RowBinding) : RecyclerView.ViewHolder(rowBinding.root),
            View.OnClickListener {
            override fun onClick(v: View?) {
                val dataBundle = Bundle()
                dataBundle.putInt("IDX",adapterPosition)
                mainActivity.replaceFragment(FragmentName.SHOW_FRAGMENT,true,false,null)

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderShowAll {
            val rowBinding = RowBinding.inflate(layoutInflater, parent, false)
            val viewHolderShowAll = ViewHolderShowAll(rowBinding)
            rowBinding.root.setOnClickListener(viewHolderShowAll)
            return viewHolderShowAll
        }

        override fun getItemCount(): Int {
            return imageArray.size
        }

        override fun onBindViewHolder(holder: ViewHolderShowAll, position: Int) {
            holder.rowBinding.imageViewRow.setImageResource(imageArray[position])
            holder.rowBinding.textViewRow.text = strArray[position]
        }
    }
}