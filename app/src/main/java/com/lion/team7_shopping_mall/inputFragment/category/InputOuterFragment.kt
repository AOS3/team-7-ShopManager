package com.lion.team7_shopping_mall.inputFragment.category

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.lion.team7_shopping_mall.MainActivity
import com.lion.team7_shopping_mall.R
import com.lion.team7_shopping_mall.databinding.FragmentInputOuterBinding
import com.lion.team7_shopping_mall.databinding.RowColorBinding
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.core.widget.addTextChangedListener
import com.lion.team7_shopping_mall.inputFragment.InputFragment
import com.lion.team7_shopping_mall.inputFragment.temp
import com.lion.temp.util.ClothesCategoryName
import com.lion.temp.util.ClothesTypeByCategoryName

class InputOuterFragment(val inputFragment: InputFragment) : Fragment() {
    lateinit var fragmentInputOuterBinding: FragmentInputOuterBinding

    lateinit var mainActivity: MainActivity

    //사진가져오기권한
    val permissionList = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.ACCESS_MEDIA_LOCATION
    )


    // 색상 데이터 리스트
    private val colorList = listOf(
        Pair("#000000", "Black"),
        Pair("#FFFFFF", "White"),
        Pair("#FF0000", "Red"),
        Pair("#00FF00", "Green"),
        Pair("#0000FF", "Blue"),
        Pair("#FFFF00", "Yellow"),
        Pair("#FFA500", "Orange"),
        Pair("#800080", "Purple"),
        Pair("#FFC0CB", "Pink"),
        Pair("#808080", "Gray")
    )

    private var selectColorList = mutableListOf<String>() // 선택된 색상 리스트

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentInputOuterBinding = FragmentInputOuterBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        //권한 호출
        requestPermissions(permissionList, 0)

        //색상고르기 설정
        settingRecyclerViewColorSelector()

        //가격,재고수량 슬라이더
        settingSlider()

        //세부종류 버튼설정
        settingCategory()

        //사이즈선택버튼 초기화
        settingSnack()

        saveListener()

        //앨범에서 사진가져오기
        setImage()

        hideKeyboard()

        return fragmentInputOuterBinding.root
    }
    //키보드 내리기처리
    fun hideKeyboard(){
        // 최상위 레이아웃에 터치 리스너 추가
        val rootLayout = fragmentInputOuterBinding.root // 루트 레이아웃
        rootLayout.setOnTouchListener { _, _ ->
            mainActivity.hideSoftInput() // 키보드를 내리는 메서드 호출
            false
        }

        fragmentInputOuterBinding.textInputLayoutName.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mainActivity.hideSoftInput() // 키보드를 내리는 메서드 호출
                true // 이벤트를 처리했음을 반환
            } else{
                true
            }
        }
    }

    //사용자의 실시간 입력감지
    fun saveListener() {
        // 텍스트 변경 감지 (EditText)
        fragmentInputOuterBinding.textInputLayoutName.editText?.addTextChangedListener {
            saveInTemp()  // 텍스트가 변경될 때마다 저장
        }

        // 슬라이더 값 변경 감지 (가격 슬라이더)
        fragmentInputOuterBinding.sliderPrice.addOnChangeListener { slider, value, fromUser ->
            saveInTemp()  // 슬라이더 값 변경 시 저장
        }

        // 슬라이더 값 변경 감지 (재고 슬라이더)
        fragmentInputOuterBinding.sliderCount.addOnChangeListener { slider, value, fromUser ->
            saveInTemp()  // 슬라이더 값 변경 시 저장
        }

        // 체크박스 상태 변경 감지 (전체 사이즈 체크박스)
        fragmentInputOuterBinding.checkBoxAll.addOnCheckedStateChangedListener { checkBox, state ->
            saveInTemp()  // 체크박스 상태 변경 시 저장
        }

        // 개별 사이즈 체크박스 상태 변경 감지
        fragmentInputOuterBinding.checkBoxXL.setOnCheckedChangeListener { _, _ -> saveInTemp() }
        fragmentInputOuterBinding.checkBoxL.setOnCheckedChangeListener { _, _ -> saveInTemp() }
        fragmentInputOuterBinding.checkBoxM.setOnCheckedChangeListener { _, _ -> saveInTemp() }
        fragmentInputOuterBinding.checkBoxS.setOnCheckedChangeListener { _, _ -> saveInTemp() }
        fragmentInputOuterBinding.checkBoxXS.setOnCheckedChangeListener { _, _ -> saveInTemp() }

        // ToggleGroup 상태 변경 감지
        fragmentInputOuterBinding.toggleGroupCategory.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.buttonCoat -> saveInTemp()
                    R.id.buttonLongPadding -> saveInTemp()
                    R.id.buttonShortPadding -> saveInTemp()
                }
                saveInTemp() // 상태 저장
            }
        }

    }

    //임시저장소(temp)에 저장하기
    fun saveInTemp() {
        fragmentInputOuterBinding.apply {
            // 상품명
            val name = textInputLayoutName.editText?.text.toString()

            // 가격 슬라이더 값
            val price = sliderPrice.value.toInt()

            // 재고 슬라이더 값
            val inventory = sliderCount.value.toInt()

            val selectedTypeByCategory = when (toggleGroupCategory.checkedButtonId) {
                R.id.buttonCoat -> ClothesTypeByCategoryName.COAT.str
                R.id.buttonLongPadding -> ClothesTypeByCategoryName.LONG_PADDING.str
                R.id.buttonShortPadding -> ClothesTypeByCategoryName.SHORT_PADDING.str
                else -> "미선택"
            }

            // 사이즈 선택 리스트
            val selectedSizes = mutableListOf<String>()

            // "전체" 체크박스가 체크된 경우
            if (checkBoxAll.isChecked) {
                selectedSizes.addAll(listOf("XL", "L", "M", "S", "XS"))
            } else {
                // 개별 체크박스 상태 확인
                if (checkBoxXL.isChecked) selectedSizes.add("XL")
                if (checkBoxL.isChecked) selectedSizes.add("L")
                if (checkBoxM.isChecked) selectedSizes.add("M")
                if (checkBoxS.isChecked) selectedSizes.add("S")
                if (checkBoxXS.isChecked) selectedSizes.add("XS")
            }

            // RecyclerView로 색상 정보 추출
            // selectColorList는 RecyclerView에서 선택된 색상 정보를 담는 List로 가정
            val selectedColors = selectColorList

            temp.clothesCategory = ClothesCategoryName.OUTER.str
            temp.clothesName = name
            temp.clothesPrice = price
            temp.clothesInventory = inventory
            temp.clothesTypeByCategory = selectedTypeByCategory

            temp.clothesColor = selectedColors.toString()
            temp.clothesSize = selectedSizes.toString()

            Log.d(
                "test",
                """
    clothesPicture: ${temp.clothesPicture}
    clothesName: ${temp.clothesName}
    clothesPrice: ${temp.clothesPrice}
    clothesInventory: ${temp.clothesInventory}
    clothesColor: ${temp.clothesColor}
    clothesSize: ${temp.clothesSize}
    clothesCategory: ${temp.clothesCategory}
    clothesTypeByCategory: ${temp.clothesTypeByCategory}
    """.trimIndent()
            )


//            val clothesColorString = "[XL, L, M]" // 저장된 문자열
//            val clothesColorList = clothesColorString
//                .removeSurrounding("[", "]") // 대괄호 제거
//                .split(", ") // 콤마와 공백으로 분리

        }
    }

    //세부종류 버튼설정
    private fun settingCategory() {
        fragmentInputOuterBinding.apply {
            buttonCoat.setOnClickListener {
                if (toggleGroupCategory.checkedButtonId != buttonCoat.id) {
                    toggleGroupCategory.check(buttonCoat.id)
                }
            }
            buttonLongPadding.setOnClickListener {
                if (toggleGroupCategory.checkedButtonId != buttonLongPadding.id) {
                    toggleGroupCategory.check(buttonLongPadding.id)
                }
            }
            buttonShortPadding.setOnClickListener {
                if (toggleGroupCategory.checkedButtonId != buttonShortPadding.id) {
                    toggleGroupCategory.check(buttonShortPadding.id)
                }
            }
        }
    }

    //사이즈선택버튼 초기화
    private fun settingSnack() {
        fragmentInputOuterBinding.apply {
            // 이벤트 동작 여부 변수
            var isParentUpdating = true
            var isChildrenUpdating = true
            // 전체 체크 박스
            // 체크 상태가 변경되었을 때
            // state : 체크 상태값
            checkBoxAll.addOnCheckedStateChangedListener { checkBox, state ->

                // 동물 체크박스의 이벤트 동작을 막았다면 종료시킨다
                if (isParentUpdating == false) {
                    return@addOnCheckedStateChangedListener
                }

                // 하위 체크박스들의 이벤트 동작을 막아준다.
                isChildrenUpdating = false

                when (state) {
                    // 체크 상태일 때
                    MaterialCheckBox.STATE_CHECKED -> {
                        // 모든 체크박스를 체크한다.
                        checkBoxXL.isChecked = true
                        checkBoxL.isChecked = true
                        checkBoxM.isChecked = true
                        checkBoxS.isChecked = true
                        checkBoxXS.isChecked = true
                    }
                    // 체크 상태가 아닐 때
                    MaterialCheckBox.STATE_UNCHECKED -> {
                        // 모든 체크스를 체크 해제한다.
                        checkBoxXL.isChecked = false
                        checkBoxL.isChecked = false
                        checkBoxM.isChecked = false
                        checkBoxS.isChecked = false
                        checkBoxXS.isChecked = false
                    }
                }

                // 하위 체크박스들의 리스너 동작을 풀어준다.
                isChildrenUpdating = true
            }

            // 체크박스들에 설정할 이벤트 람다식
            val checkBoxListener = OnCheckedChangeListener { buttonView, isChecked ->

                // 하위 체크박스의 리스너 동작을 막았다면 중단시킨다
                if (isChildrenUpdating == false) {
                    return@OnCheckedChangeListener
                }

                // 동물 체크박스의 리스너 동작을 막아준다.
                isParentUpdating = false

                // 체크되어 있는 체크박스의 개수를 담을 변수
                var checkedCount = 0

                // 체크박스들을 검사한다.
                if (checkBoxXL.isChecked) {
                    checkedCount++
                }
                if (checkBoxL.isChecked) {
                    checkedCount++
                }
                if (checkBoxM.isChecked) {
                    checkedCount++
                }
                if (checkBoxS.isChecked) {
                    checkedCount++
                }
                if (checkBoxXS.isChecked) {
                    checkedCount++
                }

                // 체크박스 개수에 따라 상태를 설정한다.
                checkBoxAll.checkedState = if (checkedCount == 0) {
                    MaterialCheckBox.STATE_UNCHECKED
                } else if (checkedCount == 3) {
                    MaterialCheckBox.STATE_CHECKED
                } else {
                    MaterialCheckBox.STATE_INDETERMINATE
                }

                // 동물 체크박스의 리스너 동작을 허용한다.
                isParentUpdating = true
            }
            // 모든 체크박스에 설정한다
            checkBoxXL.setOnCheckedChangeListener(checkBoxListener)
            checkBoxL.setOnCheckedChangeListener(checkBoxListener)
            checkBoxM.setOnCheckedChangeListener(checkBoxListener)
            checkBoxS.setOnCheckedChangeListener(checkBoxListener)
            checkBoxXS.setOnCheckedChangeListener(checkBoxListener)

        }
    }

    //가격,재고수량 슬라이더
    private fun settingSlider() {
        fragmentInputOuterBinding.apply {
            sliderPrice.addOnChangeListener { slider, value, fromUser ->
                val formattedValue = String.format("%,d", value.toInt())
                textView5.text = "${formattedValue}원"
            }
            sliderCount.addOnChangeListener { slider, value, fromUser ->

                textView6.text = "${value.toInt()}개"
            }
        }
    }

    //색상고르기 설정
    private fun settingRecyclerViewColorSelector() {
        fragmentInputOuterBinding.apply {
            // LinearLayoutManager로 설정, orientation을 horizontal로 설정
            recyclerViewColorSelector.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            recyclerViewColorSelector.adapter = RecyclerViewColorSelector(colorList)
        }
    }

    //앨범에서 사진가져오기
    private fun setImage() {
        fragmentInputOuterBinding.apply {
            buttonGetImageFromGallery.setOnClickListener {
                val albumIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                albumIntent.setType("image/*")
                val mimeType = arrayOf("image/*")
                albumIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
                inputFragment.albumLauncher.launch(albumIntent)
            }

            buttonGetImageFromCamera.setOnClickListener {
                inputFragment.takePictureByCamera()
            }
        }
    }

    fun setImageBitmap(bitmap: Bitmap) {
        fragmentInputOuterBinding.imageViewSetImage.setImageBitmap(bitmap)
        saveInTemp()
    }


    inner class RecyclerViewColorSelector(
        private val colors: List<Pair<String, String>> // 색상 데이터 전달
    ) : RecyclerView.Adapter<RecyclerViewColorSelector.ViewHolder>() {

        inner class ViewHolder(private val binding: RowColorBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(color: Pair<String, String>) {
                binding.colorView.setBackgroundColor(Color.parseColor(color.first)) // 배경색 설정

                // 색상 리스트에 해당 색상이 존재하면 "O"로 표시하고, 선택 상태로 만듦
                if (selectColorList.contains(color.second)) {
                    binding.selectionIndicator.text = "O"
                    binding.selectionIndicator.setBackgroundResource(R.drawable.border_active)
                } else {
                    binding.selectionIndicator.text = "X"
                    binding.selectionIndicator.setBackgroundResource(R.drawable.border_inactive)
                }

                // 클릭 리스너로 선택 상태 변경
                binding.root.setOnClickListener {
                    val isSelected = binding.selectionIndicator.text == "O"
                    if (isSelected) {
                        // 선택 해제
                        binding.selectionIndicator.text = "X"
                        binding.selectionIndicator.setBackgroundResource(R.drawable.border_inactive)
                        selectColorList.remove(color.second) // 색상 리스트에서 제거
                    } else {
                        // 선택
                        binding.selectionIndicator.text = "O"
                        binding.selectionIndicator.setBackgroundResource(R.drawable.border_active)
                        selectColorList.add(color.second) // 색상 리스트에 추가
                    }

                    // 상태 변화 로그
                    Log.d("test100", selectColorList.toString())
                    saveInTemp()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = RowColorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun getItemCount(): Int = colors.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(colors[position])
        }
    }


}