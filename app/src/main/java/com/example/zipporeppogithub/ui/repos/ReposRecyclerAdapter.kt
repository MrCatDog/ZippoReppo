package com.example.zipporeppogithub.ui.repos

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.databinding.RepoItemBinding
import com.example.zipporeppogithub.model.network.GithubRepo
import com.example.zipporeppogithub.ui.search.SearchViewModel
import com.example.zipporeppogithub.utils.REPOS_RESULT_COUNT
import java.util.ArrayList

class ReposRecyclerAdapter(
    private val downloadBtnListener: (GithubRepo) -> Unit,
    private val linkBtnListener: (GithubRepo) -> Unit
) :
    RecyclerView.Adapter<ReposRecyclerAdapter.VH>() {
    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RepoItemBinding.bind(itemView)
    }

    private var items: ArrayList<GithubRepo> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.repo_item, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.binding.repoTitle.text = item.name

        holder.binding.repoDownload.setOnClickListener {
            downloadBtnListener(item)
        }

        holder.binding.repoOpenInBrowser.setOnClickListener {
            linkBtnListener(item)
        }
    }

    override fun getItemCount() = items.size

    fun addUsers(moreRepos: List<GithubRepo>) {
        val lastPos = itemCount
        items.addAll(moreRepos)
        notifyItemRangeChanged(lastPos, REPOS_RESULT_COUNT)
    }

    @SuppressLint("NotifyDataSetChanged") //all of them changed
    fun setData(items: List<GithubRepo>) {
        this.items = ArrayList(items)
        notifyDataSetChanged()
    }
}