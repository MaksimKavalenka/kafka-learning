plugins {
    java
    id("io.spring.dependency-management") version "1.1.3"
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
            dependency("com.fasterxml.jackson.core:jackson-databind:2.15.2")

            dependency("com.google.code.gson:gson:2.10.1")

            dependency("com.launchdarkly:okhttp-eventsource:4.1.1")
            dependency("com.squareup.okhttp3:okhttp:4.11.0")

            dependencySet("org.apache.kafka:3.5.1") {
                entry("connect-json")
                entry("kafka-clients")
                entry("kafka-streams")
            }

            dependency("org.opensearch.client:opensearch-rest-high-level-client:2.9.0")

            dependencySet("org.slf4j:2.0.7") {
                entry("slf4j-api")
                entry("slf4j-simple")
            }
        }
    }

    tasks.getByName<Test>("test") {
        useJUnitPlatform()
    }
}
