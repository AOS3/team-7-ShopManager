package com.lion.team7_shopping_mall.modifyFragment.category

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.lion.team7_shopping_mall.MainActivity
import com.lion.team7_shopping_mall.R
import com.lion.team7_shopping_mall.database.ClothesDatabase
import com.lion.team7_shopping_mall.databinding.FragmentModifyPantsBinding
import com.lion.team7_shopping_mall.databinding.RowColorBinding
import com.lion.team7_shopping_mall.inputFragment.temp
import com.lion.team7_shopping_mall.modifyFragment.ModifyFragment
import com.lion.team7_shopping_mall.repository.ClothesRepository
import com.lion.team7_shopping_mall.util.ColorEnum
import com.lion.team7_shopping_mall.viewmodel.ClothesViewModel
import com.lion.temp.util.ClothesCategoryName
import com.lion.temp.util.ClothesTypeByCategoryName
import com.lion.temp.util.FragmentName
import com.lion.temp.util.ModifyFragmentName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import android.widget.CompoundButton.OnCheckedChangeListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.lion.team7_shopping_mall.databinding.FragmentModifyShirtBinding
import com.lion.team7_shopping_mall.repository.ClothesInOutHistoryRepository

class ModifyShirtFragment(val mainFragment: ModifyFragment) : Fragment() {
    lateinit var fragmentModifyShirtBinding: FragmentModifyShirtBinding
    lateinit var mainActivity: MainActivity
    private var selectColorList = mutableListOf<String>() // 선택된 색상 리스트

    private var selectedColorDB = mutableListOf<String>() //DB에서 가져온 색상리스트 전역변수

    //앨범에서 가져오는것 관련
    lateinit var albumLauncher: ActivityResultLauncher<Intent>
    var real: String =""

    //카메라에서 가져오는것 관련
    // 원본 사진 찍기용 런처
    lateinit var originalCameraLauncher: ActivityResultLauncher<Intent>

    // 촬영된 사진이 위치할 경로
    lateinit var filePath:String
    // 저장된 파일에 접근하기 위한 Uri
    lateinit var contentUri: Uri

    var pictureFromCamera: String =""
    //수정할 옷의 idx
    var idx: Int? = null

    // 옷 입출고 내역 변경할 이름/////////////////////////////////////////////////////////////////////////////
    var clothesInOutHistoryName:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 뒤로가기 콜백 등록
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 뒤로가기 키가 눌렸을 때 수행할 작업
                mainFragment.removeFragment(ModifyFragmentName.MODIFY_OUTER_FRAGMENT)
                mainActivity.removeFragment(FragmentName.MODIFY_FRAGMENT)
                temp.clear()

            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentModifyShirtBinding = FragmentModifyShirtBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        gettingIdx()
        createAlbumLauncher() // 앨범 관련 초기화
        createOriginalCameraLauncher() // 카메라 런처 초기화
        settingRecyclerViewColorSelector()
        settingModifyDone()
        settingOriginalValue()
        // 외부 저장소 경로를 가져온다.
        filePath = mainActivity.getExternalFilesDir(null).toString()
        //세부종류 버튼설정
        settingCategory()

        //사이즈선택버튼 초기화
        settingSnack()

        //사용자의 실시간 입력감지
        saveListener()

        //처음에 들어왔을때 사진주소를 temp에저장
        //(사진빼고 다른것만 수정했을경우)
        settingPrimaryInfoInTemp()

        //가격,재고수량 슬라이더
        settingSlider()

        //앨범에서 사진가져오기
        setImage()


