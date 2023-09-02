dependencies {
    implementation(project(":kafka-common"))

    implementation("com.fasterxml.jackson.core:jackson-databind")

    implementation("org.apache.kafka:connect-json")
    implementation("org.apache.kafka:kafka-streams")

    implementation("org.slf4j:slf4j-api")
    implementation("org.slf4j:slf4j-simple")
}
