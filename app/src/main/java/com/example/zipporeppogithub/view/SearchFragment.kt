package com.example.zipporeppogithub.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.zipporeppogithub.appComponent
import com.example.zipporeppogithub.databinding.SearchFragmentBinding
import com.example.zipporeppogithub.utils.viewModelsExt
import com.example.zipporeppogithub.viewmodels.SearchViewModel

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

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressbar.visibility =
                if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}