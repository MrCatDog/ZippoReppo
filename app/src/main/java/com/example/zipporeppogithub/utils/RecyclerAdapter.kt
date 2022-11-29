package com.example.zipporeppogithub.utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.databinding.SearchResultItemBinding
import com.example.zipporeppogithub.model.network.GithubUserSearchResult
import java.util.ArrayList

class RecyclerAdapter(private val listener: (GithubUserSearchResult.User) -> Unit) : RecyclerView.Adapter<RecyclerAdapter.VH>() {
    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = SearchResultItemBinding.bind(itemView)
    }

    private var items: List<GithubUserSearchResult.User> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.search_result_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.binding.username.text = item.username

        holder.binding.root.setOnClickListener {
            listener(item)
        }
    }

    override fun getItemCount() = items.size

    @SuppressLint("NotifyDataSetChanged") //all of them changed
    fun setData(items: List<GithubUserSearchResult.User>) {
        this.items = items
        notifyDataSetChanged()
    }
}