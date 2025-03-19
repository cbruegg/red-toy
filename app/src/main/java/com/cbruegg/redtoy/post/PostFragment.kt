package com.cbruegg.redtoy.post

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbruegg.redtoy.R
import com.cbruegg.redtoy.databinding.FragmentPostBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postAdapter = PostContentAdapter(null, emptyList(), onLinkClick = viewModel::onLinkClick, Markwon.create(requireContext()))
        val layoutManager = LinearLayoutManager(context)
        binding.postContentList.adapter = postAdapter
        binding.postContentList.layoutManager = layoutManager
        binding.postContentList.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))

        viewModel.pendingNetworkError.flowWithLifecycle(lifecycle)
            .onEach { pendingNetworkError ->
                if (pendingNetworkError) {
                    view.let {
                        Snackbar.make(it, R.string.network_error, Snackbar.LENGTH_LONG).show()
                        viewModel.setUserHasSeenError()
                    }
                }
            }
            .launchIn(lifecycleScope)

        viewModel.post.flowWithLifecycle(lifecycle)
            .onEach { post ->
                postAdapter.post = post
                postAdapter.notifyDataSetChanged()
            }
            .launchIn(lifecycleScope)

        viewModel.comments.flowWithLifecycle(lifecycle)
            .onEach { comments ->
                postAdapter.comments = comments
                postAdapter.notifyDataSetChanged()
            }
            .launchIn(lifecycleScope)

        viewModel.requestedOpenLink.flowWithLifecycle(lifecycle)
            .onEach { link ->
                if (link != null) {
                    startActivity(Intent.parseUri(link, 0))
                    viewModel.didOpenLink()
                }
            }
            .launchIn(lifecycleScope)

        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_post, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.sharePost -> {
                        val post = viewModel.post.value
                        if (post != null) {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Check out this post: ${post.title} ${post.url}") // Customize the shared content here
                                type = "text/plain"
                            }

                            val shareIntent = Intent.createChooser(sendIntent, null)
                            startActivity(shareIntent)
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
