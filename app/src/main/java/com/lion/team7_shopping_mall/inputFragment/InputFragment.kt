package com.lion.team7_shopping_mall.inputFragment


import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.content.FileProvider
import androidx.fragment.app.commit
import com.lion.team7_shopping_mall.MainActivity
import com.lion.team7_shopping_mall.R
import com.lion.team7_shopping_mall.database.ClothesDatabase
import com.lion.team7_shopping_mall.database.ClothesVO
import com.lion.team7_shopping_mall.databinding.FragmentInputBinding
import com.lion.team7_shopping_mall.inputFragment.category.InputOuterFragment
import com.lion.team7_shopping_mall.inputFragment.category.InputPantsFragment
import com.lion.team7_shopping_mall.inputFragment.category.InputShirtFragment
import com.lion.team7_shopping_mall.inputFragment.category.InputSkirtFragment
import com.lion.team7_shopping_mall.repository.ClothesRepository
import com.lion.team7_shopping_mall.viewmodel.ClothesViewModel
import com.lion.temp.util.FragmentName

import com.lion.temp.util.InputFragmentName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class InputFragment() : Fragment() {
    lateinit var fragmentInputBinding: FragmentInputBinding
    lateinit var mainActivity: MainActivity
    private var currentFragmentState: Int = -1

    //앨범에서 가져오는것 관련
    lateinit var albumLauncher: ActivityResultLauncher<Intent>
    var real: String =""


    //카메라에서 가져오는것 관련
    // 원본 사진 찍기용 런처
    lateinit var originalCameraLauncher: ActivityResultLauncher<Intent>

    // 촬영된 사진이 위치할 경로
    lateinit var filePath:String
    // 저장된 파일에 접근하기 위한 Uri
    lateinit var contentUri:Uri

    var pictureFromCamera: String =""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentInputBinding = FragmentInputBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        settingFragment()

        // 외부 저장소 경로를 가져온다.
        filePath = mainActivity.getExternalFilesDir(null).toString()
        createAlbumLauncher()
        createOriginalCameraLauncher()

        return fragmentInputBinding.root
    }

    //저장눌렀을때 다이얼로그-> 세이브
    private fun showDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("저장")
            .setMessage("저장하시겠습니까?")
            .setPositiveButton("네") { dialog, _ ->


                CoroutineScope(Dispatchers.Main).launch {
                    val work = async(Dispatchers.IO) {
                        // 샘플 데이터 생성
                        val sampleClothes = ClothesViewModel(
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
                        ClothesRepository.insertStudentInfo(mainActivity, sampleClothes)

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


                    // 성공 메시지
                    Toast.makeText(mainActivity, "저장되었습니다.", Toast.LENGTH_SHORT).show()

                    temp.clear()
                    Log.d("test", "저장 후 temp 클리어: ${temp.clothesName}")
                    mainActivity.removeFragment(FragmentName.INPUT_FRAGMENT)
                    dialog.dismiss()
                }
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }


    //각각의 프래그먼트로 이동하는 부분
    private fun settingFragment() {
        fragmentInputBinding.apply {
            materialToolbarInput.title = "상품입력"
            materialToolbarInput.isTitleCentered = true
            materialToolbarInput.inflateMenu(R.menu.menu_input)

            //초기설정
            replaceInputFragment(InputFragmentName.INPUT_OUTER_FRAGMENT, false, null)
            currentFragmentState = InputFragmentName.INPUT_OUTER_FRAGMENT.number

            materialToolbarInput.setOnMenuItemClickListener {

                when (it.itemId) {
                    R.id.itemSave -> {
                        showDialog()
                    }

                    R.id.input_outer_menu -> {
                        if (currentFragmentState != InputFragmentName.INPUT_OUTER_FRAGMENT.number) {
                            showMoveDialog() { result ->
                                if (result) {
                                    replaceInputFragment(
                                        InputFragmentName.INPUT_OUTER_FRAGMENT,
                                        false,
                                        null
                                    )
                                    currentFragmentState =
                                        InputFragmentName.INPUT_OUTER_FRAGMENT.number
                                }
                            }
                        }
                    }

                    R.id.input_shirt_menu -> {
                        if (currentFragmentState != InputFragmentName.INPUT_SHIRT_FRAGMENT.number) {
                            showMoveDialog() { result ->
                                if (result) {
                                    replaceInputFragment(
                                        InputFragmentName.INPUT_SHIRT_FRAGMENT,
                                        false,
                                        null
                                    )
                                    currentFragmentState =
                                        InputFragmentName.INPUT_SHIRT_FRAGMENT.number
                                }
                            }
                        }
                    }

                    R.id.input_pants_menu -> {
                        if (currentFragmentState != InputFragmentName.INPUT_PANTS_FRAGMENT.number) {
                            showMoveDialog() { result ->
                                if (result) {
                                    replaceInputFragment(
                                        InputFragmentName.INPUT_PANTS_FRAGMENT,
                                        false,
                                        null
                                    )
                                    currentFragmentState =
                                        InputFragmentName.INPUT_PANTS_FRAGMENT.number
                                }
                            }
                        }
                    }

                    R.id.input_skirt_menu -> {
                        if (currentFragmentState != InputFragmentName.INPUT_SKIRT_FRAGMENT.number) {
                            showMoveDialog() { result ->
                                if (result) {
                                    replaceInputFragment(
                                        InputFragmentName.INPUT_SKIRT_FRAGMENT,
                                        false,
                                        null
                                    )
                                    currentFragmentState =
                                        InputFragmentName.INPUT_SKIRT_FRAGMENT.number
                                }
                            }
                        }
                    }
                }
                true
            }
        }
    }

    //프래그먼트끼리이동할때 고지하는 다이얼로그
    private fun showMoveDialog(callback: (Boolean) -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("안내")
            .setMessage(
                "입력도중 카테고리를 넘어가면\n" +
                        "기존에 입력하신내용은 사라집니다"
            )
            .setPositiveButton("네") { dialog, _ ->
                callback(true) // 확인 버튼 클릭 시 true 전달
                //다른프래그먼트로 이동했을때 temp를 비워준다
                temp.clear()
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                callback(false) // 취소 버튼 클릭 시 false 전달
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    // 입력프래그먼트안에서 프래그먼트를 교체하는 함수
    fun replaceInputFragment(
        inputFragmentName: InputFragmentName,
        isAddToBackStack: Boolean,
        dataBundle: Bundle?
    ) {
        // 프래그먼트 객체
        val newFragment = when (inputFragmentName) {
            InputFragmentName.INPUT_OUTER_FRAGMENT -> InputOuterFragment(this)
            InputFragmentName.INPUT_SHIRT_FRAGMENT -> InputShirtFragment(this)
            InputFragmentName.INPUT_PANTS_FRAGMENT -> InputPantsFragment(this)
            InputFragmentName.INPUT_SKIRT_FRAGMENT -> InputSkirtFragment(this)
        }

        // bundle 객체가 null이 아니라면
        if (dataBundle != null) {
            newFragment.arguments = dataBundle
        }

        // 프래그먼트 교체
        mainActivity.supportFragmentManager.commit {

//            newFragment.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
//            newFragment.reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
//            newFragment.enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
//            newFragment.returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)

            mainActivity.supportFragmentManager.commit {
                replace(
                    R.id.fragmentContainerViewInput,
                    newFragment,
                    newFragment::class.java.simpleName
                )  // 클래스명을 태그로 사용
                if (isAddToBackStack) {
                    addToBackStack(newFragment::class.java.simpleName) // 클래스명으로 백스택에 추가
                }
            }
        }
    }

    fun createAlbumLauncher() {
        albumLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    if (it.data != null && it.data?.data != null) {
                        val selectedImageUri = it.data?.data!!

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val source = ImageDecoder.createSource(
                                mainActivity.contentResolver,
                                selectedImageUri
                            )
                            val bitmap = ImageDecoder.decodeBitmap(source)

                            real = getRealPathFromURI(selectedImageUri)
                            temp.clothesPicture = real
                            when (currentFragmentState) {
                                InputFragmentName.INPUT_OUTER_FRAGMENT.number -> {
                                    val inputOuterFragment =
                                        activity?.supportFragmentManager?.findFragmentByTag(
                                            InputOuterFragment::class.java.simpleName
                                        ) as? InputOuterFragment
                                    inputOuterFragment?.setImageBitmap(bitmap)
                                }

                                InputFragmentName.INPUT_SHIRT_FRAGMENT.number -> {
                                    val inputShirtFragment =
                                        activity?.supportFragmentManager?.findFragmentByTag(
                                            InputShirtFragment::class.java.simpleName
                                        ) as? InputShirtFragment
                                    inputShirtFragment?.setImageBitmap(bitmap)
                                }

                                InputFragmentName.INPUT_PANTS_FRAGMENT.number -> {
                                    val inputPantsFragment =
                                        activity?.supportFragmentManager?.findFragmentByTag(
                                            InputPantsFragment::class.java.simpleName
                                        ) as? InputPantsFragment
                                    inputPantsFragment?.setImageBitmap(bitmap)
                                }

                                InputFragmentName.INPUT_SKIRT_FRAGMENT.number -> {
                                    val inputSkirtFragment =
                                        activity?.supportFragmentManager?.findFragmentByTag(
                                            InputSkirtFragment::class.java.simpleName
                                        ) as? InputSkirtFragment
                                    inputSkirtFragment?.setImageBitmap(bitmap)
                                }
                            }


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
                                temp.clothesPicture = real
                                when (currentFragmentState) {
                                    InputFragmentName.INPUT_OUTER_FRAGMENT.number -> {
                                        val inputOuterFragment =
                                            activity?.supportFragmentManager?.findFragmentByTag(
                                                InputOuterFragment::class.java.simpleName
                                            ) as? InputOuterFragment
                                        inputOuterFragment?.setImageBitmap(bitmap) // 이미지 설정
                                    }

                                    InputFragmentName.INPUT_SHIRT_FRAGMENT.number -> {
                                        val inputShirtFragment =
                                            activity?.supportFragmentManager?.findFragmentByTag(
                                                InputShirtFragment::class.java.simpleName
                                            ) as? InputShirtFragment
                                        inputShirtFragment?.setImageBitmap(bitmap)
                                    }

                                    InputFragmentName.INPUT_PANTS_FRAGMENT.number -> {
                                        val inputPantsFragment =
                                            activity?.supportFragmentManager?.findFragmentByTag(
                                                InputPantsFragment::class.java.simpleName
                                            ) as? InputPantsFragment
                                        inputPantsFragment?.setImageBitmap(bitmap)
                                    }

                                    InputFragmentName.INPUT_SKIRT_FRAGMENT.number -> {
                                        val inputSkirtFragment =
                                            activity?.supportFragmentManager?.findFragmentByTag(
                                                InputSkirtFragment::class.java.simpleName
                                            ) as? InputSkirtFragment
                                        inputSkirtFragment?.setImageBitmap(bitmap)
                                    }
                                }

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

                // Fragment에 Bitmap 설정
                when (currentFragmentState) {
                    InputFragmentName.INPUT_OUTER_FRAGMENT.number -> {
                        val inputOuterFragment =
                            activity?.supportFragmentManager?.findFragmentByTag(
                                InputOuterFragment::class.java.simpleName
                            ) as? InputOuterFragment
                        inputOuterFragment?.setImageBitmap(resizeBitmap)
                    }

                    InputFragmentName.INPUT_SHIRT_FRAGMENT.number -> {
                        val inputShirtFragment =
                            activity?.supportFragmentManager?.findFragmentByTag(
                                InputShirtFragment::class.java.simpleName
                            ) as? InputShirtFragment
                        inputShirtFragment?.setImageBitmap(resizeBitmap)
                    }

                    InputFragmentName.INPUT_PANTS_FRAGMENT.number -> {
                        val inputPantsFragment =
                            activity?.supportFragmentManager?.findFragmentByTag(
                                InputPantsFragment::class.java.simpleName
                            ) as? InputPantsFragment
                        inputPantsFragment?.setImageBitmap(resizeBitmap)
                    }

                    InputFragmentName.INPUT_SKIRT_FRAGMENT.number -> {
                        val inputSkirtFragment =
                            activity?.supportFragmentManager?.findFragmentByTag(
                                InputSkirtFragment::class.java.simpleName
                            ) as? InputSkirtFragment
                        inputSkirtFragment?.setImageBitmap(resizeBitmap)
                    }
                }
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
    fun getDegree(uri:Uri):Int{

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



}