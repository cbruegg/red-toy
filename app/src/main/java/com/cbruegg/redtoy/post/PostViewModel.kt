package com.cbruegg.redtoy.post

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbruegg.redtoy.net.CommentsData
import com.cbruegg.redtoy.net.Post
import com.cbruegg.redtoy.net.SimplifiedRedditService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val simplifiedRedditService: SimplifiedRedditService,
    private val state: SavedStateHandle
): ViewModel() {

    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post

    private val _comments = MutableStateFlow<List<CommentsData>?>(null)
    val comments: StateFlow<List<CommentsData>?> = _comments

    private val _requestedOpenLink = MutableStateFlow<String?>(null)
    val requestedOpenLink: StateFlow<String?> = _requestedOpenLink

    init {
        viewModelScope.launch {
            val args = PostFragmentArgs.fromSavedStateHandle(state)
            val (post, comments) = simplifiedRedditService.getPostData(args.permalink)
            _post.value = post
            _comments.value = comments
        }
    }

    fun onLinkClick() {
        _requestedOpenLink.value = _post.value?.url
    }

    fun didOpenLink() {
        _requestedOpenLink.value = null
    }

}