package com.dicoding.asclepius.view.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.databinding.ItemLoadMoreBinding
import com.dicoding.asclepius.databinding.ItemRowNewsVerticalBinding
import com.dicoding.asclepius.utils.formatNewsDate

class VerticalNewsAdapter : ListAdapter<ArticlesItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    var onItemClick: ((ArticlesItem) -> Unit)? = null
    var onLoadMoreClick: (() -> Unit)? = null
    var showLoadMore: Boolean = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        return if (position < currentList.size) VIEW_TYPE_ITEM else VIEW_TYPE_LOAD_MORE
    }

    override fun getItemCount(): Int {
        return if (showLoadMore) currentList.size + 1 else currentList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val binding = ItemRowNewsVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MyViewHolder(binding)
        } else {
            val binding = ItemLoadMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            LoadMoreViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            val item = getItem(position)
            val prevItem = if (position > 0) getItem(position - 1) else null
            (holder as MyViewHolder).bind(item, prevItem)
        } else {
            (holder as LoadMoreViewHolder).bind()
        }
    }

    inner class MyViewHolder(private var binding: ItemRowNewsVerticalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(news: ArticlesItem, prevNews: ArticlesItem? = null) {
            val currentDate = formatNewsDate(news.publishedAt)
            val prevDate = prevNews?.let { formatNewsDate(it.publishedAt) }

            if (currentDate != prevDate) {
                binding.tvDateHeader.visibility = View.VISIBLE
                binding.tvDateHeader.text = currentDate
            } else {
                binding.tvDateHeader.visibility = View.GONE
            }

            Glide.with(itemView.context)
                .load(news.urlToImage)
                .placeholder(R.drawable.ic_place_holder)
                .error(R.drawable.ic_place_holder)
                .into(binding.ivNews)

            binding.textTitle.text = news.title
            binding.textDescription.text = news.description
        }

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION && position < currentList.size) {
                    onItemClick?.invoke(getItem(position))
                }
            }
        }
    }

    inner class LoadMoreViewHolder(private val binding: ItemLoadMoreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.btnLoadMore.setOnClickListener {
                onLoadMoreClick?.invoke()
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOAD_MORE = 1

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticlesItem>() {
            override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
