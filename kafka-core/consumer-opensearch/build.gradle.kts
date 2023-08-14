dependencies {
    implementation(project(":kafka-core:common"))

    implementation("com.google.code.gson:gson")

    implementation("org.apache.kafka:kafka-clients")

    implementation("org.opensearch.client:opensearch-rest-high-level-client")

    implementation("org.slf4j:slf4j-api")
    implementation("org.slf4j:slf4j-simple")
}
