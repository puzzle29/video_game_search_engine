package fr.lernejo.search.api;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfiguration {

    @Bean
    public RestHighLevelClient restHighLevelClient(
        @Value("${elasticsearch.host:localhost}") String host,
        @Value("${elasticsearch.port:9200}") int port,
        @Value("${elasticsearch.username:elastic}") String username,
        @Value("${elasticsearch.password:admin}") String password) {

        RestClientBuilder restClientBuilder = RestClient.builder(
            new HttpHost(host, port, "http")
        );
        restClientBuilder.setHttpClientConfigCallback(
            httpClientBuilder -> httpClientBuilder
                .setDefaultCredentialsProvider(createCredentialsProvider(username, password))
        );

        return new RestHighLevelClient(restClientBuilder);
    }

    private CredentialsProvider createCredentialsProvider(String username, String password) {
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
            AuthScope.ANY,
            new UsernamePasswordCredentials(username, password)
        );
        return credentialsProvider;
    }
}

