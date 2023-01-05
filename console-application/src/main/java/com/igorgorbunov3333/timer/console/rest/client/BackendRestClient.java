package com.igorgorbunov3333.timer.console.rest.client;

import com.igorgorbunov3333.timer.console.config.properties.BackendRestProperties;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class BackendRestClient {

    private final RestTemplate restTemplate;
    private final BackendRestProperties backendRestProperties;

    public <T, R> R post(String relativePath, Class<R> clazz, T entity) {
        URI uri = buildURI(relativePath, null);

        ResponseEntity<R> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(entity),
                clazz
        );

        return responseEntity.getBody();
    }

    public <T> T get(String relativePath, Class<T> clazz, Map<String, String> queryParams) {
        URI uri = buildURI(relativePath, queryParams);

        ResponseEntity<T> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                clazz);

        return responseEntity.getBody();
    }

    public <T> T get(String relativePath,
                     ParameterizedTypeReference<T> parameterizedTypeReference,
                     Map<String, String> queryParams) {
        URI uri = buildURI(relativePath, queryParams);

        ResponseEntity<T> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                parameterizedTypeReference);

        return responseEntity.getBody();
    }

    public <T> void put(String relativePath, T entity) {
        URI uri = buildURI(relativePath, null);
        
        restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(entity), String.class);
    }

    public void delete(String relativePath, Map<String, String> queryParams) {
        URI uri = buildURI(relativePath, queryParams);

        restTemplate.delete(uri);
    }

    @SneakyThrows
    private URI buildURI(String relativePath, Map<String, String> queryParams) {
        List<NameValuePair> nameValuePairs = new ArrayList<>();

        URIBuilder uriBuilder = new URIBuilder()
                .setScheme(backendRestProperties.getScheme())
                .setHost(backendRestProperties.getHost())
                .setPath(backendRestProperties.getBasePath() + relativePath);

        if (!CollectionUtils.isEmpty(queryParams)) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }

            uriBuilder.setParameters(nameValuePairs);
        }

        return uriBuilder.build();
    }

}
