plugins {
    id("java-library")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

javafx {
    version = "22"
    modules("javafx.fxml", "javafx.controls", "javafx.web")
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://maven.gravitlauncher.com")
    }
}

dependencies {
    implementation("com.gravitlauncher.launcher:launcher-runtime:5.7.0-SNAPSHOT")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}