import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_3

val kotestVersion = "6.1.11"
val mockkVersion = "1.14.9"

plugins {
  kotlin("jvm") version "2.3.20"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(kotlin("stdlib"))

  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
  testImplementation("io.mockk:mockk:$mockkVersion")
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    jvmTarget.set(JVM_25)
    languageVersion.set(KOTLIN_2_3)
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}
