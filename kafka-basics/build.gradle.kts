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
}

dependencyManagement {
    dependencies {
        dependency("org.apache.kafka:kafka-clients:3.5.1")

        dependencySet("org.slf4j:2.0.7") {
            entry("slf4j-api")
            entry("slf4j-simple")
        }
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
