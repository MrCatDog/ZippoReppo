package com.example.zipporeppogithub.ui.repos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zipporeppogithub.appComponent
import com.example.zipporeppogithub.databinding.ReposFragmentBinding
import com.example.zipporeppogithub.utils.viewModelsExt


class ReposFragment : Fragment() {

    private var _binding: ReposFragmentBinding? = null
    private val binding
        get() = _binding!!

    private val args: ReposFragmentArgs by navArgs()

    private val viewModel: ReposViewModel by viewModelsExt {
        requireContext().appComponent.provideReposViewModelFactory().create(
            args.userLogin,
            DIRECTORY_DOWNLOADS //todo
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ReposFragmentBinding.inflate(inflater)

        val recyclerAdapter = ReposRecyclerAdapter(viewModel::downloadBtnClicked, viewModel::linkBtnClicked)
        val linearLayoutManager = LinearLayoutManager(context)

        binding.usersList.apply {
            layoutManager = linearLayoutManager
            adapter = recyclerAdapter
        }

        viewModel.repos.observe(viewLifecycleOwner) {
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

        viewModel.url.observe(viewLifecycleOwner) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(it)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}