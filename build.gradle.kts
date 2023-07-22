plugins {
    java
}

group = "org.learning.kafka"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
