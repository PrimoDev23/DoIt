@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.compose.report)
}

android {
    namespace = "com.example.doit.domain"
    compileSdk = 34

    defaultConfig {
        minSdk = 29

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    implementation(project(":common"))
    testImplementation(project(":testing"))

    implementation(libs.core.ktx)
    implementation(libs.coroutines)
    testImplementation(libs.junit)

    implementation(platform(libs.compose.bom))
    implementation(libs.ui.graphics)
    implementation(libs.compose.runtime)

    implementation(libs.work)

    implementation(libs.koin)
    implementation(libs.koin.workmanager)

    implementation(libs.kotlinx.collections.immutable)
}