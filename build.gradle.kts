import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
plugins {
    val kotlinVersion = "1.8.0"

    id("org.springframework.boot") version "2.7.5"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    id("jacoco")
    id("io.gitlab.arturbosch.detekt").version("1.21.0")
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"

    id("org.springdoc.openapi-gradle-plugin") version "1.5.0"
    id("org.gradle.test-retry") version "1.5.9"
}

val buildId = System.getenv("GITHUB_RUN_NUMBER") ?: System.getenv("BUILD_ID") ?: "1-SNAPSHOT"

group = "com.katalisindonesia.banyuwangi"
version = "0.0.$buildId"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencyManagement {
    imports {
        mavenBom("org.keycloak.bom:keycloak-adapter-bom:24.0.2")
    }
}

configurations {
    implementation {
        exclude(module = "spring-boot-starter-tomcat")
    }
}

dependencies {
    // coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-undertow")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    // implementation("org.springframework.security:spring-security-oauth2-client")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    implementation("org.hibernate:hibernate-envers")

    val springdocVersion = "1.6.13"
    implementation("org.springdoc:springdoc-openapi-ui:$springdocVersion")
    implementation("org.springdoc:springdoc-openapi-security:$springdocVersion")
    implementation("org.springdoc:springdoc-openapi-kotlin:$springdocVersion")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-jackson:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-scalars:$retrofitVersion")

    val okHttpVersion = "4.10.0"
    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okHttpVersion")
    implementation("io.github.rburgst:okhttp-digest:2.7")

    implementation("com.github.03:onvif:1.0.9") {
        exclude(group = "com.burgstaller")
    }

    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("commons-io:commons-io:2.11.0")
    implementation("commons-codec:commons-codec:1.15")

    implementation("org.slf4j:slf4j-api")
    implementation("org.slf4j:jul-to-slf4j")

    implementation("io.github.microutils:kotlin-logging:3.0.4")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.amqp:spring-rabbit-test")
    testImplementation("org.springframework.security:spring-security-test")

    implementation("org.keycloak:keycloak-spring-security-adapter")
    implementation("org.keycloak:keycloak-spring-boot-starter")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")

    implementation("io.github.sercasti:spring-httpserver-timings:0.0.7")

    implementation("org.ehcache:ehcache")
    implementation("javax.cache:cache-api")
    testRuntimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.84.Final:osx-aarch_64")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.22.0")

    // Firebase cloud messaging
    implementation("com.google.firebase:firebase-admin:9.1.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    enableAssertions = true
    // setForkEvery(1L)
    retry {
        if (!buildId.contains("SNAPSHOT")) {
            maxRetries.set(3)
            maxFailures.set(20)
            failOnPassedAfterRetry.set(false)
        }
    }
    reports {
        junitXml.apply {
            isOutputPerTestCase = true // defaults to false
            mergeReruns.set(true) // defaults to false
        }
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
}
tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}
tasks.check {
    dependsOn(
        tasks.ktlintCheck,
        tasks.detekt,
        tasks.test,
        tasks.jacocoTestCoverageVerification,
    )
}

detekt {
    toolVersion = "1.21.0"
    config = files("config/detekt/detekt.yml")
    buildUponDefaultConfig = true
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            element = "SOURCEFILE"
            limit {
                counter = "LINE"
                minimum = "0.0".toBigDecimal()
            }
            excludes = listOf(
                "**/*\$log\$*.class",
                "com/katalisindonesia/banyuwangi/BanyuwangiCoreApplication*.*",
                "com/katalisindonesia/banyuwangi/controller/RestExceptionHandler*.*",
            )
        }
    }
}

openApi {
    waitTimeInSeconds.set(120)
}
