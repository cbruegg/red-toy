package com.cbruegg.redtoy.posts

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbruegg.redtoy.R
import com.cbruegg.redtoy.databinding.FragmentPostsBinding
import com.cbruegg.redtoy.util.navigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class PostsFragment : Fragment() {

    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_posts, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.refreshPosts -> {
                        viewModel.refresh()
                        true
                    }
                    else -> false
                }
            }
        }, this, Lifecycle.State.RESUMED)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostsBinding.inflate(inflater, container, false)


        val postAdapter = PostAdapter(emptyList()) { post ->
            navigate(
                PostsFragmentDirections.actionPostsFragmentToPostFragment(
                    post.permalink
                )
            )
        }
        binding.postsList.adapter = postAdapter
        binding.postsList.layoutManager = LinearLayoutManager(context)

        binding.postsSwipeRefresh.setOnRefreshListener { viewModel.refresh() }

        // TODO Ensure used all libraries from note

        viewModel.subreddit.flowWithLifecycle(lifecycle)
            .onEach { subreddit ->
                activity?.title = "/r/$subreddit"
            }
            .launchIn(lifecycleScope)

        viewModel.posts.flowWithLifecycle(lifecycle)
            .onEach { posts ->
                postAdapter.posts = posts ?: emptyList()
                postAdapter.notifyDataSetChanged()
            }
            .launchIn(lifecycleScope)

        viewModel.isLoading.flowWithLifecycle(lifecycle)
            .onEach { isLoading ->
                binding.postsSwipeRefresh.isRefreshing = isLoading
            }
            .launchIn(lifecycleScope)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}