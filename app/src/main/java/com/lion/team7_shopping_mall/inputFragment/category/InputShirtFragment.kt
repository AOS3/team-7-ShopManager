package com.lion.team7_shopping_mall.inputFragment.category

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lion.team7_shopping_mall.MainActivity
import com.lion.team7_shopping_mall.R
import com.lion.team7_shopping_mall.databinding.FragmentInputShirtBinding
import com.lion.team7_shopping_mall.databinding.RowColorBinding
import java.io.File
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.core.widget.addTextChangedListener
import com.google.android.material.checkbox.MaterialCheckBox
import com.lion.team7_shopping_mall.inputFragment.temp


class InputShirtFragment : Fragment() {
    lateinit var fragmentInputShirtBinding: FragmentInputShirtBinding
    lateinit var mainActivity: MainActivity

    // 앨범 런처
    lateinit var albumLauncher: ActivityResultLauncher<Intent>
    // 원본 사진 찍기용 런처
    lateinit var originalCameraLauncher: ActivityResultLauncher<Intent>
    // 촬영된 사진이 위치할 경로
    lateinit var filePath:String
    // 저장된 파일에 접근하기 위한 Uri
    lateinit var contentUri: Uri
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //런처초기화
        settingLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentInputShirtBinding = FragmentInputShirtBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        //권한 호출
        requestPermissions(permissionList, 0)

        //색상고르기 설정
        settingRecyclerViewColorSelector()

        //사진가져오기세팅
        settingImageViewSetImage()

        //가격,재고수량 슬라이더
        settingSlider()

        //세부종류 버튼설정
        settingCategory()

        //사이즈선택버튼 초기화
        settingSnack()

