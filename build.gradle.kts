// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        val navVersion = "2.5.0"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
    }
}

plugins {
    id("com.android.application") version "8.9.0" apply false
    id("com.android.library") version "8.9.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    id("com.google.dagger.hilt.android") version "2.55" apply false
    id("com.google.devtools.ksp") version "1.9.24-1.0.20" apply false
}
