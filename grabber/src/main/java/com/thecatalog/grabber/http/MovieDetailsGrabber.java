package com.thecatalog.grabber.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thecatalog.domain.Genre;
import com.thecatalog.domain.Movie;
import com.thecatalog.domain.ProductionCompany;
import com.thecatalog.domain.id.GenreId;
import com.thecatalog.domain.id.MovieId;
import com.thecatalog.domain.id.ProductionCompanyId;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MovieDetailsGrabber {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpRequestExecutor httpRequestExecutor;

    public MovieDetailsGrabber(HttpRequestExecutor httpRequestExecutor) {
        this.httpRequestExecutor = httpRequestExecutor;
    }

    public Set<Movie> grab(Collection<String> ids) throws Exception {
        Set<Movie> movies = new HashSet<>();
        Collection<HttpUriRequest> httpUriRequests = new ArrayList<>();
        for (String id : ids) {
            httpUriRequests.add(RequestBuilder.get("https://api.themoviedb.org/3/movie" + "/" + id)
                    .addParameter("api_key", "a5b5f4346233f9d54901fbc84c35ef74")
                    .build());
        }
        List<String> responseJson = httpRequestExecutor.execute(httpUriRequests);
        for (String json : responseJson) {
            JsonNode jsonNode = objectMapper.readTree(json);
            JsonNode genreNodes = jsonNode.get("genres");
            Collection<Genre> genres = new HashSet<>();
            for (int i = 0; i < genreNodes.size(); i++) {
                JsonNode genreNode = genreNodes.get(i);
                genres.add(new Genre(new GenreId(genreNode.get("id").asInt()), genreNode.get("name").asText()));
            }

            JsonNode productionCompanyNodes = jsonNode.get("genres");
            Collection<ProductionCompany> productionCompanies = new HashSet<>();
            for (int i = 0; i < productionCompanyNodes.size(); i++) {
                JsonNode productionCompanyNode = productionCompanyNodes.get(i);
                productionCompanies.add(new ProductionCompany(new ProductionCompanyId(productionCompanyNode.get("id").asInt()),
                        productionCompanyNode.get("name").asText()));
            }

            movies.add(new Movie(new MovieId(jsonNode.get("id").asInt()), jsonNode.get("original_title").asText(), jsonNode.get("title").asText(),
                    jsonNode.get("tagline").asText(), jsonNode.get("overview").asText(), jsonNode.get("status").asText(), jsonNode.get("homepage").asText(),
                    jsonNode.get("adult").asBoolean(), genres, productionCompanies, jsonNode.get("budget").asInt()));
        }
        return movies;
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
        Collection<String> ids = releaseDatePageGrabber.fetch(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 6, 30));
        MovieDetailsGrabber movieDetailsGrabber = new MovieDetailsGrabber(httpRequestExecutor);
        Set<Movie> movies = movieDetailsGrabber.grab(ids.stream().limit(400).collect(Collectors.toList()));
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + " millis.");
        httpclient.close();
    }
}
