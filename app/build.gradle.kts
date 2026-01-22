import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.mediqorog"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.mediqorog"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Load local.properties for API keys
    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(FileInputStream(localPropertiesFile))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            val apiKey = localProperties.getProperty("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", "")
            buildConfigField("String", "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", "\"$apiKey\"")
        }

        debug {
            val apiKey = localProperties.getProperty("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", "")
            buildConfigField("String", "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", "\"$apiKey\"")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.ui.graphics)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation("com.airbnb.android:lottie-compose:6.1.0")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("androidx.compose.material:material-icons-extended:1.6.0")
    implementation("com.cloudinary:cloudinary-android:2.1.0")
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
}