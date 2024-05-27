package com.capstone.trashapp.presentation.ui

import android.animation.ValueAnimator
import android.graphics.text.LineBreaker
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.capstone.trashapp.R
import com.capstone.trashapp.databinding.ActivityResultBinding
import com.capstone.trashapp.presentation.viewmodel.ResultViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private val viewModel: ResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        initListeners()
    }

    private fun initUI() {
        val isNewScan = intent.getBooleanExtra(EXTRA_NEW_RESULT, true)
        val score = intent.getFloatExtra(EXTRA_SCORE, 0f)
        val label = intent.getStringExtra(EXTRA_LABEL)
        val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)

        Log.d("ResultActivity", "Received data: Score=$score, Label=$label, ImageUri=$imageUri")

        with(binding) {
            if (isNewScan) {
                btnSave.isVisible = true
            } else {
                btnSave.isInvisible = true
            }
            resultImage.setImageURI(Uri.parse(imageUri))
            when (label) {
                "Plastic" -> {
                    indicator.progressDrawable = AppCompatResources.getDrawable(
                        this@ResultActivity, R.drawable.progress_circle
                    )
                    tvGuide.apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                        }
                        text = getString(R.string.plastic_guide)
                    }
                }
                "Paper" -> {
                    indicator.progressDrawable = AppCompatResources.getDrawable(
                        this@ResultActivity, R.drawable.progress_circle
                    )
                    tvGuide.apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                        }
                        text = getString(R.string.paper_guide)
                    }
                }
                "Organic" -> {
                    indicator.progressDrawable = AppCompatResources.getDrawable(
                        this@ResultActivity, R.drawable.progress_circle
                    )
                    tvGuide.apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                        }
                        text = getString(R.string.organic_guide)
                    }
                }
                "Glass" -> {
                    indicator.progressDrawable = AppCompatResources.getDrawable(
                        this@ResultActivity, R.drawable.progress_circle
                    )
                    tvGuide.apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                        }
                        text = getString(R.string.glass_guide)
                    }
                }
                "Metal" -> {
                    indicator.progressDrawable = AppCompatResources.getDrawable(
                        this@ResultActivity, R.drawable.progress_circle
                    )
                    tvGuide.apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                        }
                        text = getString(R.string.metal_guide)
                    }
                }
                "Cardboard" -> {
                    indicator.progressDrawable = AppCompatResources.getDrawable(
                        this@ResultActivity, R.drawable.progress_circle
                    )
                    tvGuide.apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                        }
                        text = getString(R.string.cardboard_guide)
                    }
                }
                else -> {
                    indicator.progressDrawable = AppCompatResources.getDrawable(
                        this@ResultActivity, R.drawable.progress_circle
                    )
                    tvGuide.apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                        }
                        text = getString(R.string.default_guide)
                    }
                }
            }
            val animator = ValueAnimator.ofFloat(0f, score)
            indicator.setProgress(0, true)
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.startDelay = 0
            animator.duration = 2000
            animator.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Float
                indicator.setProgress((value * 100).toInt(), true)  // convert float score to percentage
            }
            resultText.text = label
            tvScore.text = getString(R.string.score_percent, (score * 100).toInt())  // convert float score to percentage
            animator.start()
        }
    }

    private fun initListeners() {
        with(binding) {
            toolbarResult.setNavigationOnClickListener {
                finish()
            }
            btnSave.setOnClickListener {
                val path = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI)).path
                val label = intent.getStringExtra(EXTRA_LABEL)
                val confidenceScore = intent.getFloatExtra(EXTRA_SCORE, 0f)
                if (path == null || label == null) {
                    Toast.makeText(
                        this@ResultActivity, "Gagal menyimpan hasil scan", Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                val tempFile = File(path)
                viewModel.saveScan(
                    label, confidenceScore, tempFile
                )
                it.isInvisible = true
                Toast.makeText(
                    this@ResultActivity, "Berhasil menyimpan hasil scan", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        const val EXTRA_SCORE = "extra_score"
        const val EXTRA_LABEL = "extra_label"
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_NEW_RESULT = "extra_new_result"
    }
}