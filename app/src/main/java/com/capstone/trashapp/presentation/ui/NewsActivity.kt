package com.capstone.trashapp.presentation.ui

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.trashapp.R
import com.capstone.trashapp.databinding.ActivityNewsBinding
import com.capstone.trashapp.presentation.adapter.NewsAdapter
import com.capstone.trashapp.presentation.viewmodel.NewsViewModel
import com.capstone.trashapp.utils.Async
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding
    private lateinit var newsAdapter: NewsAdapter
    private val viewModel: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.fetchHeadlines()
        initUI()
        initListeners()
        initObservers()
    }

    private fun openNews(url: String) {
        val builder = CustomTabsIntent.Builder().apply {
            @Suppress("DEPRECATION")
            setToolbarColor(ContextCompat.getColor(this@NewsActivity, R.color.green_500))
        }
        val newsIntent = builder.build()
        newsIntent.launchUrl(this@NewsActivity, Uri.parse(url))
    }

    private fun initUI() {
        with(binding) {
            newsAdapter = NewsAdapter(::openNews)
            rvNews.layoutManager =
                LinearLayoutManager(this@NewsActivity, LinearLayoutManager.VERTICAL, false)
            rvNews.adapter = newsAdapter
        }
    }

    private fun initListeners() {
        with(binding) {
            toolbarNews.setNavigationOnClickListener {
                this@NewsActivity.finish()
            }
            swipeRefreshLayout.setOnRefreshListener {
                viewModel.fetchHeadlines()
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun initObservers() {
        with(binding) {
            viewModel.articles.observe(this@NewsActivity) { result ->
                when (result) {
                    is Async.Success -> {
                        layoutError.isGone = true
                        loadingBar.isGone = true
                        rvNews.isVisible = true
                        tvEmpty.isGone = true
                        newsAdapter.submitList(result.data)
                    }

                    is Async.Error -> {
                        loadingBar.isGone = true
                        rvNews.isGone = true
                        tvEmpty.isGone = true
                        layoutError.isVisible = true
                    }

                    Async.Loading -> {
                        layoutError.isGone = true
                        rvNews.isGone = true
                        tvEmpty.isGone = true
                        loadingBar.isVisible = true
                    }

                    Async.Empty -> {
                        layoutError.isGone = true
                        rvNews.isGone = true
                        loadingBar.isGone = true
                        tvEmpty.isVisible = true
                    }
                }
            }
        }
    }
}