plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.appautomovil"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.appautomovil"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        // 🔹 Versión compatible con Compose BOM 2024.09.01
        kotlinCompilerExtensionVersion = "1.6.10"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    // 🎨 Compose BOM (coordina versiones automáticamente)
    implementation(platform("androidx.compose:compose-bom:2024.09.01"))

    // 🧩 Componentes principales de Compose
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // 🧭 Navegación entre pantallas
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // 🪶 Material 3
    implementation("androidx.compose.material3:material3:1.3.1")

    // 🧱 Íconos extendidos
    implementation("androidx.compose.material:material-icons-extended")

    // 🌍 Google Maps (versiones estables y compatibles)
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.maps.android:maps-compose:2.11.4")

    // 🌐 Retrofit para peticiones HTTP
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // 🧠 ViewModel y LiveData
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
}

