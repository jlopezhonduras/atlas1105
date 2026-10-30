# Atlas de Banderas 1105 · Android

Aplicación Android nativa, creada con Kotlin y Jetpack Compose.

## Configuración solicitada

- `compileSdk = 36`
- `targetSdk = 36`
- `minSdk = 24` (Android 7.0)
- Java 17

## Funcionalidad del primer borrador

- Catálogo en español de 195 países (193 miembros de la ONU, Palestina y Ciudad del Vaticano).
- Búsqueda sin distinción de mayúsculas ni acentos.
- Banderas cargadas de forma segura desde FlagCDN.
- Icono de aplicación original: una brújula sobre un globo, en azul, coral y dorado.
- Botón con icono de globo para abrir la geometría local del país y consultar continente, subregión, capital, extensión y coordenadas. Los datos y los polígonos GeoJSON se incluyen dentro de la app, por lo que el visor funciona sin conexión.
- Al tocar una bandera se expanden botones para buscar el país en Google, TikTok y YouTube.
- Pantallas informativas de “Acerca de” y “Términos y condiciones”.

## Abrir y compilar

1. Abre esta carpeta con Android Studio Meerkat (2024.3.1 Patch 1) o una versión posterior.
2. Instala Android SDK Platform 36 y configura un JDK 17.
3. Permite a Gradle sincronizar las dependencias y ejecuta la configuración `app` en un emulador o dispositivo con Android 7.0 o superior.

El proyecto usa Android Gradle Plugin 8.11.1, que es compatible con API 36, y Gradle 8.13. El entorno de creación no tenía Java, Gradle ni SDK Android, por lo que el APK no se generó en esta máquina.
