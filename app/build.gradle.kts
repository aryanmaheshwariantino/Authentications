plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id ("kotlin-kapt")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.aryan.authentications"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.aryan.authentications"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
        dataBinding = true
    }


}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Navigation
    implementation (libs.androidx.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)

    //CountryCode picker
    implementation (libs.ccp)

    //Facebook Login
    implementation (libs.facebook.login)

    // retrofit and converter
    implementation (libs.retrofit)
    implementation (libs.retrofit2.converter.gson)

    // viewmodel and lifecycle
    implementation (libs.androidx.lifecycle.viewmodel.ktx)
    implementation (libs.androidx.lifecycle.livedata.ktx)

    // Coroutines
    implementation (libs.kotlinx.coroutines.android)

    // Hilt
    implementation (libs.hilt.android)

    //Glide
    implementation (libs.glide)

    // logging interceptor
    implementation (libs.logging.interceptor)

    // google maps
    implementation (libs.play.services.maps)


}