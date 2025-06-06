plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.martintrujillo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.martintrujillo"
        minSdk = 23
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

// Librería para Retrofit 2
    implementation("com.squareup.retrofit2:retrofit:2.11.0")

// Librería GSON para el tratamiento y conversión de datos JSON
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

// Librería OkHttp para simplificar la construcción de peticiones HTTP
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

// Librería para utilizar corrutinas en Kotlin (peticiones HTTP en segundo plano)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")


    //implementation ("com.mysql:mysql-connector-j:8.0.33") // Versión más reciente

    implementation("mysql:mysql-connector-java:5.1.49") // Versión más antigua

    implementation ("com.zaxxer:HikariCP:5.0.1") // Para el pool de conexiones
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    implementation ("com.squareup.picasso:picasso:2.8")

    implementation ("androidx.cardview:cardview:1.0.0")

    implementation ("androidx.constraintlayout:constraintlayout:2.1.1")
}