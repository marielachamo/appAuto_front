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
        kotlinCompilerExtensionVersion = "1.5.3"
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
    // Compose BOM (coordina versiones automáticamente)
    implementation(platform("androidx.compose:compose-bom:2024.09.01"))

    // Componentes principales de Compose
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    //  Navegación entre pantallas (NavController)
    implementation("androidx.navigation:navigation-compose:2.8.0")

    //  Material 3 (solo UNA versión)

    implementation("androidx.compose.material3:material3:1.3.1")


    //  Íconos extendidos (flechas, menú, etc.)
    implementation("androidx.compose.material:material-icons-extended")

    // Mapas de Google y MapCompose (por si usas ambos)
    implementation("com.google.maps.android:maps-compose:4.3.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("ovh.plrapps:mapcompose:2.9.0")

    //  OpenStreetMap (usa solo una versión, no dupliques)
    implementation("org.osmdroid:osmdroid-android:6.1.18")
    implementation("org.osmdroid:osmdroid-wms:6.1.16")
}
