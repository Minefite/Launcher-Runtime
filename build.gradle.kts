plugins {
    id("java-library")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.jar {
    archiveFileName.set(project.name+".jar")
    manifest {
        attributes(
            "Module-Main-Class" to "pro.gravit.launcher.gui.JavaRuntimeModule",
            "Module-Config-Class" to "pro.gravit.launcher.gui.core.config.GuiModuleConfig",
            "Module-Config-Name" to "JavaRuntime"
        )
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
    flatDir { dirs("libs") }
}

dependencies {
    implementation("com.gravitlauncher.launcher:launcher-runtime:5.7.6")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Source: https://mvnrepository.com/artifact/org.apache.httpcomponents.client5/httpclient5
    //implementation("org.apache.httpcomponents.client5:httpclient5:5.6")

    // https://mvnrepository.com/artifact/commons-io/commons-io
    implementation("commons-io:commons-io:2.21.+")

    // https://github.com/ZeyDie/Simple-GSON
    implementation("com.zeydie:SGson:2.10.x-1.2")

    // https://mvnrepository.com/artifact/org.jetbrains/annotations
    implementation("org.jetbrains:annotations:26.0.+")

    // https://mvnrepository.com/artifact/org.projectlombok/lombok
    compileOnly("org.projectlombok:lombok:1.18.+")
    annotationProcessor("org.projectlombok:lombok:1.18.+")
}

tasks.test {
    useJUnitPlatform()
}