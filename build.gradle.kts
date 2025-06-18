// 🔧 This is the top-level build.gradle.kts
plugins {
    alias(libs.plugins.android.application) apply false
}

buildscript {
    dependencies {
        // ✅ Google Services plugin for Firebase
        classpath("com.google.gms:google-services:4.4.1")
    }
}
