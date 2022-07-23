package com.cbruegg.redtoy.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbruegg.redtoy.net.Post
import com.cbruegg.redtoy.net.SimplifiedRedditService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(private val simplifiedRedditService: SimplifiedRedditService): ViewModel() {

    private val _subreddit = MutableStateFlow("androiddev")
    val subreddit: StateFlow<String> = _subreddit

    private val _posts = MutableStateFlow<List<Post>?>(null)
    val posts: StateFlow<List<Post>?> = _posts

    val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _posts.value = simplifiedRedditService.getPosts(subreddit.value, "hot", 50)
            } catch (e: IOException) {
                // TODO Display error
            } finally {
                _isLoading.value = false
            }
        }
    }

}