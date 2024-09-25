plugins {
    id("java")
    kotlin("jvm")
}

//group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    flatDir {
        dirs("sim/src/libraries/")
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.esri.geometry:esri-geometry-api:2.0.0")
    implementation("math.geom2d:javaGeom:0.11.1")
    implementation("com.google.code.gson:gson:2.8.2")
    implementation("org.jgrapht:jgrapht-core:1.1.0")
    implementation(files("src/libraries/geom/lib/gpcj-2.2.0.jar"))
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    testImplementation("junit:junit:4.11")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
sourceSets {
    main {
        java.srcDir("sim/src/main/java/")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
