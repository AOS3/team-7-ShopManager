package com.lion.team7_shopping_mall.showfragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lion.team7_shopping_mall.MainActivity
import com.lion.team7_shopping_mall.R
import com.lion.team7_shopping_mall.databinding.DialogClothesInoutBinding
import com.lion.team7_shopping_mall.databinding.FragmentShowClothesBinding
import com.lion.team7_shopping_mall.databinding.FragmentShowMainBinding
import com.lion.team7_shopping_mall.repository.ClothesInOutHistoryRepository
import com.lion.team7_shopping_mall.repository.ClothesRepository
import com.lion.team7_shopping_mall.viewmodel.ClothesInOutHistoryViewModel
import com.lion.team7_shopping_mall.viewmodel.ClothesViewModel
import com.lion.temp.util.FragmentName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Calendar

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
        settingButtonShowInventoryInput()
        settingButtonShowInventoryOutput()

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


    //////////////////////////////////입출력 버튼 부터 시작하면 됨///////////////////////////////////////\

    // 버튼을 통해 입고 내용을 등록한다
    fun settingButtonShowInventoryInput() {
        fragmentShowClothesBinding.apply {
            buttonShowInventoryInput.setOnClickListener {
                val builder1 = MaterialAlertDialogBuilder(mainActivity)
                builder1.setTitle("입고 내용 입력")
                // 다이얼로그를 커스터마이징 할 때는 지정한 View가 Message 위치에 나오므로
                // Message를 설정하지 않는다.
                // builder1.setMessage("기본 다이얼로그 입니다")
                builder1.setIcon(R.mipmap.ic_launcher)

                //설정할 View에 대한 작업을 한다.
                val dialogClothesInoutBinding = DialogClothesInoutBinding.inflate(layoutInflater)
                dialogClothesInoutBinding.textFieldClothesInOutCount.editText?.setText("")
                dialogClothesInoutBinding.textFieldClothesInOutPrice.editText?.setText("")

                // View를 Dialog에 설정해준다.
                builder1.setView(dialogClothesInoutBinding.root)

                // Dialog 생성
                val dialog = builder1.create()

                // Positive 버튼 설정
                dialogClothesInoutBinding.buttonDialogInput.setOnClickListener {
                    val inputCount = dialogClothesInoutBinding.textFieldClothesInOutCount.editText?.text.toString()
                    val inputPrice = dialogClothesInoutBinding.textFieldClothesInOutPrice.editText?.text.toString()

                    var inputFlag = true

                    // 에러 메시지 표시
                    if (inputCount.isEmpty()) {
                        dialogClothesInoutBinding.textFieldClothesInOutCount.error = "수량을 입력하세요"
                        inputFlag = false
                    } else {
                        dialogClothesInoutBinding.textFieldClothesInOutCount.error = null
                    }

                    if (inputPrice.isEmpty()) {
                        dialogClothesInoutBinding.textFieldClothesInOutPrice.error = "가격을 입력하세요"
                        inputFlag = false
                    } else {
                        dialogClothesInoutBinding.textFieldClothesInOutPrice.error = null
                    }

                    // 모든 입력이 유효한 경우에만 작업 수행 및 Dialog 닫기
                    if (inputFlag) {
                        getDataForUpdateInventory(inputCount.toInt(), inputPrice.toInt(), "+")
                        dialog.dismiss() // Dialog 닫기
                    }
                }

                // Negative 버튼 설정
                dialogClothesInoutBinding.buttonDialogCancel.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()
            }
        }
    }

    // 버튼을 통해 출고 내용을 등록한다
    fun settingButtonShowInventoryOutput() {
        fragmentShowClothesBinding.apply {
            buttonShowInventoryOutput.setOnClickListener {
                val builder1 = MaterialAlertDialogBuilder(mainActivity)
                builder1.setTitle("출고 내용 입력")
                // 다이얼로그를 커스터마이징 할 때는 지정한 View가 Message 위치에 나오므로
                // Message를 설정하지 않는다.
                // builder1.setMessage("기본 다이얼로그 입니다")
                builder1.setIcon(R.mipmap.ic_launcher)

                //설정할 View에 대한 작업을 한다.
                val dialogClothesInoutBinding = DialogClothesInoutBinding.inflate(layoutInflater)
                dialogClothesInoutBinding.textFieldClothesInOutCount.editText?.setText("")
                dialogClothesInoutBinding.textFieldClothesInOutPrice.editText?.setText("")

                // View를 Dialog에 설정해준다.
                builder1.setView(dialogClothesInoutBinding.root)

                // Dialog 생성
                val dialog = builder1.create()

                // Positive 버튼 설정
                dialogClothesInoutBinding.buttonDialogInput.setOnClickListener {
                    val inputCount = dialogClothesInoutBinding.textFieldClothesInOutCount.editText?.text.toString()
                    val inputPrice = dialogClothesInoutBinding.textFieldClothesInOutPrice.editText?.text.toString()

                    var inputFlag = true

                    // 에러 메시지 표시
                    if (inputCount.isEmpty()) {
                        dialogClothesInoutBinding.textFieldClothesInOutCount.error = "수량을 입력하세요"
                        inputFlag = false
                    } else {
                        dialogClothesInoutBinding.textFieldClothesInOutCount.error = null
                    }

                    if (inputPrice.isEmpty()) {
                        dialogClothesInoutBinding.textFieldClothesInOutPrice.error = "가격을 입력하세요"
                        inputFlag = false
                    } else {
                        dialogClothesInoutBinding.textFieldClothesInOutPrice.error = null
                    }

                    // 모든 입력이 유효한 경우에만 작업 수행 및 Dialog 닫기
                    if (inputFlag) {
                        getDataForUpdateInventory(inputCount.toInt(), inputPrice.toInt(), "-")
                        dialog.dismiss() // Dialog 닫기
                    }
                }

                // Negative 버튼 설정
                dialogClothesInoutBinding.buttonDialogCancel.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()
            }
        }
    }


    // 업데이트를 위해 옷정보를 가져오는 메소드
    fun getDataForUpdateInventory(inoutCount: Int, inoutPrice: Int, isCheckedInOut: String) {
        fragmentShowClothesBinding.apply {
            // 옷 번호를 가져온다.
            val clothesIndex = 2
            // 정보를 가져와서 담을 모델
            var clothesModel: ClothesViewModel
            // 입출고 내역을 담을 모델
            var clothesInOutHistoryViewModel : ClothesInOutHistoryViewModel


            // 옷 정보를 가져온다
            // 수정을 할때 특정 값만 수정하는게 불가능 하기 때문에 전체 정보를 가져오고 재고만 수정하여
            // 업데이트 해야한다
            CoroutineScope(Dispatchers.Main).launch {
                val work1 = async(Dispatchers.IO) {
                    // 학생 번호를 가져온다.
                    // val studentIdx = arguments?.getInt("studentIdx")

                    // 옷 데이터를 가져온다.
                    ClothesRepository.selectClothesInfoByClothesIdx(mainActivity, clothesIndex)
                }
                clothesModel = work1.await()

                val clothesInventory = when(isCheckedInOut) {
                    "+" -> clothesModel.clothesInventory + inoutCount
                    else -> clothesModel.clothesInventory - inoutCount
                }
                
                val clothesViewModel = ClothesViewModel(
                    clothesModel.clothesIdx,
                    clothesModel.clothesPicture,
                    clothesModel.clothesName,
                    clothesModel.clothesPrice,
                    clothesInventory,
                    clothesModel.clothesColor,
                    clothesModel.clothesSize,
                    clothesModel.clothesCategory,
                    clothesModel.clothesTypeByCategory
                )

                // 현재 날짜 가져오기
                val calendar = Calendar.getInstance()

                // 년, 월, 일 추출
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH) + 1 // 0부터 시작하므로 1을 더함
                val date = calendar.get(Calendar.DAY_OF_MONTH)

                // 현재 시각 정보 가져오기
                val hour = calendar.get(Calendar.HOUR_OF_DAY) // 24시간 형식
                val minute = calendar.get(Calendar.MINUTE)
                val second = calendar.get(Calendar.SECOND)

                // 입고 내역인지 출고 내역인지 저장
                val clothesInOutHistoryCheckInOut = when(isCheckedInOut) {
                    "+" -> "입고"
                    else -> "출고"
                }

                // 옷 입출고 내역을 viewModel 에 저장한다
                clothesInOutHistoryViewModel = ClothesInOutHistoryViewModel(
                    0,
                    // 옷의 입출고 내역을 저장할 때 사용할 이름을 해당 옷의 이름으로 저장
                    clothesModel.clothesName,
                    clothesInOutHistoryCheckInOut,
                    inoutCount,
                    inoutPrice,
                    year,
                    month,
                    date,
                    hour,
                    minute,
                    second
                )

                updateClothesInventoryCount(clothesViewModel, clothesInOutHistoryViewModel)
            }
        }
    }

    // 입고 또는 출고된 내용을 옷 정보에 반영 및 저장
    fun updateClothesInventoryCount(clothesViewModel: ClothesViewModel, clothesInOutHistoryViewModel: ClothesInOutHistoryViewModel) {
        CoroutineScope(Dispatchers.Main).launch {
            val work1 = async(Dispatchers.IO) {
                ClothesRepository.updateClothesInfo(mainActivity, clothesViewModel)
            }
            work1.join()

            val work2 = async(Dispatchers.IO) {
                ClothesInOutHistoryRepository.insertClothesInOutHistoryInfo(mainActivity,clothesInOutHistoryViewModel)
            }
            work2.join()

            Toast.makeText(mainActivity, "입고 내용 저장 완료", Toast.LENGTH_SHORT).show()
            settingImageViewTextView()
            val work3 = async(Dispatchers.IO) {
                ClothesRepository.selectClothesInfoAll(mainActivity)
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun settingButtonShowInOutHistory() {
        fragmentShowClothesBinding.apply {
            buttonShowInOutHistory.setOnClickListener {
                val dataBundle = Bundle()
                dataBundle.putString("clothesInOutHistoryName", textViewShowClothesName.text.toString())
                showMainFragment.replaceShowFragment(ShowFragmentName.SHOW_INOUT_FRAGMENT, true, true, dataBundle)
            }
        }
    }

    fun settingImageViewTextView() {
        fragmentShowClothesBinding.apply {
            CoroutineScope(Dispatchers.Main).launch {
                val work1 = async(Dispatchers.IO) {
                    // 학생 번호를 가져온다.
                    // val studentIdx = arguments?.getInt("studentIdx")
                    // 테스트용
                    val clothesIdx = 2

                    // 학생 데이터를 가져온다.
                    ClothesRepository.selectClothesInfoByClothesIdx(mainActivity, clothesIdx)
                }
                val clothesModel = work1.await()

                Log.d("clothesModel", "${clothesModel}")

                imageViewShowClothesImage.setImageResource(R.drawable.padding_image)
                textViewShowClothesName.text = clothesModel.clothesName
                textViewShowClothesPrice.setText("${clothesModel.clothesPrice} 원")
                textViewSHowClothesInventory.setText("${clothesModel.clothesInventory} 개")
                textViewSHowClothesColor.setText(clothesModel.clothesColor)
                textViewSHowClothesSize.setText(clothesModel.clothesSize)
                textViewSHowClothesTypeByCategory.setText(clothesModel.clothesTypeByCategory)
            }

        }
    }

}