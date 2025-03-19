plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.cbruegg.redtoy"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.cbruegg.redtoy"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
}

dependencies {
    val roomVersion = "2.4.2"
    val navVersion = "2.5.0"
    val moshiVersion = "1.13.0"
    val lifecycleVersion = "2.6.0-alpha01"
    val activityVersion = "1.6.0-alpha05"
    val appCompatVersion = "1.4.2"
    val materialVersion = "1.6.1"
    val coilVersion = "2.1.0"
    val composeVersion = "1.1.1"
    val composeThemeAdapterVersion = "1.1.14"
    val swipeRefreshVersion = "0.24.13-rc"
    val coreKtxVersion = "1.8.0"
    val constraintLayoutVersion = "2.1.4"
    val retrofitVersion = "2.9.0"
    val daggerHiltVersion = "2.55"
    val junitVersion = "4.13.2"
    val coroutinesTestVersion = "1.6.4"
    val extJunitVersion = "1.1.3"
    val espressoCoreVersion = "3.4.0"
    val archCoreTestingVersion = "2.1.0"

    implementation("io.noties.markwon:core:4.6.2")

    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    implementation("com.google.accompanist:accompanist-swiperefresh:$swipeRefreshVersion")

    // Integration with activities
    implementation("androidx.activity:activity-compose:1.5.0")
    // Compose Material Design
    implementation("androidx.compose.material:material:$composeVersion")
    // Animations
    implementation("androidx.compose.animation:animation:$composeVersion")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    // Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    implementation("com.google.android.material:compose-theme-adapter:$composeThemeAdapterVersion")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation("io.coil-kt:coil:$coilVersion")
    implementation("io.coil-kt:coil-compose:$coilVersion")

    implementation("androidx.constraintlayout:constraintlayout:$constraintLayoutVersion")

    implementation("androidx.navigation:navigation-fragment:$navVersion")
    implementation("androidx.navigation:navigation-ui:$navVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    androidTestImplementation("androidx.navigation:navigation-testing:$navVersion")
    implementation("androidx.navigation:navigation-compose:$navVersion")

    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")
    implementation("com.squareup.moshi:moshi-kotlin:$moshiVersion")
    implementation("com.squareup.moshi:moshi-adapters:$moshiVersion")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

    implementation("com.google.dagger:hilt-android:$daggerHiltVersion")
    kapt("com.google.dagger:hilt-compiler:$daggerHiltVersion")

    androidTestImplementation("com.google.dagger:hilt-android-testing:$daggerHiltVersion")
    kaptAndroidTest("com.google.dagger:hilt-compiler:$daggerHiltVersion")

    testImplementation("com.google.dagger:hilt-android-testing:$daggerHiltVersion")
    kaptTest("com.google.dagger:hilt-compiler:$daggerHiltVersion")

    implementation("androidx.core:core-ktx:$coreKtxVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.navigation:navigation-runtime-ktx:$navVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    // Use alpha version to support enableOnBackInvokedCallback (https://android-developers.googleblog.com/2022/07/prepare-your-app-to-support-predictive-back-gestures.html)
    implementation("androidx.activity:activity:$activityVersion")

    implementation("androidx.appcompat:appcompat:$appCompatVersion")
    implementation("com.google.android.material:material:$materialVersion")
    testImplementation("junit:junit:$junitVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesTestVersion")
    androidTestImplementation("androidx.test.ext:junit:$extJunitVersion")
    androidTestImplementation("androidx.test.espresso:espresso-core:$espressoCoreVersion")

    testImplementation("androidx.arch.core:core-testing:$archCoreTestingVersion")
}

kapt {
    correctErrorTypes = true
}
