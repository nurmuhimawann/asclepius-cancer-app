package com.dicoding.asclepius.view.history

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.local.Asclepius
import com.dicoding.asclepius.databinding.ItemRowHistoryBinding
import com.dicoding.asclepius.view.result.ResultActivity

class HistoryAdapter : ListAdapter<Asclepius, HistoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    fun setListAsclepius(listNotes: List<Asclepius>) {
        submitList(listNotes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRowHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MyViewHolder(private val binding: ItemRowHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(asclepius: Asclepius) {
            with(binding) {
                textDate.text = asclepius.date
                textResult.text = asclepius.result
                textConfidenceScore.text = root.context.getString(R.string.confidence_score_format, asclepius.confidenceScore)
                
                Glide.with(itemView.context)
                    .load(Uri.parse(asclepius.imageUri))
                    .placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_place_holder)
                    .into(ivHistory)

                root.setOnClickListener {
                    val intent = Intent(it.context, ResultActivity::class.java)
                    intent.putExtra(ResultActivity.EXTRA_RESULT_TEXT, asclepius.result)
                    intent.putExtra(ResultActivity.EXTRA_CONFIDENCE_SCORE, asclepius.confidenceScore)
                    intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, asclepius.imageUri)
                    it.context.startActivity(intent)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Asclepius>() {
            override fun areItemsTheSame(oldItem: Asclepius, newItem: Asclepius): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Asclepius, newItem: Asclepius): Boolean {
                return oldItem == newItem
            }
        }
    }
}
