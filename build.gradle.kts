plugins {
    java
    id("io.spring.dependency-management") version "1.1.2"
}

subprojects {
    apply {
        plugin("java")
        plugin("io.spring.dependency-management")
    }

    group = "org.learning.kafka"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
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
}
