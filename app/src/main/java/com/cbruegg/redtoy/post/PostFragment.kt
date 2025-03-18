package com.cbruegg.redtoy.post

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
class PostFragment : Fragment() {
    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_post, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sharePost -> {
                sharePost()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun sharePost() {
        val post = viewModel.post.value
        if (post != null) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, post.url)
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_post)))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
