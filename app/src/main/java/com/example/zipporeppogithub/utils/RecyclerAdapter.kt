package com.example.zipporeppogithub.utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.databinding.SearchResultItemBinding
import com.example.zipporeppogithub.model.network.GithubUser
import java.util.ArrayList

class RecyclerAdapter(private val listener: (GithubUser) -> Unit) : RecyclerView.Adapter<RecyclerAdapter.VH>() {
    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = SearchResultItemBinding.bind(itemView)
    }

    private var items: List<GithubUser> = ArrayList()

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
    fun setData(items: List<GithubUser>) {
        this.items = items
        notifyDataSetChanged()
    }
}