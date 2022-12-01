package com.example.zipporeppogithub.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.appComponent
import com.example.zipporeppogithub.databinding.HistoryFragmentBinding
import com.example.zipporeppogithub.utils.viewModelsExt

class HistoryFragment : Fragment() {
    private var _binding: HistoryFragmentBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: HistoryViewModel by viewModelsExt {
        requireContext().appComponent.provideHistoryViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HistoryFragmentBinding.inflate(inflater)

        val recyclerAdapter = HistoryRecyclerAdapter()
        val linearLayoutManager = LinearLayoutManager(context)
        val dividerItemDecoration =
            DividerItemDecoration(binding.historyList.context, linearLayoutManager.orientation)
        dividerItemDecoration.setDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.divider_item_layout,
                null
            )!!
        )

        binding.historyList.apply {
            layoutManager = linearLayoutManager
            adapter = recyclerAdapter
            addItemDecoration(dividerItemDecoration)
        }

        viewModel.historyRecords.observe(viewLifecycleOwner) {
            recyclerAdapter.setData(it)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressbar.visibility =
                if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        viewModel.message.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.messageText.visibility = View.GONE
            } else {
                binding.messageText.setText(it)
                binding.messageText.visibility = View.VISIBLE
            }
        }

        viewModel.isError.observe(viewLifecycleOwner) {
            if (it) {
                binding.retryBtn.visibility = View.VISIBLE
            } else {
                binding.retryBtn.visibility = View.GONE
            }
        }

        binding.retryBtn.setOnClickListener { viewModel.getHistory() }

        viewModel.getHistory()
        //наверное это самый простой способ обновлять состояние,
        // когда пользователь сюда заглядывает

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
