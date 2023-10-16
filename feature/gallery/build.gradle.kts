plugins {
    alias(androidx.plugins.library)
    alias(kotlinx.plugins.android)
}

android {
    namespace = "pl.intexsoft.photoapp.feature.gallery"
    compileSdk = 34

    defaultConfig {
        minSdk = 30

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:model"))
    implementation(project(":core:navigation"))
    implementation(project(":core:ui"))

    // Navigation
    implementation(libs.bundles.voyager)

    // Images
    implementation(libs.bundles.coil)

    // DI
    implementation(libs.bundles.koin)

    // Coroutines
    implementation(kotlinx.bundles.coroutines)

    // AndroidX libraries
    implementation(androidx.core.ktx)
    implementation(androidx.appcompat)
}