plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "io.github.libxposed.api"
    compileSdk = 36

    sourceSets {
        val main by getting
        main.apply {
            manifest.srcFile("api/api/src/main/AndroidManifest.xml")
            java.setSrcDirs(listOf("api/api/src/main/java"))
        }
    }

    defaultConfig {
        consumerProguardFiles("api/api/proguard-rules.pro")
    }

    buildFeatures {
        buildConfig = false
    }

    androidResources {
        enable = false
    }
}

dependencies {
    compileOnly(libs.androidx.annotation)
}
