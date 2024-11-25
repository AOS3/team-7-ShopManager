package com.lion.team7_shopping_mall.mainfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.lion.team7_shopping_mall.MainActivity
import com.lion.team7_shopping_mall.R
import com.lion.team7_shopping_mall.databinding.FragmentMainShowShirtBinding
import com.lion.team7_shopping_mall.databinding.RowBinding

class MainShowShirtFragment : Fragment() {
    lateinit var fragmentMainShowShirtBinding: FragmentMainShowShirtBinding
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

        fragmentMainShowShirtBinding = FragmentMainShowShirtBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        settingRecyclerView()

        return fragmentMainShowShirtBinding.root
    }

    // recyclerView를 구성하는 메서드
    fun settingRecyclerView(){
        fragmentMainShowShirtBinding.apply {
            RecyclerViewShirt.adapter = RecyclerViewShowShirtAdapter()
            RecyclerViewShirt.layoutManager = LinearLayoutManager(mainActivity)
            val deco = MaterialDividerItemDecoration(mainActivity, MaterialDividerItemDecoration.VERTICAL)
            RecyclerViewShirt.addItemDecoration(deco)
        }
    }

    // Recyclerview의 어뎁터
    inner class RecyclerViewShowShirtAdapter : RecyclerView.Adapter<RecyclerViewShowShirtAdapter.ViewHolderShowShirt>() {
        inner class ViewHolderShowShirt(var rowBinding: RowBinding) :
            RecyclerView.ViewHolder(rowBinding.root),
            View.OnClickListener {
            override fun onClick(v: View?) {
                // 세부 정보를 보는 화면으로 이동한다.

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderShowShirt {
            val rowBinding = RowBinding.inflate(layoutInflater, parent, false)
            val viewHolderShowShirt = ViewHolderShowShirt(rowBinding)
            rowBinding.root.setOnClickListener(viewHolderShowShirt)
            return viewHolderShowShirt
        }

        override fun getItemCount(): Int {
            return imageArray.size
        }

        override fun onBindViewHolder(holder: ViewHolderShowShirt, position: Int) {
            holder.rowBinding.imageViewRow.setImageResource(imageArray[position])
            holder.rowBinding.textViewRow.text = strArray[position]
        }
    }
}