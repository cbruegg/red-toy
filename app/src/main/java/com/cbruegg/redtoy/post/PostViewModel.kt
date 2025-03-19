package com.cbruegg.redtoy.post

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbruegg.redtoy.Repository
import com.cbruegg.redtoy.db.Comment
import com.cbruegg.redtoy.db.Post
import com.cbruegg.redtoy.db.PostId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: Repository,
    private val state: SavedStateHandle
) : ViewModel() {

    private val _postId = MutableStateFlow<PostId?>(null)

    val post: StateFlow<Post?> = _postId
        .flatMapConcat { postId -> if (postId != null) repository.post(postId) else emptyFlow() }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val comments: StateFlow<List<Comment>> = _postId
        .flatMapConcat { postId -> if (postId != null) repository.commentsOfPost(postId) else emptyFlow() }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _requestedOpenLink = MutableStateFlow<String?>(null)
    /**
     * Emits a link when the user clicks it. Call [didOpenLink] once the browser has been started.
     */
    val requestedOpenLink: StateFlow<String?> = _requestedOpenLink

    private val _pendingNetworkError = MutableStateFlow(false)
    /**
     * Emits `true` if there was a network error. The UI may display a warning in this case.
     * Call [setUserHasSeenError] afterwards.
     */
    val pendingNetworkError: StateFlow<Boolean> = _pendingNetworkError

    init {
        viewModelScope.launch {
            val args = PostFragmentArgs.fromSavedStateHandle(state)
            val subreddit = args.subreddit
            val permalink = args.permalink
            val postId = args.postId
            _postId.value = postId
            try {
                repository.updateDataOfPost(permalink, subreddit)
            } catch (e: IOException) {
                e.printStackTrace()
                _pendingNetworkError.value = true
            }
        }
    }

    fun onLinkClick() {
        _requestedOpenLink.value = post.value?.url
    }

    fun didOpenLink() {
        _requestedOpenLink.value = null
    }

    fun setUserHasSeenError() {
        _pendingNetworkError.value = false
    }

}