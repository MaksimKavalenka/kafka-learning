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
}

dependencyManagement {
    dependencies {
        dependency("org.apache.kafka:kafka-clients:3.5.1")
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
