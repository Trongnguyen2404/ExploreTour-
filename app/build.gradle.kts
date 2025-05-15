plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}
dependencies {
    implementation("androidx.navigation:navigation-compose:2.8.9")
    implementation ("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    implementation ("androidx.compose.ui:ui:1.7.0")
    implementation("androidx.compose.ui:ui:1.3.0-alpha01")
    implementation ("androidx.compose.material3:material3:1.3.0")

    implementation("androidx.compose.ui:ui-tooling-preview:1.3.0-alpha01")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation ("androidx.core:core-ktx:1.10.1")

    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.9.0")
    implementation("io.coil-kt:coil-compose:2.2.2")

    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.X.X") // Trùng lặp

    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    implementation ("androidx.media3:media3-common:<latest-version>")
    implementation("androidx.media3:media3-common:1.3.1")

    implementation ("androidx.compose.material3:material3:1.2.0") // Hoặc phiên bản mới nhất
    implementation ("androidx.compose.material:material-icons-extended:1.6.0") // Hoặc phiên bản mới nhất
// Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

// Coil ( cái mới nhất)
    implementation("io.coil-kt:coil-compose:2.5.0")


    implementation ("androidx.compose.ui:ui:1.6.8")


    // JSON Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    implementation("androidx.compose.material3:material3:1.2.0")

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

// Retrofit for networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Hoặc phiên bản mới nhất
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Hoặc phiên bản mới nhất

    // Gson for JSON parsing
    implementation("com.google.code.gson:gson:2.10.1") // Hoặc phiên bản mới nhất

    // OkHttp Logging Interceptor (optional, but helpful for debugging)
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0") // Hoặc phiên bản mới nhất

    // Kotlin Coroutines Core (thường đã có sẵn trong dự án Android mới)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3") // Hoặc phiên bản mới nhất
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3") // Hoặc phiên bản mới nhất

    // Lifecycle ViewModel & LiveData KTX (useful if using ViewModel, good practice)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2") // Hoặc phiên bản mới nhất
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2") // Hoặc phiên bản mới nhất
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2") // Để dùng ViewModel trong Compose

    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation ("com.jakewharton.threetenabp:threetenabp:1.4.4")



}
android {
    namespace = "com.example.vivu_application"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.vivu_application"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.core.splashscreen)
}