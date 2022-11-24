package com.example.zipporeppogithub.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.zipporeppogithub.appComponent
import com.example.zipporeppogithub.databinding.ReposFragmentBinding
import com.example.zipporeppogithub.utils.viewModelsExt
import com.example.zipporeppogithub.viewmodels.ReposViewModel

class ReposFragment : Fragment() {

    private var _binding: ReposFragmentBinding? = null
    private val binding
        get() = _binding!!

    private val args: ReposFragmentArgs by navArgs()

    private val viewModel: ReposViewModel by viewModelsExt {
        requireContext().appComponent.provideReposViewModelFactory().create(args.userLogin)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ReposFragmentBinding.inflate(inflater)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}