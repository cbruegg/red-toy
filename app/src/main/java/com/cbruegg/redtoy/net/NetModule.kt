package com.cbruegg.redtoy.net

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import dagger.Binds
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
abstract class NetModule {
    @Binds
    abstract fun bindSimplifiedRedditService(simplifiedRedditService: SimplifiedRedditServiceImpl): SimplifiedRedditService

    companion object {
        @Provides
        fun provideMoshi(): Moshi {
            val postContentChildAdapter =
                PolymorphicJsonAdapterFactory.of(PostContentChild::class.java, "kind")
                    .withSubtype(PostContentChild.PostChild::class.java, "t3")
                    .withSubtype(PostContentChild.CommentChild::class.java, "t1")
            return Moshi.Builder()
                .add(postContentChildAdapter)
                .build()
        }

        @Provides
        fun provideRedditService(moshi: Moshi): RedditService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://www.reddit.com/")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
            return retrofit.create()
        }
    }

}