package com.example.zipporeppogithub.ui.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.databinding.HistoryItemBinding
import com.example.zipporeppogithub.model.db.HistoryRecord
import java.util.ArrayList

class HistoryRecyclerAdapter : RecyclerView.Adapter<HistoryRecyclerAdapter.VH>() {
    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = HistoryItemBinding.bind(itemView)
    }

    private var items: List<HistoryRecord> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.history_item, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.binding.repoTitle.text = item.repoName
        holder.binding.username.text = item.userLogin
        holder.binding.downloadDate.text = item.getFormattedDate()
    }

    override fun getItemCount() = items.size

    @SuppressLint("NotifyDataSetChanged") //all of them changed
    fun setData(items: List<HistoryRecord>) {
        this.items = items
        notifyDataSetChanged()
    }
}