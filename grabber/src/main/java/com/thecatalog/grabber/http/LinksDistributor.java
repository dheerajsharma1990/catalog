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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LinksDistributor {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpRequestExecutor httpRequestExecutor;

    public LinksDistributor(HttpRequestExecutor httpRequestExecutor) {
        this.httpRequestExecutor = httpRequestExecutor;
    }

    public Collection<ReleaseDate> distribute(ReleaseDate releaseDate) throws Exception {
        if (releaseDate.isStartDateBeforeEndDate() || releaseDate.isStartDateEqualToEndDate()) {
            HttpUriRequest httpUriRequest = RequestBuilder.get("https://api.themoviedb.org/3/discover/movie")
                    .addParameter("api_key", "a5b5f4346233f9d54901fbc84c35ef74")
                    .addParameter("release_date.gte", releaseDate.getStartDate().format(DateTimeFormatter.ISO_DATE))
                    .addParameter("release_date.lte", releaseDate.getEndDate().format(DateTimeFormatter.ISO_DATE))
                    .build();
            Collection<String> responseList = httpRequestExecutor.execute(Arrays.asList(httpUriRequest));
            String jsonResponse = responseList.iterator().next();
            JsonNode jsonNode = objectMapper.readValue(jsonResponse, JsonNode.class);
            int totalPages = jsonNode.get("total_pages").asInt();
            if (totalPages <= 1000) {
                return Arrays.asList(releaseDate);
            } else {
                return Stream.concat(
                        distribute(releaseDate.getFirstHalf()).stream(),
                        distribute(releaseDate.getSecondHalf()).stream())
                        .collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
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
        LinksDistributor linksDistributor = new LinksDistributor(httpRequestExecutor);
        ReleaseDatePageGrabber releaseDatePageGrabber = new ReleaseDatePageGrabber(httpRequestExecutor);
        long startTime = System.currentTimeMillis();
        Collection<ReleaseDate> releaseDates = linksDistributor.distribute(new ReleaseDate(LocalDate.of(1883, 1, 1), LocalDate.now()));
        System.out.println("Fetching for total of " + releaseDates.size() + " pages.");
        Set<String> allMovieIds = releaseDates.stream()
                .map(releaseDatePageGrabber::fetch)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + " millis to generate " + releaseDates.size() + " partitions.");
        httpclient.close();
    }
}
