plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false  // Add KSP
    id("com.google.dagger.hilt.android") version "2.48" apply false  // Updated version
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22" apply false  // Updated version
    id("com.google.gms.google-services") version "4.4.2" apply false
}