import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            // put your Multiplatform dependencies here
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "org.buildconfig.demo.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

buildkonfig {

    packageName = "org.buildconfig.demo"
    objectName = "BuildConfigKMPDemo"
    exposeObjectWithName = "BuildConfigKMPDemo"

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "variant", "dev")
        buildConfigField(FieldSpec.Type.STRING, "apiEndPoint", "https://dev.example.com")
    }

    defaultConfigs("dev") {
        buildConfigField(FieldSpec.Type.STRING, "variant", "dev")
        buildConfigField(FieldSpec.Type.STRING, "apiEndPoint", "https://dev.example.com")
    }

    defaultConfigs("staging") {
        buildConfigField(FieldSpec.Type.STRING, "variant", "staging")
        buildConfigField(FieldSpec.Type.STRING, "apiEndPoint", "https://staging.example.com")
    }

    defaultConfigs("prod") {
        buildConfigField(FieldSpec.Type.STRING, "variant", "prod")
        buildConfigField(FieldSpec.Type.STRING, "apiEndPoint", "https://example.com")
    }
}
