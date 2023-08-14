dependencies {
    implementation(project(":kafka-core:common"))

    implementation("com.launchdarkly:okhttp-eventsource")
    implementation("com.squareup.okhttp3:okhttp")

    implementation("org.apache.kafka:kafka-clients")

    implementation("org.slf4j:slf4j-api")
    implementation("org.slf4j:slf4j-simple")
}
