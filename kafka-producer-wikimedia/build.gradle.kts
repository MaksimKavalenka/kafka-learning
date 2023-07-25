plugins {
    java
    id("io.spring.dependency-management") version "1.1.2"
}

group = "org.learning.kafka"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.kafka:kafka-clients")

    implementation("org.slf4j:slf4j-api")
    implementation("org.slf4j:slf4j-simple")

    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.launchdarkly:okhttp-eventsource")
}

dependencyManagement {
    dependencies {
        dependency("org.apache.kafka:kafka-clients:3.5.1")

        dependencySet("org.slf4j:2.0.7") {
            entry("slf4j-api")
            entry("slf4j-simple")
        }

        dependency("com.squareup.okhttp3:okhttp:4.11.0")
        dependency("com.launchdarkly:okhttp-eventsource:4.1.1")
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
