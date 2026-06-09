package com.dicoding.asclepius.view.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.databinding.ItemMoreNewsBinding
import com.dicoding.asclepius.databinding.ItemRowNewsBinding

class NewsAdapter : ListAdapter<ArticlesItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    var onItemClick: ((ArticlesItem) -> Unit)? = null
    var onMoreClick: (() -> Unit)? = null

    override fun getItemViewType(position: Int): Int {
        return if (position < currentList.size) VIEW_TYPE_ITEM else VIEW_TYPE_MORE
    }

    override fun getItemCount(): Int {
        return if (currentList.isEmpty()) 0 else currentList.size + 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val binding = ItemRowNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MyViewHolder(binding)
        } else {
            val binding = ItemMoreNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MoreViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            val item = getItem(position)
            (holder as MyViewHolder).bind(item)
        } else {
            (holder as MoreViewHolder).bind()
        }
    }

    inner class MyViewHolder(private var binding: ItemRowNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(news: ArticlesItem) {
            Glide.with(itemView.context)
                .load(news.urlToImage)
                .placeholder(R.drawable.ic_place_holder)
                .error(R.drawable.ic_place_holder)
                .into(binding.ivNews)

            binding.textTitle.text = news.title
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

    inner class MoreViewHolder(private var binding: ItemMoreNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.btnCircleMore.setOnClickListener {
                onMoreClick?.invoke()
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_MORE = 1

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
