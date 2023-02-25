package com.example.zipporeppogithub.ui.repos

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.appComponent
import com.example.zipporeppogithub.databinding.ReposFragmentBinding
import com.example.zipporeppogithub.utils.viewModelsExt
import com.google.android.material.snackbar.Snackbar


class ReposFragment : Fragment() {

    private var activityResultLauncher: ActivityResultLauncher<Array<String>>

    init {
        this.activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result -> //todo передать result в VM, там обработать, если есть разрешение - качать, нет - сообщить
            viewModel.setPermissionAnswer(result)
        }
    }

    private var _binding: ReposFragmentBinding? = null
    private val binding
        get() = _binding!!

    private val args: ReposFragmentArgs by navArgs()

    private val viewModel: ReposViewModel by viewModelsExt {
        requireContext().appComponent.provideReposViewModelFactory().create(
            args.userLogin,
            Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).path
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
        val dividerItemDecoration =
            DividerItemDecoration(binding.reposList.context, linearLayoutManager.orientation)
        dividerItemDecoration.setDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.divider_item_layout,
                null
            )!!
        )

        binding.reposList.apply {
            layoutManager = linearLayoutManager
            adapter = recyclerAdapter
            addItemDecoration(dividerItemDecoration)
        }

        binding.retryBtn.setOnClickListener { viewModel.retryBtnClicked() }

        binding.reposList.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(unused: RecyclerView, dx: Int, dy: Int) {
                viewModel.onScrolledToEnd(
                    linearLayoutManager.findLastVisibleItemPosition(),
                    linearLayoutManager.itemCount
                )
            }
        })

        viewModel.reposToShow.observe(viewLifecycleOwner) { recyclerAdapter.setData(it) }

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
            recyclerAdapter.addReposToShow(it)
        }

        viewModel.snackMessage.observe(viewLifecycleOwner) {
            Snackbar.make(binding.reposList, it, Snackbar.LENGTH_LONG).show()
        }

        viewModel.url.observe(viewLifecycleOwner) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(it)
            startActivity(intent)
        }

        viewModel.isPermissionRequested.observe(viewLifecycleOwner) {
            val externalPerms = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            activityResultLauncher.launch(externalPerms)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
