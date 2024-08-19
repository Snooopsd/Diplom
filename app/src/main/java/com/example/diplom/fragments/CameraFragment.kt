package com.example.diplom.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.diplom.Database
import com.example.diplom.JsonHandler
import com.example.diplom.ListItem
import com.example.diplom.R
import com.example.diplom.TFLmodel
import java.io.File
import java.util.Date

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class CameraFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var image: ImageView
    private lateinit var CameraBtn: Button
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var currentPhotoPath: String
    private lateinit var text: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        image = view.findViewById(R.id.imageView)
        image.setImageResource(R.drawable.ic_camera)
        CameraBtn = view.findViewById(R.id.CameraBtn)
        text = view.findViewById(R.id.textView4)

        CameraBtn.setOnClickListener {
//            dispatchTakePictureIntent()
            val drawableId = R.drawable.img2
            val bitmap = BitmapFactory.decodeResource(resources, drawableId)
            image.setImageResource(R.drawable.img2)

            val tf = TFLmodel(requireContext())
            val TFLResult = tf.classifyImage(bitmap)

            val db = Database(requireContext())

            db.fetchDataFromFirestore(TFLResult) {result ->
                val listItem = ListItem(
                    sort = TFLResult,
                    flora = result,
                    image = "drawable://" + drawableId
                )

                showBottomSheet(listItem)

                checkAndRequestPermissions()
                val currentList = JsonHandler.loadData(requireContext()).toMutableList()
                currentList.add(listItem)
                JsonHandler.saveData(requireContext(), currentList)
            }
        }

        image.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CameraFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_IMAGE_CAPTURE
            )
            return
        }

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: Exception) {
                    null
                }
                photoFile?.also {
                    val photoURI = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
//            val file = File(currentPhotoPath)
//            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
//            image.setImageBitmap(bitmap)
//            val drawableId = R.drawable.img2
//            val bitmap = BitmapFactory.decodeResource(resources, drawableId)
//
//            val tf = TFLmodel(requireContext())
//            val TFLResult = tf.classifyImage(bitmap)
//
//            val db = Database(requireContext())
//
//            db.fetchDataFromFirestore(TFLResult) {result ->
//                val listItem = ListItem(
//                    sort = TFLResult,
//                    flora = result,
//                    image = "drawable://" + drawableId
//                )
//
//                showBottomSheet(listItem)
//
//                checkAndRequestPermissions()
//                val currentList = JsonHandler.loadData(requireContext()).toMutableList()
//                currentList.add(listItem)
//                JsonHandler.saveData(requireContext(), currentList)
//            }
        }
    }

    private fun showBottomSheet(item: ListItem) {
        val bottomSheet = if (item.flora == "Яблоня" || item.flora == "Груша") {
            InformationBottomSheetDialogFragment.newInstance(R.layout.sort_bottom_sheet_layout)
        } else {
            InformationBottomSheetDialogFragment.newInstance(R.layout.diseases_bottom_sheet_layout)
        }
        bottomSheet.setData(item, true)
        bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
    }

    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 150)
        }
    }
}