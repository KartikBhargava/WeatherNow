import org.gradle.api.JavaVersion.VERSION_11

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
}

android {
    namespace = "bhargava.kartik.weathernow"
    compileSdk = 34

    defaultConfig {
        applicationId = "bhargava.kartik.weathernow"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // SECURITY: Move API key to local.properties for production
        buildConfigField("String", "WEATHER_API_KEY", "\"${project.findProperty("WEATHER_API_KEY") ?: ""}\"")

        // Optimize for Play Store
        vectorDrawables {
            useSupportLibrary = true
        }

        // Supported languages (reduce APK size)
        resourceConfigurations += listOf("en", "es", "fr", "de", "it", "pt", "ru", "ja", "ko", "zh")
    }

    signingConfigs {
        create("release") {
            // TODO: Add these when ready for Play Store upload
            // You'll need to create a keystore file
            // keyAlias = "your-key-alias"
            // keyPassword = "your-key-password"
            // storeFile = file("path/to/your/keystore.jks")
            // storePassword = "your-store-password"
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isMinifyEnabled = false
        }

        release {
            // Optimization for Play Store
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // TODO: Enable when keystore is ready
            // signingConfig = signingConfigs.getByName("release")
        }
    }

    // Android App Bundle optimization
    bundle {
        language {
            // Split by language to reduce download size
            enableSplit = true
        }
        density {
            // Split by screen density
            enableSplit = true
        }
        abi {
            // Split by architecture
            enableSplit = true
        }
    }

    compileOptions {
        sourceCompatibility = VERSION_11
        targetCompatibility = VERSION_11

        // Enable desugaring for better compatibility
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "11"

        // Kotlin compiler optimizations
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi"
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true

        // Disable unused features to reduce build time
        aidl = false
        renderScript = false
        resValues = false
        shaders = false
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    packaging {
        resources {
            excludes += setOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "/META-INF/DEPENDENCIES",
                "/META-INF/LICENSE",
                "/META-INF/LICENSE.txt",
                "/META-INF/license.txt",
                "/META-INF/NOTICE",
                "/META-INF/NOTICE.txt",
                "/META-INF/notice.txt",
                "/META-INF/ASL2.0",
                "/META-INF/*.kotlin_module"
            )
        }
    }

    // Lint configuration for Play Store compliance
    lint {
        checkReleaseBuilds = true
        abortOnError = false
        warningsAsErrors = false

        // Important for Play Store
        checkAllWarnings = true
        explainIssues = true

        // Disable specific checks if needed
        disable += setOf(
            "MissingTranslation",
            "UnusedResources"
        )
    }
}

dependencies {
    // Desugaring support for older Android versions
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Location Services
    implementation(libs.play.services.location)

    // Image Loading
    implementation(libs.coil.compose)

    // Permissions
    implementation(libs.accompanist.permissions)

    // Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Play Store optimization
    implementation("androidx.profileinstaller:profileinstaller:1.3.1")

    //location
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debug tools (only in debug builds)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // Remove the hardcoded version - use BOM version instead
}