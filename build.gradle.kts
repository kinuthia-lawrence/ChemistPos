// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    //dagger hilt - id("com.google.dagger.hilt.android") version "2.44" apply false
    alias(libs.plugins.google.dagger.hilt.android)  apply false
    alias(libs.plugins.compose.compiler) apply false

    // Add the dependency for the Google services Gradle plugin
    alias(libs.plugins.google.services) apply false
}