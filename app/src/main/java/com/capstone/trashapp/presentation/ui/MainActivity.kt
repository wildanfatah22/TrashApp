package com.capstone.trashapp.presentation.ui

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.capstone.trashapp.R
import com.capstone.trashapp.databinding.ActivityMainBinding
import com.capstone.trashapp.presentation.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri == null || uri == Uri.EMPTY) {
            return@registerForActivityResult
        } else {
            uCrop(uri)
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
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("currentImageUri")) {
                currentImageUri = Uri.parse(savedInstanceState.getString("currentImageUri"))
                showImage()
            }
        }
        initListeners()
        observeViewModel()
        
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
                // TODO
            }
            btnGallery.setOnClickListener {
                startGallery()
            }
            btnAnalyze.setOnClickListener {
                analyzeImage()
            }
            btnClear.setOnClickListener {
                currentImageUri = null
                showImage()
            }
            topbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.article -> {
                        val articleIntent = Intent(this@MainActivity, ArticleActivity::class.java)
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

    private fun observeViewModel() {
        viewModel.classificationResult.observe(this) { result ->
            result?.let {
                val accuracy = it.accuracy?.times(100)?.toInt() ?: 0
                val prediction = it.prediction ?: "Unknown"
                moveToResult(accuracy, prediction)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressIndicator.isVisible = isLoading
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


    private fun analyzeImage() {
        val imageUri = currentImageUri
        if (imageUri == null) {
            Snackbar.make(binding.root, "Gambar belum dipilih", Snackbar.LENGTH_LONG)
                .setAction("BUKA GALERI") { startGallery() }
                .show()
            return
        }
        viewModel.classifyImage(imageUri)
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

    private fun moveToResult(score: Int, label: String) {
        val intent = Intent(this, ResultActivity::class.java).apply {

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