import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("org.springframework.boot") version "2.6.6"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("com.github.node-gradle.node") version "3.0.1"

    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.21"

}

group = "pt.isel.ps"
version = "0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    maven {
        url = uri("https://jcenter.bintray.com/")
    }
}

dependencies {
    implementation("org.unbroken-dome.siren:siren-core:0.2.0")
    implementation("com.sun.mail:javax.mail:1.6.2")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb:2.7.0")
    implementation("com.sendgrid:sendgrid-java:4.0.1")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("org.springframework.boot:spring-boot-starter-websocket:2.6.7")
    implementation("org.springframework.boot:spring-boot-starter-data-rest:2.6.7")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")



    //testImplementation("org.springframework.boot:spring-boot-starter-test")

}
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

node {
    version.set("16.13.1")
    npmInstallCommand.set("install")
    distBaseUrl.set("https://nodejs.org/dist")
    download.set(true)
    workDir.set(file("${project.projectDir}/src/main/webapp/.cache/nodejs"))
    npmWorkDir.set(file("${project.projectDir}/src/main/webapp/.cache/npm"))
    nodeProjectDir.set(file("${project.projectDir}/src/main/webapp"))
}

tasks.npmInstall

val buildTaskUsingNpm = tasks.register<NpmTask>("buildNpm") {
    dependsOn(tasks.npmInstall)
    npmCommand.set(listOf("run", "build"))
    //args.set(listOf("--", "--out-dir", "${buildDir}/npm-output"))
    inputs.dir("src/main/webapp/src")
    outputs.dir("src/main/webapp/npm-output")
}


tasks.compileKotlin {
    dependsOn(buildTaskUsingNpm)
    dependsOn("copyWebApp")
}

tasks.register<Copy>("copyWebApp") {
    //dependsOn(tasks.compileKotlin)
    from("${project.projectDir}/src/main/webapp/build")
    into("${buildDir}/resources/main/static/.")
}
