package com.cbruegg.redtoy.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbruegg.redtoy.databinding.FragmentPostBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@AndroidEntryPoint
class PostFragment: Fragment() {
    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)

        val postAdapter = PostContentAdapter(null, emptyList())
        binding.postContentList.adapter = postAdapter
        binding.postContentList.layoutManager = LinearLayoutManager(context)

        // TODO Add refresh button? Or pull to refresh?

        val postAdapterMutex = Mutex()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.post.collect { post ->
                    postAdapterMutex.withLock { // TODO Is this lock even needed? This runs on Dispatcher.Main, so should be single-threaded
                        postAdapter.post = post
                        postAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.comments.collect { comments ->
                    postAdapterMutex.withLock {
                        postAdapter.comments = comments ?: emptyList()
                        postAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}