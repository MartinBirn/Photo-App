plugins {
    alias(androidx.plugins.application)
    alias(kotlinx.plugins.android)
}

android {
    namespace = "pl.intexsoft.photoapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "pl.intexsoft.photoapp"
        minSdk = 30
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
    implementation(project(":core:navigation"))
    implementation(project(":core:ui"))

    implementation(project(":feature:camera"))
    implementation(project(":feature:gallery"))

    // AndroidX libraries
    implementation(androidx.appcompat)
    implementation(androidx.core.ktx)
    implementation(androidx.splashscreen)

    // Navigation
    implementation(libs.bundles.voyager)

    // DI
    implementation(libs.bundles.koin)
}