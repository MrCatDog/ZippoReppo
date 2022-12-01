package com.example.zipporeppogithub.ui.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.databinding.SearchResultItemBinding
import com.example.zipporeppogithub.model.network.GithubUserSearchResult
import com.example.zipporeppogithub.utils.USERS_RESULT_COUNT
import kotlin.collections.ArrayList

class SearchRecyclerAdapter(private val listener: (GithubUserSearchResult.User) -> Unit) :
    RecyclerView.Adapter<SearchRecyclerAdapter.VH>() {
    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = SearchResultItemBinding.bind(itemView)
    }

    private var items: ArrayList<GithubUserSearchResult.User> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.search_result_item, parent, false
            )
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
    fun setNewUsers(items: List<GithubUserSearchResult.User>) {
        this.items = ArrayList(items)
        notifyDataSetChanged()
    }

    fun addUsers(moreUsers: List<GithubUserSearchResult.User>) {
        val lastPos = itemCount
        items.addAll(moreUsers)
        notifyItemRangeChanged(lastPos, USERS_RESULT_COUNT)
    }
}