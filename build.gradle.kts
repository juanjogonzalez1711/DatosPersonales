// Archivo de construcción de nivel superior donde se definen opciones comunes para todos los submódulos
plugins {
    // Declara el plugin de Android Application mediante su alias sin aplicarlo en la raíz (apply false)
    alias(libs.plugins.android.application) apply false
}