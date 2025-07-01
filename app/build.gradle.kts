plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.hello_world"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.hello_world"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    // 加上这段配置自动使用 JDK 11 或 JDK 8 编译
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(11) // 或 8，推荐用 11
        }
    }
}

dependencies {
    implementation ("com.squareup.retrofit2:retrofit:2.9.0") // Retrofit 核心库
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // 如果需要使用 Gson 作为 JSON 转换器
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.compose.theme.adapter)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}