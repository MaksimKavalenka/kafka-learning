dependencies {
    implementation(project(":kafka-streams:common"))

    implementation("org.apache.kafka:kafka-streams")

    implementation("org.slf4j:slf4j-api")
    implementation("org.slf4j:slf4j-simple")
}
