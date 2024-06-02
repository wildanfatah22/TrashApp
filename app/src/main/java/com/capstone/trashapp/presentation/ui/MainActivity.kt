package com.capstone.trashapp.presentation.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.capstone.trashapp.R
import com.capstone.trashapp.databinding.ActivityMainBinding
import com.capstone.trashapp.utils.ImageClassifierHelper
import com.google.android.material.snackbar.Snackbar
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null
    private var imgHelper: ImageClassifierHelper? = null
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri == null || uri == Uri.EMPTY) {
            return@registerForActivityResult
        } else {
            uCrop(uri)
        }
    }

    private val imageCaptureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            // Store the captured image and proceed to crop
            if (bitmap != null) {
                storeImage(bitmap)
            }
        }


    override fun onResume() {
        super.onResume()
        binding.progressIndicator.isGone = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imgHelper = ImageClassifierHelper(this)
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("currentImageUri")) {
                currentImageUri = Uri.parse(savedInstanceState.getString("currentImageUri"))
                showImage()
            }
        }
        initListeners()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        currentImageUri?.let { uri ->
            outState.putString("currentImageUri", uri.toString())
        }
        super.onSaveInstanceState(outState)
    }

    private fun initListeners() {
        with(binding) {
            btnCamera.setOnClickListener {
                checkCameraPermission()
            }
            btnGallery.setOnClickListener {
                startGallery()
            }
            btnAnalyze.setOnClickListener {
                processImage()
            }
            btnClear.setOnClickListener {
                currentImageUri = null
                showImage()
            }
            topbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.article -> {
                        val articleIntent = Intent(this@MainActivity, NewsActivity::class.java)
                        startActivity(articleIntent)
                        true
                    }

                    R.id.history -> {
                        val historyIntent = Intent(this@MainActivity, HistoryActivity::class.java)
                        startActivity(historyIntent)
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun openCamera() {
        imageCaptureLauncher.launch(null)
    }

    private fun storeImage(bitmap: Bitmap) {
        val fileUri = saveBitmapToFile(bitmap)
        if (fileUri != null) {
            uCrop(fileUri)
        } else {
            showToast("Gagal menyimpan gambar")
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap): Uri? {
        val fileName = "captured_image.jpg"
        val file = File(cacheDir, fileName)
        return try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            file.toUri()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        with(binding) {
            val tempCurrentImageUri = currentImageUri
            if (tempCurrentImageUri == null || tempCurrentImageUri == Uri.EMPTY) {
                tvEmpty.isVisible = true
                previewImageView.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this@MainActivity,
                        R.drawable.image_placeholder
                    )
                )
                btnClear.isGone = true
            } else {
                btnClear.isVisible = true
                tvEmpty.isGone = true
                previewImageView.setImageDrawable(null) // image view perlu di force redraw ketika file di URI di rewrite
                previewImageView.setImageURI(tempCurrentImageUri)
            }
        }
    }

    private fun processImage() {
        val tempCurrentImageUri = currentImageUri
        if (tempCurrentImageUri == null) {
            showGallerySnackbar()
            return
        }
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, tempCurrentImageUri)
        val classificationResult = imgHelper?.classifyImage(bitmap)
        if (classificationResult != null) {
            Log.d("MainActivity", "Classification Result: Confidence=${classificationResult.confidence}," +
                    " ClassName=${classificationResult.className}")
            moveToResult(classificationResult.confidence, classificationResult.className)
        } else {
            Toast.makeText(this, "Error in classification", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        imgHelper?.close()
    }

    private fun uCrop(sourceUri: Uri) {
        val fileName = "preview_cropped.jpg"
        val destinationFile = File(this@MainActivity.cacheDir, fileName)
        val destinationUri = Uri.fromFile(destinationFile)

        val uCropOptions = UCrop.Options().apply {
            setCompressionFormat(Bitmap.CompressFormat.JPEG)
            setActiveControlsWidgetColor(getColor(R.color.green_500))
            setStatusBarColor(getColor(R.color.green_500))
            setFreeStyleCropEnabled(true)
            setToolbarColor(getColor(R.color.white))
        }

        UCrop.of(sourceUri, destinationUri)
            .withOptions(uCropOptions)
            .start(this)
    }

    private fun moveToResult(score: Float, label: String) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(ResultActivity.EXTRA_SCORE, score)
            putExtra(ResultActivity.EXTRA_IMAGE_URI, currentImageUri.toString())
            putExtra(ResultActivity.EXTRA_LABEL, label)
            putExtra(ResultActivity.EXTRA_NEW_RESULT, true)
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showGallerySnackbar() {
        Snackbar.make(
            binding.root,
            "Gambar belum dipilih",
            Snackbar.LENGTH_LONG
        ).setAction("BUKA GALERI") {
            startGallery()
        }.show()

    }

    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission is granted, launch camera activity
                openCamera()
            } else {
                // Permission is denied, show an explanation or request again
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }

    private fun checkCameraPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) -> {
                // Permission is already granted, launch camera activity
                openCamera()
            }
            else -> {
                // Permission is not granted, request it
                cameraPermissionRequest.launch(Manifest.permission.CAMERA)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            currentImageUri = resultUri
            showImage()
        } else if (resultCode == UCrop.RESULT_ERROR) {
            showToast("Terjadi kesalahan saat mengedit foto")
        }
    }
}