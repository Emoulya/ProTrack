plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.protrack"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.protrack"
        minSdk = 26
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Additional dependencies for ProTrack
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.preference:preference:1.2.1")

    // Room Database
    implementation("androidx.room:room-runtime:2.7.2")
    annotationProcessor("androidx.room:room-compiler:2.7.2")

    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.9.1")
    implementation("androidx.lifecycle:lifecycle-livedata:2.9.1")

    // Work Manager for background tasks
    implementation("androidx.work:work-runtime:2.10.2")

    // Date and Time Picker
    implementation("com.wdullaer:materialdatetimepicker:4.2.3")

    // Chart library for progress tracking
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Gson for JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("androidx.core:core-splashscreen:1.1.0-rc01")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("androidx.fragment:fragment-ktx:1.8.8") // atau versi terbaru

}