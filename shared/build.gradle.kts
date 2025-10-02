import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.regex.Pattern

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

project.extra.set("buildkonfig.flavor", currentBuildVariant())

fun Project.getAndroidBuildVariantOrNull(): String? {
    val variants = setOf("dev", "prod", "staging")
    val taskRequestsStr = gradle.startParameter.taskRequests.toString()
    val pattern: Pattern = if (taskRequestsStr.contains("assemble")) {
        Pattern.compile("assemble(\\w+)(Release|Debug)")
    } else {
        Pattern.compile("bundle(\\w+)(Release|Debug)")
    }

    val matcher = pattern.matcher(taskRequestsStr)
    val variant = if (matcher.find()) matcher.group(1).lowercase() else null
    return if (variant in variants) {
        variant
    } else {
        null
    }
}

private fun Project.currentBuildVariant(): String {
    val variants = setOf("dev", "prod", "staging")

//    val iosEnv = System.getenv()["VARIANT"] // This is not working
//        .toString()
//        .takeIf { it in variants }


    val iosEnvMap = hashMapOf<String, String>(
        "Development Debug" to "dev",
        "Development Release" to "dev",
        "Staging Debug" to "staging",
        "Staging Release" to "staging",
        "Production Debug" to "prod",
        "Production Release" to "prod"
    )
    val iosEnv = iosEnvMap[System.getenv()["CONFIGURATION"]] ?: "dev"

    return getAndroidBuildVariantOrNull() // Android
        ?: iosEnv // iOS
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
