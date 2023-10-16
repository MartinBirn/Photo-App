plugins {
    alias(androidx.plugins.library)
    alias(kotlinx.plugins.android)
}

android {
    namespace = "pl.intexsoft.photoapp.core.ui"
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

    implementation(project(":core:model"))

    // Compose
    api(platform(compose.bom))
    api(compose.accompanist.permissions)
    api(compose.accompanist.systemuicontroller)
    api(compose.accompanist.themeadapter)
    api(compose.accompanist.webview)
    api(compose.accompanist.placeholder)
    api(compose.activity)
    api(compose.animation)
    api(compose.animation.graphics)
    api(compose.foundation)
    api(compose.material.icons)
    api(compose.material3.core)
    api(compose.ui.util)
    debugApi(compose.ui.tooling)
    api(compose.ui.tooling.preview)

    // AndroidX libraries
    implementation(androidx.core.ktx)
    implementation(androidx.core.ktx)
    implementation(androidx.appcompat)
    implementation(androidx.google.fonts)
}