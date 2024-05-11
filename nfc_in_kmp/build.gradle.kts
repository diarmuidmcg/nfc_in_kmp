import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("module.publication")
}

// if you're using XCode 15 or after, you will need to set
// export MODERN_XCODE_LINKER=true
// in your CLI before running ./gradlew clean && ./gradlew assemblexcframework
val isModernXcodeLinker = System.getenv("MODERN_XCODE_LINKER")?.toBoolean() ?: false

kotlin {
    targetHierarchy.default()
    androidTarget {
        publishLibraryVariants("release")
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    val xcf = XCFramework()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            binaryOption("bundleId", "nfc_in_kmp")
            baseName = "nfc_in_kmp"
            if (isModernXcodeLinker) linkerOpts += "-ld64"
            xcf.add(this)
        }
    }
//
//    iosX64()
//    iosArm64()
//    iosSimulatorArm64()
    task("testClasses")

    sourceSets {
        val commonMain by getting {
            dependencies {
                //put your multiplatform dependencies here
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = "org.diarmuiddevs.nfc_in_kmp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
