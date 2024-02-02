plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "ge.gogichaishvili.lotto"
    compileSdk = 34

    defaultConfig {
        applicationId = "ge.gogichaishvili.lotto"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Recyclerview
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    // Fragment
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    runtimeOnly("androidx.lifecycle:lifecycle-extensions:2.2.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.14.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")

    // Circular Imageview
    implementation ("com.mikhaellopez:circularimageview:4.3.1")

    // EncryptedSharedPreferences
    implementation ("androidx.security:security-crypto:1.0.0")

    //Koin
    // Koin core features
    implementation ("io.insert-koin:koin-core:3.1.2")
    // Koin test features
    testImplementation ("io.insert-koin:koin-test:3.1.2")
    // Koin for JUnit 4
    testImplementation ("io.insert-koin:koin-test-junit4:3.1.2")
    // Koin for JUnit 5
    testImplementation ("io.insert-koin:koin-test-junit5:3.1.2")
    // Koin main features for Android (Scope,ViewModel ...)
    implementation ("io.insert-koin:koin-android:3.1.2")
    // Koin Java Compatibility
    implementation ("io.insert-koin:koin-android-compat:3.1.2")
    // Koin for Jetpack WorkManager
    implementation ("io.insert-koin:koin-androidx-workmanager:3.1.2")
    // Koin for Jetpack Compose
    implementation ("io.insert-koin:koin-androidx-compose:3.1.2")
    // Koin for Ktor
    implementation ("io.insert-koin:koin-ktor:3.1.2")
    // SLF4J Logger
    implementation ("io.insert-koin:koin-logger-slf4j:3.1.2")

    // Okhttp
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")

    // Retrofit
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")

    //user permission
    implementation ("com.karumi:dexter:6.2.3")

    //Splashscreen
    implementation("androidx.core:core-splashscreen:1.1.0-alpha02")

}