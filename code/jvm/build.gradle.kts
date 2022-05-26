import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.6.6"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.10"
	kotlin("plugin.spring") version "1.6.10"
	kotlin("plugin.serialization") version "1.6.21"
}

group = "pt.isel.ps"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
	maven {
		url = uri("https://jcenter.bintray.com/")
	}
}

dependencies {
	//implementation("org.springframework.security:spring-security-config")
	//implementation("org.springframework.security:spring-security-web")
	implementation("org.unbroken-dome.siren:siren-core:0.2.0")
	implementation("com.sun.mail:javax.mail:1.6.2")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.data:spring-data-elasticsearch:4.3.3") //Bonsai Max Elastic version 7.10.2 -> Spting plugin elastic plugin 7.9.3
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("javax.validation:validation-api:2.0.1.Final")
	implementation("org.springframework.boot:spring-boot-starter-websocket:2.6.7")
	implementation("org.springframework.boot:spring-boot-starter-data-rest:2.6.7")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

	testImplementation("org.springframework.boot:spring-boot-starter-test")

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
