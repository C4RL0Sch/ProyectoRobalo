plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.crashlytics)
}

android {
    namespace = "tmz.jcmh.proyecto_robalo"
    compileSdk = 35

    defaultConfig {
        applicationId = "tmz.jcmh.proyecto_robalo"
        minSdk = 28
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.drawerlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //FireBase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    //apache POI
    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")

    // Para soporte de coroutines
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    //Para cargar imagenes asincronamente
    implementation ("com.squareup.picasso:picasso:2.8")

    //PARA MENEJO DE FRAGMENTOS
    implementation("androidx.fragment:fragment:1.8.5")
}