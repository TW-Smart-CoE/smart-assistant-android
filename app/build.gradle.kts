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

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["ALI_IVS_ACCESS_KEY"] = System.getenv("ALI_IVS_ACCESS_KEY") ?: ""
        manifestPlaceholders["ALI_IVS_ACCESS_KEY_SECRET"] = System.getenv("ALI_IVS_ACCESS_KEY_SECRET") ?: ""
        manifestPlaceholders["ALI_IVS_APP_KEY"] = System.getenv("ALI_IVS_APP_KEY") ?: ""
        manifestPlaceholders["BAIDU_IVS_APP_ID"] = System.getenv("BAIDU_IVS_APP_ID") ?: ""
        manifestPlaceholders["BAIDU_IVS_API_KEY"] = System.getenv("BAIDU_IVS_API_KEY") ?: ""
        manifestPlaceholders["BAIDU_IVS_SECRET_KEY"] = System.getenv("BAIDU_IVS_SECRET_KEY") ?: ""
        manifestPlaceholders["OPENAI_API_KEY"] = System.getenv("OPENAI_API_KEY") ?: ""
        manifestPlaceholders["PICOVOICE_ACCESS_KEY"] = System.getenv("PICOVOICE_ACCESS_KEY") ?: ""
        manifestPlaceholders["GOOGLE_CLOUD_API_KEY"] = System.getenv("GOOGLE_CLOUD_API_KEY") ?: ""
    }

    enableCompose()

    packagingOptions {
        pickFirst("META-INF/io.netty.versions.properties")
        pickFirst("META-INF/DEPENDENCIES")
        pickFirst("META-INF/INDEX.LIST")
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