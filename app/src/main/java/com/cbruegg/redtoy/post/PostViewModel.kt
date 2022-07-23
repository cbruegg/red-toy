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

// TODO How to save ViewModel state?

@HiltViewModel
class PostViewModel @Inject constructor(
    private val simplifiedRedditService: SimplifiedRedditService,
    private val state: SavedStateHandle
): ViewModel() {

    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post

    private val _comments = MutableStateFlow<List<CommentsData>?>(null)
    val comments: StateFlow<List<CommentsData>?> = _comments

    init {
        viewModelScope.launch {
            val permalink = state.get<String>("permalink") ?: throw IllegalArgumentException("State is missing permalink!")
            val (post, comments) = simplifiedRedditService.getPostData(permalink)
            _post.value = post
            _comments.value = comments
        }
    }

}