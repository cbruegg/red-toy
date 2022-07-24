package com.cbruegg.redtoy.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbruegg.redtoy.Repository
import com.cbruegg.redtoy.db.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class PostsViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {

    private val _subreddit = MutableStateFlow("androiddev")
    val subreddit: StateFlow<String> = _subreddit

    val posts: StateFlow<List<Post>?> = _subreddit
        .flatMapConcat { subreddit -> repository.postsOfSubreddit(subreddit) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _pendingNetworkError = MutableStateFlow(false)
    /**
     * Emits `true` if there was a network error. The UI may display a warning in this case.
     * Call [setUserHasSeenError] afterwards.
     */
    val pendingNetworkError: StateFlow<Boolean> = _pendingNetworkError

    private val _postToOpen = MutableStateFlow<Post?>(null)
    val postToOpen: StateFlow<Post?> = _postToOpen

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.updateSubreddit(subreddit.value)
            } catch (e: IOException) {
                e.printStackTrace()
                _pendingNetworkError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setUserHasSeenError() {
        _pendingNetworkError.value = false
    }

    fun clickedPost(post: Post) {
        _postToOpen.value = post
    }

    fun didOpenPost() {
        _postToOpen.value = null
    }

}