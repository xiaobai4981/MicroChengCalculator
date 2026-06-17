import com.android.build.api.variant.VariantOutputConfiguration
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
}


tasks.register<Copy>("renameReleaseApk") {
    dependsOn("assembleRelease")

    val versionName = android.defaultConfig.versionName ?: "1.0"

    from(layout.buildDirectory.dir("outputs/apk/release")) {
        include("*.apk")
        rename {
            "Vanilla_${versionName}.apk"
        }
    }

    into(layout.buildDirectory.dir("outputs/apk/renamed"))
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

android {
    namespace = "com.sosauce.vanilla"
    compileSdk = 37

    defaultConfig {

        applicationId = "com.chengjing.microcutecalculator"
        minSdk = 24
        targetSdk = 37
        versionCode = 3
        versionName = "1.0.0"
        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters += arrayOf("arm64-v8a", "armeabi-v7a")
        }

    }

    signingConfigs {
        val keystorePropertiesFile = rootProject.file("keystore.properties")
        val keystoreProperties = Properties()

        keystoreProperties.load(FileInputStream(keystorePropertiesFile))

        create("basic") {
            storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
            storePassword = keystoreProperties.getProperty("storePassword")
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("basic")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        aidl = false
        shaders = false
        buildConfig = false
        resValues = false
        viewBinding = false
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.datastore.preferences)
    //implementation(libs.keval)
    implementation(libs.androidx.room.ktx)
    implementation(libs.squircle.shape)
    ksp(libs.androidx.room.compiler)
}
