package com.capstone.trashapp.presentation.adapter

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.trashapp.data.local.entity.Scan
import com.capstone.trashapp.databinding.ItemHistoryBinding
import com.capstone.trashapp.utils.renderBlob

class ScanAdapter(private val onClickRoot: (Scan) -> Unit, private val onClickDelete: (Scan) -> Unit) :
    ListAdapter<Scan, ScanAdapter.ScanViewHolder>(DIFF_CALLBACK) {
    private lateinit var parent: ViewGroup
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        this.parent = parent
        return ScanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScanViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            ivPhoto.renderBlob(item.scanImgBlob)
//            val animator = ValueAnimator.ofInt(0, item.confidenceScore)
//            indicator.setProgress(0, true)
//            animator.interpolator = AccelerateDecelerateInterpolator()
//            animator.startDelay = 0
//            animator.setDuration(2000)
//            animator.addUpdateListener { valueAnimator ->
//                val value = valueAnimator.animatedValue as Int
//                indicator.setProgress(value, true)
//            }
            tvDate.text = item.timestamp
//            tvScore.text = parent.context.getString(R.string.score_percent, item.confidenceScore)
            tvTitle.text = item.label
            btnDelete.setOnClickListener {
                onClickDelete.invoke(item)
            }
            root.setOnClickListener {
                onClickRoot.invoke(item)
            }
//            animator.start()
        }
    }

    inner class ScanViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Scan>() {
            override fun areItemsTheSame(oldItem: Scan, newItem: Scan): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Scan, newItem: Scan): Boolean {
                return oldItem == newItem
            }
        }
    }
}