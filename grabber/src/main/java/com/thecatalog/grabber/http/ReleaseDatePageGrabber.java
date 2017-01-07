package com.thecatalog.grabber.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ReleaseDatePageGrabber {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpRequestExecutor httpRequestExecutor;

    public ReleaseDatePageGrabber(HttpRequestExecutor httpRequestExecutor) {
        this.httpRequestExecutor = httpRequestExecutor;
    }


    public Set<String> fetch(LocalDate releaseDateAfter, LocalDate releaseDateBefore) throws Exception {
        HttpUriRequest httpUriRequest = RequestBuilder.get("https://api.themoviedb.org/3/discover/movie")
                .addParameter("api_key", "a5b5f4346233f9d54901fbc84c35ef74")
                .addParameter("release_date.gte", releaseDateAfter.format(DateTimeFormatter.ISO_DATE))
                .addParameter("release_date.lte", releaseDateBefore.format(DateTimeFormatter.ISO_DATE))
                .build();
        List<String> jsonList = httpRequestExecutor.execute(Arrays.asList(httpUriRequest));
        String json = jsonList.get(0);
        JsonNode jsonNode = objectMapper.readTree(json);
        int totalPages = jsonNode.get("total_pages").intValue();
        Collection<HttpUriRequest> httpUriRequests = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            httpUriRequests.add(RequestBuilder.get("https://api.themoviedb.org/3/discover/movie")
                    .addParameter("api_key", "a5b5f4346233f9d54901fbc84c35ef74")
                    .addParameter("release_date.gte", releaseDateAfter.format(DateTimeFormatter.ISO_DATE))
                    .addParameter("release_date.lte", releaseDateBefore.format(DateTimeFormatter.ISO_DATE))
                    .addParameter("page", String.valueOf(i))
                    .build());
        }
        Collection<String> result = httpRequestExecutor.execute(httpUriRequests);
        Set<String> ids = new HashSet<>();
        for (String jsonPage : result) {
            JsonNode node = objectMapper.readTree(jsonPage);
            JsonNode array = node.get("results");
            for (int i = 0; i < array.size(); i++) {
                ids.add(array.get(i).get("id").asText());
            }
        }
        return ids;
    }

    public static void main(String[] args) throws Exception {
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setIoThreadCount(Runtime.getRuntime().availableProcessors())
                .setConnectTimeout(30000)
                .setSoTimeout(30000)
                .build();
        ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
        PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(ioReactor);
        connManager.setDefaultMaxPerRoute(50);
        connManager.closeIdleConnections(1, TimeUnit.MINUTES);
        connManager.closeExpiredConnections();

        CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
                .setConnectionManager(connManager)
                .build();
        httpclient.start();

        HttpRequestExecutor httpRequestExecutor = new HttpRequestExecutor(httpclient);
        ReleaseDatePageGrabber releaseDatePageGrabber = new ReleaseDatePageGrabber(httpRequestExecutor);
        long startTime = System.currentTimeMillis();
        Collection<String> result = releaseDatePageGrabber.fetch(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 12, 31));
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + " millis.");
        httpclient.close();
    }

}
