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
import androidx.recyclerview.widget.RecyclerView
import com.example.zipporeppogithub.appComponent
import com.example.zipporeppogithub.databinding.ReposFragmentBinding
import com.example.zipporeppogithub.utils.viewModelsExt
import com.google.android.material.snackbar.Snackbar

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

        val recyclerAdapter =
            ReposRecyclerAdapter(viewModel::downloadBtnClicked, viewModel::linkBtnClicked)
        val linearLayoutManager = LinearLayoutManager(context)

        binding.reposList.apply {
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

        viewModel.additionalRepos.observe(viewLifecycleOwner) {
            recyclerAdapter.addUsers(it)
        }

        viewModel.snackMessage.observe(viewLifecycleOwner) {
            Snackbar.make(binding.reposList, it, Snackbar.LENGTH_LONG).show()
        }

        viewModel.url.observe(viewLifecycleOwner) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(it)
            startActivity(intent)
        }

        binding.retryBtn.setOnClickListener { viewModel.retryBtnClicked() }

        binding.reposList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(unused: RecyclerView, dx: Int, dy: Int) {
                viewModel.onScrolledToEnd(
                    linearLayoutManager.findLastVisibleItemPosition(),
                    linearLayoutManager.itemCount
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