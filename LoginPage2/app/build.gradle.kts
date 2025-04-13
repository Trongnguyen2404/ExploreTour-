plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}
dependencies {
    implementation("androidx.navigation:navigation-compose:2.8.9")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
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
    implementation("androidx.compose.material:material-icons-extended:1.6.0") // Thêm icons
    implementation("androidx.navigation:navigation-compose:2.X.X") // Trùng lặp

    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    implementation ("androidx.media3:media3-common:<latest-version>")
    implementation("androidx.media3:media3-common:1.3.1")

    // Jetpack Compose Core

    implementation("androidx.compose.ui:ui:1.6.0")



// Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

// Coil ( cái mới nhất)
    implementation("io.coil-kt:coil-compose:2.5.0")

// DataStore


// JSON Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    implementation("androidx.compose.material3:material3:1.2.0")

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

}
android {
    namespace = "com.example.loginpage"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.loginpage"
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