package com.example.zipporeppogithub.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.appComponent
import com.example.zipporeppogithub.databinding.SearchFragmentBinding
import com.example.zipporeppogithub.utils.viewModelsExt
import com.google.android.material.snackbar.Snackbar

class SearchFragment : Fragment() {
    private var _binding: SearchFragmentBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: SearchViewModel by viewModelsExt {
        requireContext().appComponent.provideSearchViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SearchFragmentBinding.inflate(inflater)

        val recyclerAdapter = SearchRecyclerAdapter(viewModel::listItemClicked)
        val linearLayoutManager = LinearLayoutManager(context)
        val dividerItemDecoration =
            DividerItemDecoration(binding.usersList.context, linearLayoutManager.orientation)
        dividerItemDecoration.setDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.divider_item_layout,
                null
            )!!
        )

        binding.usersList.apply {
            layoutManager = linearLayoutManager
            adapter = recyclerAdapter
            addItemDecoration(dividerItemDecoration)
        }

        viewModel.users.observe(viewLifecycleOwner) {
            recyclerAdapter.setNewUsers(it)
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

        viewModel.navigateToUserRepos.observe(viewLifecycleOwner) {
            findNavController().navigate(
                SearchFragmentDirections.actionSearchToReposFragment(it)
            )
        }

        viewModel.additionalUsers.observe(viewLifecycleOwner) {
            recyclerAdapter.addUsers(it)
        }

        viewModel.snackMessage.observe(viewLifecycleOwner) {
            Snackbar.make(binding.usersList, it, Snackbar.LENGTH_LONG).show()
        }

        binding.searchBar.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (binding.searchBar.hasFocus()) {
                    viewModel.onSearchTextChanged(s.toString())
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {}
        })

        binding.retryBtn.setOnClickListener { viewModel.retryBtnClicked(binding.searchBar.text.toString()) }

        binding.usersList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(unused: RecyclerView, dx: Int, dy: Int) {
                viewModel.onScrolledToEnd(
                    linearLayoutManager.findLastVisibleItemPosition(),
                    linearLayoutManager.itemCount,
                    binding.searchBar.text.toString()
                )
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
