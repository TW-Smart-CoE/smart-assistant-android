@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

import com.thoughtworks.ark.buildlogic.androidLibrary

plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.detekt)
    id("kotlin-parcelize")
}

apply(from = "../config/jacoco/modules.kts")

androidLibrary {
    namespace = "com.thoughtworks.assistant"

    defaultConfig {
        minSdk = 22

        ndk {
            abiFilters.add("armeabi")
            abiFilters.add("armeabi-v7a")
        }
    }

    sourceSets {
        named("main") {
            jniLibs.srcDirs("src/main/jniLibs/baidu")
        }
    }

    packagingOptions {
        // baidu libs
        jniLibs.keepDebugSymbols.add("*/*/libvad.dnn.so")
        jniLibs.keepDebugSymbols.add("*/*/libbd_easr_s1_merge_normal_20151216.dat.so")
    }
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.android)

    api(fileTree("libs") { include("**/*.jar") })

    // network
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // ali libs
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

    implementation("com.alibaba:fastjson:1.1.46.android")
    implementation("com.thoughtworks.smart-assistant:nuisdk:0.1.0")

    // picovoice
    implementation("ai.picovoice:porcupine-android:2.2.0")

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

apply(plugin = "maven-publish")

// 执行 ./gradlew publish 上传 smart-assistant 到 maven
configure<PublishingExtension> {
    publications {
        repositories {
            maven {
                url = uri("http://10.205.215.4:8081/repository/maven-releases/")
                isAllowInsecureProtocol = true
                credentials {
                    username = "admin"
                    password = "IoT1234"
                }
            }
        }

        create<MavenPublication>("assistant") {
            afterEvaluate {
                from(components.getByName("devRelease"))
                groupId = "com.thoughtworks.smart-assistant"
                version = "0.5.4"
            }
        }
    }

    // 上传ali tts aar 到maven，执行`./gradlew publishAlittsPublicationToMavenRepository`
    publications {
        create<MavenPublication>("alitts") {
            groupId = "com.thoughtworks.smart-assistant"
            version = "0.1.0"
            artifactId = "nuisdk"
            artifact(file("libs/ali/nuisdk-release.aar"))
        }
    }
}