package com.cbruegg.redtoy.posts

import android.os.Bundle
import android.view.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cbruegg.redtoy.R
import com.cbruegg.redtoy.databinding.FragmentPostsComposeBinding
import com.cbruegg.redtoy.db.Post
import com.cbruegg.redtoy.util.navigate
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class PostsComposeFragment : Fragment() {

    private var _binding: FragmentPostsComposeBinding? = null
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
                    R.id.toggleCompose -> {
                        navigate(PostsComposeFragmentDirections.actionPostsComposeFragmentToPostsFragment())
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
        _binding = FragmentPostsComposeBinding.inflate(inflater, container, false)

        viewModel.postToOpen.flowWithLifecycle(lifecycle)
            .onEach { post ->
                if (post != null) {
                    navigate(
                        PostsComposeFragmentDirections.actionPostsComposeFragmentToPostFragment(
                            post.permalink,
                            post.id,
                            viewModel.subreddit.value
                        )
                    )
                    viewModel.didOpenPost()
                }
            }
            .launchIn(lifecycleScope)

        viewModel.subreddit.flowWithLifecycle(lifecycle)
            .onEach { subreddit ->
                activity?.title = "/r/$subreddit"
            }
            .launchIn(lifecycleScope)

        viewModel.pendingNetworkError.flowWithLifecycle(lifecycle)
            .onEach { pendingNetworkError ->
                if (pendingNetworkError) {
                    view?.let {
                        Snackbar.make(it, R.string.network_error, Snackbar.LENGTH_LONG)
                        viewModel.setUserHasSeenError()
                    }
                }
            }
            .launchIn(lifecycleScope)

        binding.postsComposeView.setContent {
            MdcTheme {
                Posts(viewModel)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@Composable
private fun Posts(viewModel: PostsViewModel) {
    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    SwipeRefresh(
        state = rememberSwipeRefreshState(isLoading),
        onRefresh = { viewModel.refresh() },
    ) {
        posts?.let { posts ->
            LazyColumn {
                items(posts.size, key = { index -> posts[index].id }) { index ->
                    val post = posts[index]
                    Post(post, modifier = Modifier.clickable {
                        viewModel.clickedPost(post)
                    })
                }
            }
        }
    }
}

@Composable
@Preview
private fun SelfPostPreview() {
    Post(
        Post(
            author = "Some other author",
            createdUtc = 0,
            id = "1",
            isSelf = true,
            isVideo = false,
            numComments = 0,
            permalink = "/abc",
            selftext = "This is a self text",
            selftextHtml = null,
            thumbnail = "https://cbruegg.com/wp-content/uploads/2015/10/mensa-upb-pebble-thumb.jpg",
            title = "Some Post Title",
            url = "/abcd",
            subreddit = "androiddev"
        )
    )
}

@Composable
@Preview
private fun LinkPostPreview() {
    Post(
        Post(
            author = "Some author",
            createdUtc = 0,
            id = "0",
            isSelf = false,
            isVideo = false,
            numComments = 0,
            permalink = "/abc",
            selftext = null,
            selftextHtml = null,
            thumbnail = "https://cbruegg.com/wp-content/uploads/2015/10/mensa-upb-pebble-thumb.jpg",
            title = "Some Post Title",
            url = "/abcd",
            subreddit = "androiddev"
        )
    )
}

@Composable
private fun Post(post: Post, modifier: Modifier = Modifier) {
    Card(modifier = Modifier.padding(8.dp).then(modifier)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
            PostImage(post)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = post.title, style = MaterialTheme.typography.h3, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = post.author, style = MaterialTheme.typography.subtitle1, color = Color.DarkGray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun PostImage(post: Post, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        if (post.thumbnail == "self") {
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_article_24),
                contentDescription = null,
                modifier = Modifier.size(72.dp)
            )
        } else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(post.thumbnail)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.size(72.dp)
            )
        }
    }
}