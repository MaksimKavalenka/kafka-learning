rootProject.name = "kafka-learning"

include("kafka-core")
include("kafka-core:basics")
findProject(":kafka-core:basics")?.name = "basics"
include("kafka-core:common")
findProject(":kafka-core:common")?.name = "common"
include("kafka-core:consumer-opensearch")
findProject(":kafka-core:consumer-opensearch")?.name = "consumer-opensearch"
include("kafka-core:producer-wikimedia")
findProject(":kafka-core:producer-wikimedia")?.name = "producer-wikimedia"

include("kafka-streams")
include("kafka-streams:common")
findProject(":kafka-streams:common")?.name = "common"
include("kafka-streams:favorite-color")
findProject(":kafka-streams:favorite-color")?.name = "favorite-color"
include("kafka-streams:word-count")
findProject(":kafka-streams:word-count")?.name = "word-count"
