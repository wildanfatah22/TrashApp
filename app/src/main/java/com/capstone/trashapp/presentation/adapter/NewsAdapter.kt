package com.capstone.trashapp.presentation.adapter

import android.graphics.text.LineBreaker
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.trashapp.data.response.Article
import com.capstone.trashapp.databinding.ItemNewsBinding
import com.capstone.trashapp.utils.glide

class NewsAdapter(private val onNewsClick: (String) -> Unit) :
    ListAdapter<Article, NewsAdapter.NewsViewHolder>(DIFF_CALLBACK) {
    private lateinit var parent: ViewGroup
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        this.parent = parent
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            this.root.setOnClickListener {
                item.url?.let { onNewsClick.invoke(it) }
            }
            tvTitle.apply {
                text = item.title ?: ""
                isSelected = true
            }
            item.description?.let { description ->
                tvDescription.apply {
                    text = description
                    maxLines = 4
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                    }
                }
            }
            item.urlToImage?.let { urlToImage ->
                ivImage.glide(urlToImage)
            }
        }
    }

    inner class NewsViewHolder(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem == newItem
            }
        }
    }
}