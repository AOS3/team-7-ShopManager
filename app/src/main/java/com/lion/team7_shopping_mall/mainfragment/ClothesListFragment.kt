package com.lion.team7_shopping_mall.mainfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.lion.team7_shopping_mall.MainActivity
import com.lion.team7_shopping_mall.R
import com.lion.team7_shopping_mall.databinding.FragmentClothesListBinding


class ClothesListFragment(val mainFragment: MainFragment) : Fragment() {


    lateinit var fragmentClothesListBinding: FragmentClothesListBinding
    lateinit var mainActivity: MainActivity


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        fragmentClothesListBinding = FragmentClothesListBinding.inflate(inflater)
        mainActivity = activity as MainActivity


        // Toolbar를 구성하는 메서드 호출
        settingToolbar()
        // FAB를 구성하는 메서드 호출
        settingFAB()
        // ViewPager2의 어뎁터를 설정한다
        settingPager()

        return fragmentClothesListBinding.root
    }


    // Toolbar를 구성하는 메서드
    fun settingToolbar(){
        fragmentClothesListBinding.apply {
            // 타이틀
            toolbarMain.title = "물건 정보 보기"


            toolbarMain.inflateMenu(R.menu.toolbar_clothes_list_menu)
            toolbarMain.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.clothes_menu_search -> {
                        // 검색 화면으로 이동
                        mainFragment.replaceFragment(SubFragmentName.SEARCH_CLOTHES_FRAGMENT,true,true,null)
                    }
                }
                true
            }
        }
    }

    // fab를 구성하는 메서드
    fun settingFAB(){
        fragmentClothesListBinding.apply {
            fabMainAdd.setOnClickListener {
                // InputFragment로 이동. (입력 화면으로 이동)

            }
        }
    }

    fun settingPager(){
        fragmentClothesListBinding.apply {
            // ViewPager2의 어뎁터를 설정한다.
            // 프래그먼트 클래스에서 supportFragmentManager 사용 불가 -> childFragmentManager로 재설정
            pager.adapter = ViewPagerAdapter(childFragmentManager, lifecycle)

            // TabLayout과 ViewPager2 연결
            val tabNames = listOf("All", "아우터", "셔츠", "치마", "바지")

            // TabLayout과 ViewPager2가 상호 작용을 할 수 있도록 연동시켜준다.
            val tabLayoutMediator = TabLayoutMediator(tabLayout, pager)
            { tab, position ->
                tab.text = tabNames[position]
            }
            tabLayoutMediator.attach()
        }
    }

    // ViewPager2의 어댑터
    inner class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
        // ViewPager2를 통해 보여줄 프래그먼트의 개수 (기본,아우터, 셔츠,치마,바지)
        override fun getItemCount(): Int {
            return 5
        }

        override fun createFragment(position: Int): Fragment {
            val newFragment = when (position) {
                0 -> MainShowAllFragment()
                1 -> MainShowOuterFragment()
                2 -> MainShowShirtFragment()
                3 -> MainShowSkirtFragment()
                else -> MainShowPantsFragment()
            }
            return newFragment
        }
    }

}

