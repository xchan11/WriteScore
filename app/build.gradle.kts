plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.writescore"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.writescore"
        minSdk = 28
        targetSdk = 33
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

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}


dependencies {
    // Compose 依赖
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("io.coil-kt:coil-compose:1.3.0")
    implementation("androidx.compose.material:material-icons-extended:1.0.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.0.0")

    // PhotoView 依赖
    implementation("com.github.chrisbanes:PhotoView:2.3.0")

    // 第三方权限框架
    implementation("com.guolindev.permissionx:permissionx:1.7.1")

    // 地区选择器
    implementation("liji.library.dev:citypickerview:5.2.2")

    // 圆形图片依赖
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // 下拉刷新
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // 轮播图
    implementation("com.youth.banner:banner:2.1.0")

    // Flexbox 布局
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    // 添加 JAR 文件依赖
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // AppCompat
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // 测试依赖
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")

    // Android-PickerView
    implementation("com.contrarywind:Android-PickerView:4.1.9")

    // 时间选择器
    implementation("com.github.loperSeven:DateTimePicker:0.6.3")

    // GPUImage
    implementation("jp.co.cyberagent.android.gpuimage:gpuimage-library:1.3.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // PictureSelector
    implementation("io.github.lucksiege:pictureselector:v3.11.2")
    implementation("io.github.lucksiege:pictureselector:kotlin-v1.0.0-beta")

    // BaseRecyclerViewAdapterHelper
    implementation("com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.4")

    // OkHttpUtils
    implementation("com.zhy:okhttputils:2.6.2")

    // OkHttp Logging Interceptor
    implementation("com.squareup.okhttp3:logging-interceptor:3.4.1")

    // Retrofit2
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.5.0")

    // UETool
    debugImplementation("com.github.eleme.UETool:uetool:1.3.4")
    debugImplementation("com.github.eleme.UETool:uetool-base:1.3.4")

    // Bugly
    implementation("com.tencent.bugly:crashreport:4.1.9.3")

    // 七牛云
    implementation("com.qiniu:qiniu-android-sdk:8.7.+") {
        exclude(mapOf("group" to "com.squareup.okhttp3", "module" to "okhttp"))
    }

    // ZoomLayout
    implementation("com.otaliastudios:zoomlayout:1.9.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0-alpha01")
    implementation("com.google.android.material:material:1.4.0")

    // Retrofit2
    api("com.squareup.retrofit2:retrofit:2.9.0")
    api("com.squareup.okhttp3:okhttp:4.11.0")
    api("com.google.code.gson:gson:2.10.1")
    api("com.squareup.retrofit2:converter-gson:2.9.0")

    // Lifecycle
    api("androidx.lifecycle:lifecycle-extensions:2.2.0")
    api("androidx.viewpager:viewpager:1.0.0")

    // Protocol Buffers
    implementation("com.google.protobuf:protobuf-java:3.19.2")

    // CameraX
    implementation("androidx.camera:camera-core:1.3.0")
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-video:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")
    implementation("androidx.camera:camera-extensions:1.3.0")

    // ML Kit
    implementation("com.google.mlkit:barcode-scanning:17.1.0")

    // ImmersionBar
    implementation("com.geyifeng.immersionbar:immersionbar:3.2.2")

    // QRGen
    implementation("com.github.kenglxn.QRGen:android:3.0.1")

    // ApShare
    implementation(files("src/main/resources/libs/libapshare.jar"))

    // 项目依赖
//    implementation(project(":annotation"))
//    kapt(project(":compiler"))
}