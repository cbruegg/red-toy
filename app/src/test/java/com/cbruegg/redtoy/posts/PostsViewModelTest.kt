package com.cbruegg.redtoy.posts

import com.cbruegg.redtoy.Repository
import com.cbruegg.redtoy.db.Comment
import com.cbruegg.redtoy.db.Post
import com.cbruegg.redtoy.db.PostId
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
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

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    inner class MockRepository : Repository {
        private val posts = listOf(
            Post(
                author = "author",
                createdUtc = 0,
                id = "0",
                isSelf = true,
                isVideo = false,
                numComments = 0,
                permalink = "/abc/",
                selftext = "selftest",
                selftextHtml = "selftext",
                thumbnail = "/abc.jpg",
                title = "Post Title",
                url = "self",
                subreddit = "androiddev"
            )
        )
        private val postsSecondTime = posts.map { it.copy(createdUtc = it.createdUtc + 1) }

        private val comments = run {
            var counter = 0
            posts.map { post ->
                Comment(
                    author = "author",
                    body = "comment text",
                    createdUtc = 0,
                    id = run { counter += 1; counter.toString() },
                    parentId = "parent_id",
                    score = 0,
                    postId = post.id,
                )
            }
        }

        private val postsFlow = MutableStateFlow(emptyList<Post>())

        private val updates = AtomicInteger(0)

        override fun postsOfSubreddit(subreddit: String) = postsFlow

        override fun commentsOfPost(postId: PostId) =
            flowOf(comments.filter { it.postId == postId })

        override fun post(postId: PostId) =
            postsFlow.map { it.single { post -> post.id == postId } }

        override suspend fun updateSubreddit(subreddit: String) {
            withContext(mainThreadSurrogate) {
                delay(1500)
            }
            if (updates.getAndIncrement() == 0) {
                postsFlow.value = posts
            } else {
                postsFlow.value = postsSecondTime
            }
        }

        override suspend fun updateDataOfPost(permalink: String, subreddit: String) {
            withContext(mainThreadSurrogate) {
                delay(1500)
            }
        }

    }

    object FailingServiceRepository : Repository {
        override fun postsOfSubreddit(subreddit: String) = emptyFlow<List<Post>>()

        override fun commentsOfPost(postId: PostId) = emptyFlow<List<Comment>>()

        override fun post(postId: PostId) = emptyFlow<Post>()

        override suspend fun updateSubreddit(subreddit: String) =
            throw IOException("Network failure!")

        override suspend fun updateDataOfPost(permalink: String, subreddit: String) =
            throw IOException("Network failure!")

    }

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    private fun createViewModel(repository: Repository = MockRepository()): PostsViewModel {
        return PostsViewModel(repository)
    }

    @Test
    fun getSubreddit() {
        val viewModel = createViewModel()
        assertEquals(
            "Initial subreddit should be /r/androiddev",
            viewModel.subreddit.value,
            "androiddev"
        )
    }

    @Test
    fun getPosts() = runBlocking { // not runTest as we want delays to work
        val viewModel = createViewModel()
        val job = launch { viewModel.posts.collect() } // Start the flow

        delay(2500)
        assertTrue(
            "ViewModel should have loaded posts eventually",
            !viewModel.posts.value.isNullOrEmpty()
        )

        job.cancelAndJoin()
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

        val viewModelWithFailingService = createViewModel(FailingServiceRepository)
        Thread.sleep(1000)
        assertTrue(
            "ViewModel should have pending network error after network failure",
            viewModelWithFailingService.pendingNetworkError.value
        )

        viewModelWithFailingService.setUserHasSeenError()
        assertFalse(
            "ViewModel should have no pending network error after setUserHasSeenError()",
            viewModelWithFailingService.pendingNetworkError.value
        )
    }

    @Test
    fun refresh() = runBlocking { // not runTest as we want delays to work
        val viewModel = createViewModel()
        val job = launch { viewModel.posts.collect() } // Start the flow

        delay(2500)
        val postsBeforeRefresh = viewModel.posts.value
        viewModel.refresh()
        delay(2500)
        val postsAfterRefresh = viewModel.posts.value
        assertNotEquals(
            "Posts should be different after refreshing",
            postsBeforeRefresh,
            postsAfterRefresh
        )

        job.cancelAndJoin()
    }

}