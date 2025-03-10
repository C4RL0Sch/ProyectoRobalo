plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.devtools.ksp") version("1.9.0-1.0.13")
}

android {
    namespace = "tmz.jcmh.proyecto_robalo"
    compileSdk = 34

    defaultConfig {
        applicationId = "tmz.jcmh.proyecto_robalo"
        minSdk = 28
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
    kotlinOptions {
        jvmTarget = "1.8"
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //apache POI
    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")

    //ROOM
    implementation ("androidx.room:room-runtime:2.5.0")
    ksp ("androidx.room:room-compiler:2.5.0")
    implementation ("androidx.room:room-ktx:2.5.0")

    // Para soporte de coroutines
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    //TODO reporte
    //Para cargar imagenes asincronamente
    implementation ("com.squareup.picasso:picasso:2.8")

    //PARA MENEJO DE FRAGMENTOS
    implementation("androidx.fragment:fragment:1.8.5")
}