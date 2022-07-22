package com.cbruegg.redtoy.net

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

@InstallIn(ViewModelComponent::class, FragmentComponent::class)
@Module
object NetModule {
    @Provides
    fun provideRedditService(): RedditService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.reddit.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        return retrofit.create()
    }
}