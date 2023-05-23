@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

import com.thoughtworks.ark.buildlogic.androidApplication
import com.thoughtworks.ark.buildlogic.enableCompose

plugins {
    alias(libs.plugins.application)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.detekt)
//    alias(libs.plugins.router)
}

apply(from = "../config/jacoco/modules.kts")

androidApplication {
    namespace = "com.thoughtworks.smartassistantapp"

    defaultConfig {
        applicationId = "com.thoughtworks.smartassistantapp"
        versionCode = 1
        versionName = "1.0.0"
        minSdk = 22

        ndk {
            abiFilters.add("armeabi")
            abiFilters.add("armeabi-v7a")
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    enableCompose()

    packagingOptions {
        doNotStrip("*/*/libvad.dnn.so")
        doNotStrip("*/*/libbd_easr_s1_merge_normal_20151216.dat.so")
    }
}

dependencies {
    
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.android)
    implementation(libs.bundles.compose)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

//    implementation(libs.router)
//    kapt(libs.router.compiler)

    implementation(libs.bundles.coil)

    implementation(project(":assistant"))

    testImplementation(libs.junit4)

    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.truth)

    detektPlugins(libs.detekt.formatting)
}