plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.phellipesilva.coolposts"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.phellipesilva.coolposts"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "com.phellipesilva.coolposts.TestInstrumentationRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    //Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.20")

    //AppCompat
    implementation("androidx.appcompat:appcompat:1.6.1")

    //KTX
    implementation("androidx.core:core-ktx:1.12.0")

    //Layout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.11.0-beta01")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    //Architecture Components
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.room:room-runtime:2.6.0")
    implementation("androidx.room:room-rxjava2:2.6.0")
    ksp("androidx.room:room-compiler:2.6.0")

    //RX
    implementation("io.reactivex.rxjava2:rxandroid:2.1.0")
    implementation("io.reactivex.rxjava2:rxjava:2.2.9")
    implementation("io.reactivex.rxjava2:rxkotlin:2.3.0")
    implementation("com.jakewharton.rxbinding:rxbinding-kotlin:1.0.1")

    //Dagger
    ksp("com.google.dagger:dagger-compiler:2.48")
    implementation("com.google.dagger:dagger:2.48")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.7.1")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.5.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.7.1")
    implementation("com.squareup.okhttp3:okhttp:4.3.1")

    //Logging
    implementation("com.jakewharton.timber:timber:4.7.1")

    //Non-instrumented testing
    testImplementation("junit:junit:4.12")
    testImplementation("androidx.test:core:1.2.0")
    testImplementation("org.robolectric:robolectric:4.3.1")
    testImplementation("androidx.test.espresso:espresso-core:3.2.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.3.1")
    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("androidx.arch.core:core-testing:2.1.0")

    //Instrumentation testing
    androidTestImplementation("androidx.test:core:1.2.0")
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test:rules:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.2.0")
    androidTestImplementation("com.squareup.okhttp3:mockwebserver:4.3.1")
    androidTestImplementation("com.github.andrzejchm.RESTMock:android:0.4.1")
    androidTestImplementation("com.jakewharton.espresso:okhttp3-idling-resource:1.0.0")
    androidTestUtil("androidx.test:orchestrator:1.2.0")
    kaptAndroidTest("com.google.dagger:dagger-compiler:2.26")
}
