import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_3
import org.jooq.meta.jaxb.Logging
import org.jooq.meta.jaxb.Property

val archunitVersion = "1.4.1"
val jooqLiquibaseVersion = "3.19.32"
val kotestVersion = "6.1.11"
val liquibaseVersion = "4.33.0"
val mockkVersion = "1.14.9"

plugins {
  kotlin("jvm") version "2.3.20"
  kotlin("plugin.spring") version "2.3.20"
  id("org.springframework.boot") version "3.5.13"
  id("io.spring.dependency-management") version "1.1.7"
  id("org.jooq.jooq-codegen-gradle") version "3.19.32"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(kotlin("stdlib"))

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-jooq")
  implementation("org.jooq:jooq-kotlin")
  implementation("org.jooq:jooq-jackson-extensions:${dependencyManagement.importedProperties["jooq.version"] as String}")

  runtimeOnly("org.postgresql:postgresql")
  implementation("org.liquibase:liquibase-core:$liquibaseVersion")

  jooqCodegen("org.liquibase:liquibase-core")
  jooqCodegen("org.jooq:jooq-meta-extensions-liquibase:$jooqLiquibaseVersion")

  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude("org.junit.vintage", "junit-vintage-engine")
  }
  testImplementation("org.springframework.boot:spring-boot-testcontainers")
  testImplementation("org.testcontainers:postgresql")

  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
  testImplementation("io.mockk:mockk:$mockkVersion")
  testImplementation("com.tngtech.archunit:archunit:$archunitVersion")
  testImplementation("com.tngtech.archunit:archunit-junit5:$archunitVersion")
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

springBoot {
  mainClass.set("fr.sdecout.repository.AppKt")
}

jooq {
  version = dependencyManagement.importedProperties["jooq.version"] as String

  configuration {
    logging = Logging.WARN
    jdbc {
      driver = "org.postgresql.Driver"
      url = System.getProperty("jooq.codegen.jdbc.url")
      user = System.getProperty("jooq.codegen.jdbc.username")
      password = System.getProperty("jooq.codegen.jdbc.password")
    }
    generator {
      name = "org.jooq.codegen.KotlinGenerator"
      database {
        name = "org.jooq.meta.extensions.liquibase.LiquibaseDatabase"
        withProperties(
          Property().withKey("rootPath").withValue("$projectDir/src/main/resources"),
          Property().withKey("scripts").withValue("/db/changelog/db.changelog-master.yaml"),
          Property().withKey("includeLiquibaseTables").withValue("false")
        )
      }
      generate {
        isKotlinNotNullPojoAttributes = true
        isKotlinNotNullRecordAttributes = true
        isKotlinNotNullInterfaceAttributes = true
      }
      target {
        packageName = "fr.sdecout.repository.infrastructure.driven.jdbc.jooq"
      }
    }
  }
}

tasks.named("compileKotlin") {
  dependsOn(tasks.named("jooqCodegen"))
}
