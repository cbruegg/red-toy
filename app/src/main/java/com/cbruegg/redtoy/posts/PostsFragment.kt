package com.cbruegg.redtoy.posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbruegg.redtoy.databinding.FragmentPostsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class PostsFragment : Fragment() {

    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostsBinding.inflate(inflater, container, false)


        val postAdapter = PostAdapter(emptyList()) { post ->
            findNavController().navigate(PostsFragmentDirections.actionPostsFragmentToPostFragment(post.permalink))
        }
        binding.postsList.adapter = postAdapter
        binding.postsList.layoutManager = LinearLayoutManager(context)

        // TODO Add refresh button
        // TODO Ensure used all libraries from note

        viewModel.posts.flowWithLifecycle(lifecycle)
            .onEach { posts ->
                postAdapter.posts = posts ?: emptyList()
                postAdapter.notifyDataSetChanged()
            }
            .launchIn(lifecycleScope)

        viewModel.isLoading.flowWithLifecycle(lifecycle)
            .onEach { isLoading -> binding.postsListProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE }
            .launchIn(lifecycleScope)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}