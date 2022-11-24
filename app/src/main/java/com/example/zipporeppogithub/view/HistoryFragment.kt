package com.example.zipporeppogithub.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.zipporeppogithub.appComponent
import com.example.zipporeppogithub.databinding.HistoryFragmentBinding
import com.example.zipporeppogithub.utils.viewModelsExt
import com.example.zipporeppogithub.viewmodels.HistoryViewModel

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

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}