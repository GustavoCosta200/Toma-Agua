
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Compose
    alias(libs.plugins.kotlin.compose)
    // Hilt
    alias(libs.plugins.hilt)
    // KSP
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.tomagua"
    compileSdk = 35   // <- sintaxe antiga, direto como propriedade

    defaultConfig {
        applicationId = "com.example.tomagua"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false   // <- sintaxe antiga (era "minifyEnabled" em Groovy)

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // ----------------------------------------
    // Jetpack Compose
    // ----------------------------------------

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    implementation(libs.androidx.material3)

    debugImplementation(libs.androidx.compose.ui.tooling)

    // ----------------------------------------
    // Lifecycle / ViewModel
    // ----------------------------------------

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // ----------------------------------------
    // Navigation Compose
    // ----------------------------------------

    implementation(libs.androidx.navigation.compose)

    // ----------------------------------------
    // Room
    // ----------------------------------------

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    ksp(libs.androidx.room.compiler)

    // ----------------------------------------
    // Hilt
    // ----------------------------------------

    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)

    ksp(libs.hilt.compiler)

    // ----------------------------------------
    // Kotlin Coroutines
    // ----------------------------------------

    implementation(libs.kotlinx.coroutines.android)

    // Desugaring
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}