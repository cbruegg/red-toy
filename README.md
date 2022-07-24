# Red(dit) Toy: An Architectural Playground
This app is a sample project for experimenting with various technologies and patterns involved in modern Android development:

- Jetpack Compose (Android's new, reactive UI framework)
- Dagger Hilt (simplified dependency injection with the power of Dagger)
- Navigation Graphs (for type-safe navigation between UI parts)
- Kotlin Coroutine Flows (for consuming results from worker threads and for keeping the View up to date with the ViewModel)
- Room (for accessing SQLite databases in a type-safe manner)
- Repository Pattern (to isolate I/O logic from ViewModel logic and for improved testability)
- ConstraintLayout (for designing UIs optimized for a low hierarchy depth)
- Retrofit (for consuming REST APIs)

# Features

## Overview
![Overview](docimgs/overview.png | height=400)
This screen provides an overview of hot posts of the Android subreddit. The _swap_ button in the top right corner allows switching to a Jetpack Compose-based implementation. It should look virtually identical. Clicking the button again will revert to the traditional XML-based implementation.

## Post
![Post](docimgs/post.png "Post" | height=400)
This screen shows the content of a post. If it is a self-post, its text is displayed. If it is a link-post, a clickable link card is displayed. Below the post itself are the comments of the post. As a limitation of the tokenless Reddit API, this only displays the top level comments.

# Architecture
The app follows the MVVM architecture. UI content logic is moved out of Fragments as much as possible into ViewModels. This has several advantages:
- ViewModels contain automatic lifecycle management.
- ViewModels can be unit-tested more easily.
- View implementations can be swapped out. This has been done for the overview screen that has been implemented both using XML and Jetpack Compose. The ViewModel is the same.

Important objects such as the database, the REST API adapter (implemented through Retrofit) and the repository are all injected into classes that need them using Dagger Hilt. 

Navigation between fragments has been implemented using the navigation library and the type-safe argument Gradle plugin.

Kotlin Coroutine Flows are used for exposing live data from the database, passing through the repository and the ViewModel to the View.

The database access layer has been implemented using Room, providing type-safe database access and live data.

This repository follows the school of thought of only commenting code that cannot be easily understood without an explanation. As comments get out of sync with the actual implementation very quickly, high-level documentation is considered more beneficial.
While there are projects in which low-level comments are still beneficial, I believe this architecture demo project is not one of them.