        return fragmentModifyShirtBinding.root
    }

    private fun gettingIdx(){
        if (arguments != null){
            idx = arguments?.getInt("ClothesIDX")!!
        }
    }

    //앨범에서 사진가져오기
    private fun setImage() {
        fragmentModifyShirtBinding.apply {
            buttonGetImageFromGallery.setOnClickListener {
                val albumIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                albumIntent.setType("image/*")
                val mimeType = arrayOf("image/*")
                albumIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
                albumLauncher.launch(albumIntent)
            }

            buttonGetImageFromCamera.setOnClickListener {
                takePictureByCamera()
            }
        }
    }

    //처음에 들어왔을때 사진주소를 temp에저장
    //(사진빼고 다른것만 수정했을경우)
    fun settingPrimaryInfoInTemp() {
        CoroutineScope(Dispatchers.Main).launch {
            val work = async(Dispatchers.IO) {
                ClothesRepository.selectClothesInfoByClothesIdx(mainActivity, idx!!)
            }
            val dataModel = work.await()


            val uri = dataModel.clothesPicture
            temp.clothesPicture = uri

            Log.d("test", temp.clothesPicture)



        }
    }

    //사용자의 실시간 입력감지
    fun saveListener() {
        fragmentModifyShirtBinding.apply {
            // 텍스트 변경 감지 (EditText)
            textInputLayoutName.editText?.addTextChangedListener {
                saveInTemp()  // 텍스트가 변경될 때마다 저장
            }

            // 슬라이더 값 변경 감지 (가격 슬라이더)
            sliderPrice.addOnChangeListener { slider, value, fromUser ->
                saveInTemp()  // 슬라이더 값 변경 시 저장
            }

            // 슬라이더 값 변경 감지 (재고 슬라이더)
            sliderCount.addOnChangeListener { slider, value, fromUser ->
                saveInTemp()  // 슬라이더 값 변경 시 저장
            }

            // 체크박스 상태 변경 감지 (전체 사이즈 체크박스)
            checkBoxAll.addOnCheckedStateChangedListener { checkBox, state ->
                saveInTemp()  // 체크박스 상태 변경 시 저장
            }

            // 개별 사이즈 체크박스 상태 변경 감지
            checkBoxXL.setOnCheckedChangeListener { _, _ -> saveInTemp() }
            checkBoxL.setOnCheckedChangeListener { _, _ -> saveInTemp() }
            checkBoxM.setOnCheckedChangeListener { _, _ -> saveInTemp() }
            checkBoxS.setOnCheckedChangeListener { _, _ -> saveInTemp() }
            checkBoxXS.setOnCheckedChangeListener { _, _ -> saveInTemp() }

            // ToggleGroup 상태 변경 감지
            toggleGroupCategory.addOnButtonCheckedListener { group, checkedId, isChecked ->
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
    }

    //임시저장소(temp)에 저장하기
    private fun saveInTemp() {
        fragmentModifyShirtBinding.apply {
            // 상품명
            val name = textInputLayoutName.editText?.text.toString()

            // 가격 슬라이더 값
            val price = sliderPrice.value.toInt()

            // 재고 슬라이더 값
            val inventory = sliderCount.value.toInt()

            val selectedTypeByCategory = when (toggleGroupCategory.checkedButtonId) {
                R.id.buttonHoodie -> ClothesTypeByCategoryName.HOODIE.str
                R.id.buttonSweatShirt -> ClothesTypeByCategoryName.SWEAT_SHIRT.str
                R.id.buttonShortSleeveTshirt -> ClothesTypeByCategoryName.SHORT_SLEEVE_T_SHIRT.str
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

            temp.clothesCategory = ClothesCategoryName.SHIRT.str
            temp.clothesName = name
            temp.clothesPrice = price
            temp.clothesInventory = inventory
            temp.clothesTypeByCategory = selectedTypeByCategory

            temp.clothesColor = selectedColors.toString()
            temp.clothesSize = selectedSizes.toString()

            Log.d(
                "test(temp)",
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
        }
    }

    //원래 값을 화면구성함수
    fun settingOriginalValue() {
        fragmentModifyShirtBinding.apply {

            Log.d("test",idx.toString())

            CoroutineScope(Dispatchers.Main).launch {
                var clothesViewModel: ClothesViewModel
                val work = async(Dispatchers.IO) {
                    ClothesRepository.selectClothesInfoByClothesIdx(mainActivity, idx!!)
                }
                clothesViewModel = work.await()


                withContext(Dispatchers.Main) {
                    textInputLayoutName.editText?.setText(clothesViewModel.clothesName)
                    sliderPrice.value = clothesViewModel.clothesPrice.toFloat()
                    sliderCount.value = clothesViewModel.clothesInventory.toFloat()


                    when (clothesViewModel.clothesTypeByCategory) {
                        ClothesTypeByCategoryName.HOODIE.str -> toggleGroupCategory.check(R.id.buttonHoodie)
                        ClothesTypeByCategoryName.SWEAT_SHIRT.str -> toggleGroupCategory.check(R.id.buttonSweatShirt)
                        ClothesTypeByCategoryName.SHORT_SLEEVE_T_SHIRT.str -> toggleGroupCategory.check(R.id.buttonShortSleeveTshirt)
                    }

                    val sizeList = parseStringToList(clothesViewModel.clothesSize)
                    checkBoxAll.isChecked = sizeList.size == 5
                    checkBoxXL.isChecked = "XL" in sizeList
                    checkBoxL.isChecked = "L" in sizeList
                    checkBoxM.isChecked = "M" in sizeList
                    checkBoxS.isChecked = "S" in sizeList
                    checkBoxXS.isChecked = "XS" in sizeList

                    val selectedColorDB = parseStringToList(clothesViewModel.clothesColor)
                    initializeSelectedColors(selectedColorDB)

                    //사진가져오기
                    val uri = clothesViewModel.clothesPicture
//                    Log.d("test", uri)
                    if (uri != null) {
                        Glide.with(mainActivity)
                            .asBitmap()  // 이미지를 Bitmap으로 로드
                            .load("file://$uri")  // 이미지 URI 로드
                            .override(1000)  // 가로 1000으로 먼저 설정
                            .into(object : SimpleTarget<Bitmap>() {
                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    // 이미지가 로드된 후, Bitmap에서 너비와 높이를 가져옴
                                    val width = resource.width
                                    val height = resource.height

                                    // 픽셀 비율 계산 (가로:세로 비율)
                                    val aspectRatio = width.toFloat() / height.toFloat()
//                                    Log.d(
//                                        "Image Aspect Ratio",
//                                        "Width: $width, Height: $height, Aspect Ratio: $aspectRatio"
//                                    )

                                    // 비율에 맞게 세로 크기 계산
                                    val calculatedHeight = (1000 * aspectRatio).toInt()

                                    // 이미지 설정
                                    Glide.with(mainActivity)
                                        .load("file://$uri")
                                        .override(1000, calculatedHeight)  // 세로 크기 계산 후 적용
                                        .into(fragmentModifyShirtBinding.imageViewSetImage)
                                }
                            })

                        saveInTemp()
                    } else {
//                        Log.d("test", "uri is null")
                    }


                }
            }
        }
    }

    //스트링으로된 사이즈 리스트로변환
    fun parseStringToList(input: String): List<String> {
        // 문자열의 양 끝 대괄호를 제거하고 쉼표로 나눔
        return input.removeSurrounding("[", "]")
            .split(",")
            .map { it.trim() } // 각 요소에서 공백 제거
    }

    //DB에서 이미선택된 컬러리스트를 가져와 수정화면에 반영함
    fun initializeSelectedColors(list: List<String>) {
        // selectColorList를 초기화하고, DB에서 가져온 색상을 추가
        selectColorList.clear()
        selectColorList.addAll(list)

        // RecyclerView 갱신
        fragmentModifyShirtBinding.recyclerViewColorSelector.adapter?.notifyDataSetChanged()
    }

    //세부종류 버튼설정
    private fun settingCategory() {
        fragmentModifyShirtBinding.apply {
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
        fragmentModifyShirtBinding.apply {
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
        fragmentModifyShirtBinding.apply {
            sliderPrice.addOnChangeListener { slider, value, fromUser ->
                val formattedValue = String.format("%,d", value.toInt())
                textView5.text = "${formattedValue}원"
            }
            sliderCount.addOnChangeListener { slider, value, fromUser ->

                textView6.text = "${value.toInt()}개"
            }
        }
    }

    //사진 출력 기능
    private fun setImageBitmap(bitmap: Bitmap) {
        fragmentModifyShirtBinding.imageViewSetImage.setImageBitmap(bitmap)
        saveInTemp()
    }

    //수정완료 버튼설정
    fun settingModifyDone() {
        fragmentModifyShirtBinding.apply {
            buttonModifyDone.setOnClickListener {
                val builder = androidx.appcompat.app.AlertDialog.Builder(mainActivity)
                builder.setTitle("수정")
                    .setMessage("수정하시겠습니까?")
                    .setPositiveButton("네") { dialog, _ ->

                        modifyDone()
                        modifyClothesInOutHistory()///////////////////////////////////////////////
                        mainActivity.removeFragment(FragmentName.MODIFY_FRAGMENT)

                        dialog.dismiss()
                    }
                    .setNegativeButton("취소") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }
    }

    //수정
    fun modifyDone() {
        fragmentModifyShirtBinding.apply {
            CoroutineScope(Dispatchers.Main).launch {
                val work = async(Dispatchers.IO) {
                    // 샘플 데이터 생성
                    val sampleClothes = ClothesViewModel(
                        clothesIdx = idx!!,
                        clothesPicture = temp.clothesPicture,
                        clothesName = temp.clothesName,
                        clothesPrice = temp.clothesPrice,
                        clothesInventory = temp.clothesInventory,
                        clothesColor = temp.clothesColor,
                        clothesSize = temp.clothesSize,
                        clothesCategory = temp.clothesCategory,
                        clothesTypeByCategory = temp.clothesTypeByCategory
                    )

                    // 데이터 저장
                    ClothesRepository.updateClothesInfo(mainActivity, sampleClothes)

                    // 저장된 데이터 확인
                    val clothesDatabase = ClothesDatabase.getInstance(mainActivity)
                    clothesDatabase?.clothesDAO()?.selectClothesDataAll()
                }
                val savedData = work.await()

                // 저장된 데이터 로그 출력
                savedData?.forEach { clothes ->
                    Log.d(
                        "test",
                        """
        저장된 데이터:
        ID=${clothes.clothesIdx}
        Name=${clothes.clothesName}
        Picture=${clothes.clothesPicture}
        Price=${clothes.clothesPrice}
        Inventory=${clothes.clothesInventory}
        Color=${clothes.clothesColor}
        Size=${clothes.clothesSize}
        Category=${clothes.clothesCategory}
        TypeByCategory=${clothes.clothesTypeByCategory}
        """.trimIndent()
                    )
                }
                //temp클리어
                temp.clear()

            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    fun modifyClothesInOutHistory() {
        fragmentModifyShirtBinding.apply {
            CoroutineScope(Dispatchers.Main).launch {
                val work1 = async(Dispatchers.IO) {
                    clothesInOutHistoryName
                    ClothesInOutHistoryRepository.modifyClothesInOutName(
                        mainActivity,
                        clothesInOutHistoryName,
                        temp.clothesName
                    )
                }
                work1.join()
            }
        }
    }
///////////////////////////////////////////////////////////////////////////////////////////////////

    // RecyclerView 색상 선택 설정
    private fun settingRecyclerViewColorSelector() {
        // selectColorList 초기화
        initializeSelectedColors(selectedColorDB)

        fragmentModifyShirtBinding.apply {
            recyclerViewColorSelector.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            // Enum 리스트를 전달
            recyclerViewColorSelector.adapter =
                RecyclerViewColorSelector(ColorEnum.values().toList())
        }
    }

    fun createAlbumLauncher() {
        albumLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    if (it.data != null && it.data?.data != null) {
                        val selectedImageUri = it.data?.data!!

                        val savedFile = saveImageToAppDirectory(selectedImageUri) // 이미지 저장
                        // `temp.clothesPicture`에 저장된 경로 설정
                        if (savedFile != null) {
                            temp.clothesPicture = savedFile.absolutePath
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val source = ImageDecoder.createSource(
                                mainActivity.contentResolver,
                                selectedImageUri
                            )
                            val bitmap = ImageDecoder.decodeBitmap(source)

                            real = getRealPathFromURI(selectedImageUri)
                            //temp.clothesPicture = real

                            setImageBitmap(bitmap)


                        } else {
                            val cursor = mainActivity.contentResolver.query(
                                selectedImageUri,
                                null,
                                null,
                                null,
                                null
                            )
                            if (cursor != null) {
                                cursor.moveToNext()
                                val idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                                real = cursor.getString(idx)
                                cursor.close()
                                val bitmap = BitmapFactory.decodeFile(real)
                                //temp.clothesPicture = real

                                setImageBitmap(bitmap)

                            }
                        }
                    }
                }
            }
    }

    fun getRealPathFromURI(contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            cursor = mainActivity.contentResolver.query(contentUri, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return ""
    }

    fun takePictureByCamera() {
        // 촬영한 사진이 저장될 파일 이름
        val fileName = "temp_${System.currentTimeMillis()}.jpg"
        // 앱 전용 디렉터리에 저장
        val storageDir = mainActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(storageDir, fileName)

        // 사진이 저장될 위치를 관리하는 Uri 객체를 생성
        contentUri = FileProvider.getUriForFile(
            mainActivity,
            "com.lion.team7_shopping_mall.file_provider",
            file
        )

        // 카메라 인텐트를 실행
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
        }
        originalCameraLauncher.launch(cameraIntent)
    }

    // 원본 사진 찍기 런처 생성
    fun createOriginalCameraLauncher() {
        originalCameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // 사진이 저장된 경로를 로그에 출력
                Log.d("test", "Saved photo path: ${contentUri.path}")

                pictureFromCamera = contentUri.path.toString()
                temp.clothesPicture = pictureFromCamera

                // 저장된 사진을 Bitmap으로 변환
                val bitmap = BitmapFactory.decodeFile(File(contentUri.path).absolutePath)

                // 이미지의 회전 각도값을 가져온다.
                val degree = getDegree(contentUri)

                // 회전 값을 이용해 이미지를 회전시킨다.
                val rotateBitmap = rotateBitmap(bitmap, degree)

                // 크기를 조정한 이미지를 가져온다.
                val resizeBitmap = resizeBitmap(1024, rotateBitmap)

                setImageBitmap(resizeBitmap)
            }
        }
    }

    // 이미지를 회전시키는 메서드
    fun rotateBitmap(bitmap: Bitmap, degree:Int): Bitmap {
        // 회전 이미지를 구하기 위한 변환 행렬
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        // 회전 행렬을 적용하여 회전된 이미지를 생성한다.
        val resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
        return resultBitmap
    }

    // 회전 각도값을 구하는 메서드
    fun getDegree(uri: Uri):Int{

        // 이미지의 태그 정보에 접근할 수 있는 객체를 생성한다.
        // andorid 10 버전 이상이라면
        val exifInterface = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            // 이미지 데이터를 가져올 수 있는 Content Provider의 Uri를 추출한다.
            val photoUri = MediaStore.setRequireOriginal(uri)
            // 컨텐츠 프로바이더를 통해 파일에 접근할 수 있는 스트림을 추출한다.
            val inputStream = mainActivity.contentResolver.openInputStream(photoUri)
            // ExifInterface 객체를 생성한다.
            ExifInterface(inputStream!!)
        } else {
            ExifInterface(uri.path!!)
        }

        // ExifInterface 정보 중 회전 각도 값을 가져온다
        val ori = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)

        // 회전 각도값을 담는다.
        val degree = when(ori){
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }

        return degree
    }

    // 이미지의 사이즈를 줄이는 메서드
    fun resizeBitmap(targetWidth:Int, bitmap: Bitmap): Bitmap {
        // 이미지의 축소/확대 비율을 구한다.
        val ratio = targetWidth.toDouble() / bitmap.width.toDouble()
        // 세로 길이를 구한다.
        val targetHeight = (bitmap.height.toDouble() * ratio).toInt()
        // 크기를 조절한 Bitmap 객체를 생성한다.
        val result = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false)
        return result
    }

    fun saveImageToAppDirectory(sourceUri: Uri): File? {
        try {
            // 저장할 디렉토리 설정
            val storageDir = mainActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val fileName = "temp_${System.currentTimeMillis()}.jpg"
            val targetFile = File(storageDir, fileName)

            // ContentResolver를 통해 Uri에서 파일 읽기
            val inputStream = mainActivity.contentResolver.openInputStream(sourceUri)
            val outputStream = FileOutputStream(targetFile)

            // 데이터를 복사
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            // temp.clothesPicture에 전체 경로 저장
            temp.clothesPicture = targetFile.absolutePath
            saveInTemp()

            return targetFile // 저장된 파일 반환
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    // RecyclerView Adapter
    inner class RecyclerViewColorSelector(
        private val colors: List<ColorEnum>
    ) : RecyclerView.Adapter<RecyclerViewColorSelector.ViewHolder>() {

        inner class ViewHolder(private val binding: RowColorBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(color: ColorEnum) {
                binding.colorView.setBackgroundColor(Color.parseColor(color.hexCode)) // 배경색 설정

                // selectColorList에 해당 색상이 존재하면 "O"로 표시하고, 선택 상태로 만듦
                if (selectColorList.contains(color.colorName)) {
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
                        selectColorList.remove(color.colorName) // 색상 리스트에서 제거
                    } else {
                        // 선택
                        binding.selectionIndicator.text = "O"
                        binding.selectionIndicator.setBackgroundResource(R.drawable.border_active)
                        selectColorList.add(color.colorName) // 색상 리스트에 추가
                    }
                    saveInTemp()
                    // 상태 변화 로그
//                    Log.d("test100", selectColorList.toString())
                    // RecyclerView 상태 갱신
                    notifyDataSetChanged()
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