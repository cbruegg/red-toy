package com.cbruegg.redtoy.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbruegg.redtoy.net.Post
import com.cbruegg.redtoy.net.SimplifiedRedditService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(private val simplifiedRedditService: SimplifiedRedditService): ViewModel() {

    private val _posts = MutableStateFlow<List<Post>?>(null)
    val posts: StateFlow<List<Post>?> = _posts

    val isLoading = posts.map { it == null }

    init {
        viewModelScope.launch {
            _posts.value = simplifiedRedditService.getPosts("androiddev", "hot", 50)
        }
    }

}