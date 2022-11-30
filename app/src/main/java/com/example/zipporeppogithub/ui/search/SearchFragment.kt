package com.example.zipporeppogithub.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.appComponent
import com.example.zipporeppogithub.databinding.SearchFragmentBinding
import com.example.zipporeppogithub.utils.viewModelsExt

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

        binding.usersList.apply {
            layoutManager = linearLayoutManager
            adapter = recyclerAdapter
        }

        viewModel.users.observe(viewLifecycleOwner) {
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

        viewModel.error.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.messageText.visibility = View.GONE
                binding.retryBtn.visibility = View.GONE
            } else {
                binding.messageText.setText(it)
                binding.messageText.visibility = View.VISIBLE
                binding.retryBtn.visibility = View.VISIBLE
            }
        }

        viewModel.isAnswerEmpty.observe(viewLifecycleOwner) {
            if (it) {
                binding.messageText.setText(R.string.empty_result_text)
                binding.messageText.visibility = View.VISIBLE
            } else {
                binding.messageText.visibility = View.GONE
            }
        }

        viewModel.navigateToUserRepos.observe(viewLifecycleOwner) {
            findNavController().navigate(
                SearchFragmentDirections.actionSearchToReposFragment(it)
            )
        }

        binding.searchBar.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                viewModel.onSearchTextChanged(s.toString())
            }
        })

        binding.retryBtn.setOnClickListener { viewModel.retryBtnClicked(binding.searchBar.text.toString()) }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}