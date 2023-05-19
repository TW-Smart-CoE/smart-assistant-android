@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

import com.thoughtworks.ark.buildlogic.androidLibrary

plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.detekt)
}

apply(from = "../config/jacoco/modules.kts")

androidLibrary {
    namespace = "com.thoughtworks.assistant"
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.android)

    implementation("com.aliyun:aliyun-java-sdk-core:3.7.1")
    implementation("com.alibaba.nls:nls-sdk-common:2.1.6") {
        exclude(group = "com.alibaba", module = "fastjson")
        exclude(group = "io.netty", module = "netty-handler")
        exclude(group = "io.netty", module = "netty-codec")
        exclude(group = "io.netty", module = "netty-codec-http")
        exclude(group = "io.netty", module = "netty-buffer")
        exclude(group = "io.netty", module = "netty-transport")
        exclude(group = "io.netty", module = "netty-resolver")
    }
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("com.sun.xml.bind:jaxb-core:2.3.0")
    implementation("com.sun.xml.bind:jaxb-impl:2.3.0")
    implementation("xerces:xercesImpl:2.12.0")

    implementation(files("libs/fastjson-1.1.46.android.jar"))
    implementation(files("libs/nuisdk-release.aar"))

    testImplementation(libs.junit4)
    testImplementation(libs.androidx.junit.ktx)

    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.truth)

    detektPlugins(libs.detekt.formatting)
}