        //사용자의 실시간 입력감지
        saveListener()
        return fragmentInputShirtBinding.root
    }

    //사용자의 실시간 입력감지
    fun saveListener() {
        // 텍스트 변경 감지 (EditText)
        fragmentInputShirtBinding.textInputLayoutName.editText?.addTextChangedListener {
            saveInTemp()  // 텍스트가 변경될 때마다 저장
        }

        // 슬라이더 값 변경 감지 (가격 슬라이더)
        fragmentInputShirtBinding.sliderPrice.addOnChangeListener { slider, value, fromUser ->
            saveInTemp()  // 슬라이더 값 변경 시 저장
        }

        // 슬라이더 값 변경 감지 (재고 슬라이더)
        fragmentInputShirtBinding.sliderCount.addOnChangeListener { slider, value, fromUser ->
            saveInTemp()  // 슬라이더 값 변경 시 저장
        }

        // 체크박스 상태 변경 감지 (전체 사이즈 체크박스)
        fragmentInputShirtBinding.checkBoxAll.addOnCheckedStateChangedListener { checkBox, state ->
            saveInTemp()  // 체크박스 상태 변경 시 저장
        }

        // 개별 사이즈 체크박스 상태 변경 감지
        fragmentInputShirtBinding.checkBoxXL.setOnCheckedChangeListener { _, _ -> saveInTemp() }
        fragmentInputShirtBinding.checkBoxL.setOnCheckedChangeListener { _, _ -> saveInTemp() }
        fragmentInputShirtBinding.checkBoxM.setOnCheckedChangeListener { _, _ -> saveInTemp() }
        fragmentInputShirtBinding.checkBoxS.setOnCheckedChangeListener { _, _ -> saveInTemp() }
        fragmentInputShirtBinding.checkBoxXS.setOnCheckedChangeListener { _, _ -> saveInTemp() }


        // ToggleGroup 상태 변경 감지
        fragmentInputShirtBinding.toggleGroupCategory.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.buttonHoodie -> saveInTemp()
                    R.id.buttonSweatShirt -> saveInTemp()
                    R.id.buttonShortSleeveTshirt -> saveInTemp()
                }
                saveInTemp() // 상태 저장
            }
        }
    }

    //임시저장소(temp)에 저장하기
    fun saveInTemp() {
        fragmentInputShirtBinding.apply {
            //타입
            val type = "shirt"
            Log.d("InputData", "종류: $type")
            // 상품명
            val name = textInputLayoutName.editText?.text.toString()
            Log.d("InputData", "상품명: $name")

            // 가격 슬라이더 값
            val price = sliderPrice.value.toInt()
            Log.d("InputData", "가격: $price")

            // 재고 슬라이더 값
            val stock = sliderCount.value.toInt()
            Log.d("InputData", "재고: $stock")

            val selectedCategory = when (toggleGroupCategory.checkedButtonId) {
                R.id.buttonHoodie -> "후드"
                R.id.buttonSweatShirt -> "맨투맨"
                R.id.buttonShortSleeveTshirt -> "반팔티셔츠"
                else -> "미선택"
            }
            Log.d("InputData", "종류: $selectedCategory")

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
            Log.d("InputData", "선택된 사이즈: $selectedSizes")

            // RecyclerView로 색상 정보 추출
            // selectColorList는 RecyclerView에서 선택된 색상 정보를 담는 List로 가정
            val selectedColors = selectColorList
            Log.d("InputData", "선택된 색상: $selectedColors")

            temp.name = name

        }
    }
    //런처 초기화
    private fun settingLauncher(){
        // 사진 가져오는 런처를 초기화
        albumLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleAlbumResult(result)
        }

        originalCameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleCameraResult(result)
        }
    }

    //사진가져오기 버튼 설정
    private fun settingImageViewSetImage(){
        fragmentInputShirtBinding.apply {
            // 외부 저장소 경로를 가져온다.
            filePath = mainActivity.getExternalFilesDir(null).toString()

            buttonGetImageFromCamera.setOnClickListener {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                // 촬영한 사진이 저장될 파일 이름
                val fileName = "/temp_${System.currentTimeMillis()}.jpg"
                // 경로 + 파일이름
                val picPath = "${filePath}${fileName}"
                val file = File(picPath)

                // 사진이 저장될 위치를 관리하는 Uri 객체를 생성ㅎ
                contentUri = FileProvider.getUriForFile(mainActivity, "com.lion.getpicture.file_provider", file)

                // Activity를 실행한다.
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
                originalCameraLauncher.launch(cameraIntent)
            }
        }

        //앨범에서 사진가져오는 버튼설정
        fragmentInputShirtBinding.apply {
            buttonGetImageFromGallery.setOnClickListener {
                val albumIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                // 이미지 타입을 설정한다.
                albumIntent.setType("image/*")
                // 선택할 파일의 타입을 지정(안드로이드 OS가 사전 작업을 할 수 있도록)
                val mimeType = arrayOf("image/*")
                albumIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
                // 액티비티 실행
                albumLauncher.launch(albumIntent)
            }
        }
    }

    //가격,재고수량 슬라이더
    private fun settingSlider(){
        fragmentInputShirtBinding.apply {
            sliderPrice.addOnChangeListener { slider, value, fromUser ->
                val formattedValue = String.format("%,d", value.toInt())
                textView5.text = "${formattedValue}원"
            }
            sliderCount.addOnChangeListener { slider, value, fromUser ->
                textView6.text = "${value.toInt()}개"
            }
        }
    }

    //세부종류 버튼설정
    private fun settingCategory() {
        fragmentInputShirtBinding.apply {
            buttonHoodie.setOnClickListener {
                if (toggleGroupCategory.checkedButtonId != buttonHoodie.id) {
                    toggleGroupCategory.check(buttonHoodie.id)
                }
            }
            buttonSweatShirt.setOnClickListener {
                if (toggleGroupCategory.checkedButtonId != buttonSweatShirt.id) {
                    toggleGroupCategory.check(buttonSweatShirt.id)
                }
            }
            buttonShortSleeveTshirt.setOnClickListener {
                if (toggleGroupCategory.checkedButtonId != buttonShortSleeveTshirt.id) {
                    toggleGroupCategory.check(buttonShortSleeveTshirt.id)
                }
            }
        }
    }

    //사이즈선택버튼 초기화
    private fun settingSnack() {
        fragmentInputShirtBinding.apply {
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

    private fun handleAlbumResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK && result.data?.data != null) {
            val uri = result.data?.data!!
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val source = ImageDecoder.createSource(mainActivity.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                BitmapFactory.decodeFile(uri.path)
            }
            fragmentInputShirtBinding.imageViewSetImage.setImageBitmap(bitmap)
        }
    }

    private fun handleCameraResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            val bitmap = BitmapFactory.decodeFile(contentUri.path)
            fragmentInputShirtBinding.imageViewSetImage.setImageBitmap(bitmap)

            // 파일 삭제
            val file = File(contentUri.path!!)
            file.delete()
        }
    }

    private fun settingRecyclerViewColorSelector(){
        fragmentInputShirtBinding.apply {
            // LinearLayoutManager로 설정, orientation을 horizontal로 설정
            recyclerViewColorSelector.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            recyclerViewColorSelector.adapter = RecyclerViewColorSelector(colorList)
        }
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