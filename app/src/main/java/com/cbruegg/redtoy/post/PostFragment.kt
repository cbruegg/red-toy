package com.cbruegg.redtoy.post

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
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

        val postAdapter = PostContentAdapter(null, emptyList(), onLinkClick = viewModel::onLinkClick, Markwon.create(requireContext()))
        val layoutManager = LinearLayoutManager(context)
        binding.postContentList.adapter = postAdapter
        binding.postContentList.layoutManager = layoutManager
        binding.postContentList.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))

        viewModel.pendingNetworkError.flowWithLifecycle(lifecycle)
            .onEach { pendingNetworkError ->
                if (pendingNetworkError) {
                    view?.let {
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

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.post_menu, menu)

        val shareMenuItem = menu.findItem(R.id.share_post)
        shareMenuItem.setOnMenuItemClickListener {
            onSharePostOptionsItemSelected()
            true
        }
    }

    private fun onSharePostOptionsItemSelected() {

        val post = viewModel.post.value
        if (post != null) {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Shared post:\n${post.content}")

            // Optional: Add title and image if available
            post.title?.let { shareIntent.putExtra(Intent.EXTRA_SUBJECT, it) }
            // Note: For images, you would need to handle loading from network first

            shareIntent.type = "text/plain"
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            Intent.createChooser(shareIntent, getString(R.string.share_post)).also {
                startActivity(it)
            }
        } else {
            Snackbar.make(binding.root, R.string.no_post_to_share, Snackbar.LENGTH_SHORT).show()
        }
    }

}
