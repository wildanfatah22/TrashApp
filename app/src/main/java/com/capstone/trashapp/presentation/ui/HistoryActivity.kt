package com.capstone.trashapp.presentation.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.trashapp.R
import com.capstone.trashapp.data.local.entity.Scan
import com.capstone.trashapp.databinding.ActivityHistoryBinding
import com.capstone.trashapp.presentation.adapter.ScanAdapter
import com.capstone.trashapp.presentation.viewmodel.HistoryViewModel
import com.capstone.trashapp.utils.Async
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var scanAdapter: ScanAdapter
    private val viewModel: HistoryViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.fetchScanHistory()
        initUI()
        initListeners()
        initObservers()
    }

    private fun deleteScanHistory(scan: Scan) {
        viewModel.deleteScanHistory(scan)
        Snackbar.make(
            binding.root,
            "Riwayat scan berhasil dihapus",
            Snackbar.LENGTH_LONG
        ).setAction("UNDO") {
            viewModel.saveScanHistory(scan)
        }.show()
    }

    private fun viewScanHistory(scan: Scan) {
        val bitmap = BitmapFactory.decodeByteArray(scan.scanImgBlob, 0, scan.scanImgBlob.size)
        val tempFile = File(cacheDir, "temp_history_image.jpg")
        try {
            val fos = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val tempUri = Uri.fromFile(tempFile)
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(ResultActivity.EXTRA_SCORE, scan.confidenceScore)
            putExtra(ResultActivity.EXTRA_IMAGE_URI, tempUri.toString())
            putExtra(ResultActivity.EXTRA_LABEL, scan.label)
            putExtra(ResultActivity.EXTRA_NEW_RESULT, false)
        }
        startActivity(intent)
    }

    private fun initUI() {
        with(binding) {
            scanAdapter = ScanAdapter(::viewScanHistory, ::deleteScanHistory)
            rvScan.layoutManager =
                LinearLayoutManager(this@HistoryActivity, LinearLayoutManager.VERTICAL, false)
            rvScan.adapter = scanAdapter
        }
    }

    private fun initListeners() {
        with(binding) {
            toolbarHistory.setNavigationOnClickListener {
                finish()
            }
            swipeRefreshLayout.setOnRefreshListener {
                viewModel.fetchScanHistory()
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun initObservers() {
        with(binding) {
            viewModel.history.observe(this@HistoryActivity) { result ->
                when (result) {
                    is Async.Success -> {
                        layoutError.isGone = true
                        loadingBar.isGone = true
                        rvScan.isVisible = true
                        tvEmpty.isGone = true
                        scanAdapter.submitList(result.data)
                    }

                    is Async.Error -> {
                        loadingBar.isGone = true
                        rvScan.isGone = true
                        tvEmpty.isGone = true
                        layoutError.isVisible = true
                    }

                    Async.Loading -> {
                        layoutError.isGone = true
                        rvScan.isGone = true
                        tvEmpty.isGone = true
                        loadingBar.isVisible = true
                    }

                    Async.Empty -> {
                        layoutError.isGone = true
                        rvScan.isGone = true
                        loadingBar.isGone = true
                        tvEmpty.isVisible = true
                    }
                }
            }
        }
    }
}