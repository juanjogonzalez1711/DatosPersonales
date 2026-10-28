// Configuración de plugins aplicados al módulo
plugins {
    // Aplica el plugin oficial de Android para crear la app
    alias(libs.plugins.android.application)
}

// Bloque principal de configuración de Android
android {
    // Define el paquete base para generar la clase de recursos R.java
    namespace = "com.example.datospersonales"

    // Versión del SDK de Android usada para compilar el código
    compileSdk = 34

    // Parámetros por defecto de la aplicación
    defaultConfig {
        // ID único del paquete de la app en el dispositivo y Google Play
        applicationId = "com.example.datospersonales"

        // Versión mínima de Android soportada (API 24 = Android 7.0)
        minSdk = 24

        // Versión de API para la que se ha optimizado el comportamiento
        targetSdk = 34

        // Versión interna de compilación y versión pública para usuarios
        versionCode = 1
        versionName = "1.0"

        // Runner utilizado para ejecutar las pruebas unitarias de UI
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Configuración de los tipos de compilación (release y debug)
    buildTypes {
        release {
            // Desactiva la ofuscación y reducción de código con ProGuard
            isMinifyEnabled = false

            // Archivos de reglas de optimización de código
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Opciones del compilador de Java
    compileOptions {
        // Establece la compatibilidad del código fuente con Java 11
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// Declaración de librerías y dependencias externas del proyecto
dependencies {
    // Librería KTX para extensiones de Kotlin en actividades
    implementation(libs.activity.ktx)

    // Soporte para componentes de la interfaz de AndroidX
    implementation(libs.appcompat)

    // Layout para crear interfaces complejas mediante restricciones
    implementation(libs.constraintlayout)

    // Componentes de diseño visual de Google Material Design
    implementation(libs.material)

    // Dependencias de testing unitario y de UI
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}