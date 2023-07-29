package org.learning.kafka.opensearch;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.GetIndexRequest;
import org.opensearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

public final class RestHighLevelClientFacade {

    private static final Logger log = LoggerFactory.getLogger(RestHighLevelClientFacade.class.getSimpleName());

    private RestHighLevelClientFacade() {
    }

    public static RestHighLevelClient createRestHighLevelClient() {
        String serverUrl = "http://localhost:9200";
        URI serverUri = URI.create(serverUrl);
        String userInfo = serverUri.getUserInfo();

        if (Objects.isNull(userInfo)) {
            return new RestHighLevelClient(RestClient.builder(new HttpHost(serverUri.getHost(), serverUri.getPort(), HttpHost.DEFAULT_SCHEME_NAME)));
        } else {
            String[] auth = userInfo.split(":");

            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(auth[0], auth[1]));

            return new RestHighLevelClient(
                    RestClient.builder(new HttpHost(serverUri.getHost(), serverUri.getPort(), serverUri.getScheme()))
                            .setHttpClientConfigCallback(
                                    httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                                            .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())));
        }
    }

    public static void createIndex(RestHighLevelClient client, String index) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(index);
        client.indices().create(request, RequestOptions.DEFAULT);

        log.info("Index '{}' has been created", index);
    }

    public static boolean indexExists(RestHighLevelClient client, String index) throws IOException {
        GetIndexRequest request = new GetIndexRequest(index);
        return client.indices().exists(request, RequestOptions.DEFAULT);
    }

    public static void request(RestHighLevelClient client, String index, String document) throws IOException {
        IndexRequest request = new IndexRequest(index)
                .source(document, XContentType.JSON);

        IndexResponse response = client.index(request, RequestOptions.DEFAULT);

        log.info("Document is indexed: {}", response.getId());
    }

}
