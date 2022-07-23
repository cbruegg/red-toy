package com.cbruegg.redtoy.posts

import com.cbruegg.redtoy.net.CommentsData
import com.cbruegg.redtoy.net.Post
import com.cbruegg.redtoy.net.SimplifiedRedditService
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger

@DelicateCoroutinesApi
@OptIn(ExperimentalCoroutinesApi::class)
class PostsViewModelTest {

    class MockSimplifiedRedditService : SimplifiedRedditService {
        val posts = listOf(
            Post(
                "author",
                created_utc = 0,
                id = "0",
                is_self = true,
                is_video = false,
                num_comments = 0,
                permalink = "/abc/",
                selftext = "selftest",
                selftext_html = "selftext",
                thumbnail = "/abc.jpg",
                title = "Post Title",
                url = "self",
                preview = null
            )
        )
        val postsSecondTime = posts.map { it.copy(created_utc = it.created_utc + 1) }

        val postData = Pair(
            posts[0], listOf(
                CommentsData(
                    "author", "comment text", created_utc = 0, id = "0", parent_id = "parent_id",
                    score = 0
                )
            )
        )

        private val getPostsCounter = AtomicInteger(0)

        override suspend fun getPosts(subreddit: String, listing: String, limit: Int): List<Post> {
            delay(2000)
            return if (getPostsCounter.getAndIncrement() == 0) posts else postsSecondTime
        }

        override suspend fun getPostData(permalink: String): Pair<Post, List<CommentsData>> {
            delay(2000)
            return postData
        }
    }

    object FailingService: SimplifiedRedditService {
        override suspend fun getPosts(
            subreddit: String,
            listing: String,
            limit: Int
        ) = throw IOException("Network failure!")
        override suspend fun getPostData(permalink: String) = throw IOException("Network failure!")
    }

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    private fun createViewModel(simplifiedRedditService: SimplifiedRedditService = MockSimplifiedRedditService()) =
        PostsViewModel(simplifiedRedditService)

    @Test
    fun getSubreddit() {
        val viewModel = createViewModel()
        assertEquals("Initial subreddit should be /r/androiddev", viewModel.subreddit.value, "androiddev")
    }

    @Test
    fun getPosts() {
        val viewModel = createViewModel()
        Thread.sleep(2500)
        assertTrue("ViewModel should have loaded posts eventually", !viewModel.posts.value.isNullOrEmpty())
    }

    @Test
    fun isLoading() {
        val viewModel = createViewModel()
        assertTrue("ViewModel should initially be loading", viewModel.isLoading.value)
        Thread.sleep(2500)
        assertFalse("ViewModel should stop loading eventually", viewModel.isLoading.value)
    }

    @Test
    fun getPendingNetworkError() {
        val viewModel = createViewModel()
        assertFalse(
            "ViewModel should initially not have any pending network error",
            viewModel.pendingNetworkError.value
        )
        Thread.sleep(2500)
        assertFalse(
            "ViewModel should not have any pending network error after successful request",
            viewModel.pendingNetworkError.value
        )

        val viewModelWithFailingService = createViewModel(FailingService)
        Thread.sleep(1000)
        assertTrue("ViewModel should have pending network error after network failure", viewModelWithFailingService.pendingNetworkError.value)

        viewModelWithFailingService.setUserHasSeenError()
        assertFalse("ViewModel should have no pending network error after setUserHasSeenError()", viewModelWithFailingService.pendingNetworkError.value)
    }

    @Test
    fun refresh() {
        val viewModel = createViewModel()
        Thread.sleep(2500)
        val postsBeforeRefresh = viewModel.posts.value
        viewModel.refresh()
        Thread.sleep(2500)
        val postsAfterRefresh = viewModel.posts.value
        assertNotEquals("Posts should be different after refreshing", postsBeforeRefresh, postsAfterRefresh)
    }

}