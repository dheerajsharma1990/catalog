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

import java.util.*;

public class MovieDetailsGrabber {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpRequestExecutor httpRequestExecutor;

    public MovieDetailsGrabber(HttpRequestExecutor httpRequestExecutor) {
        this.httpRequestExecutor = httpRequestExecutor;
    }

    public Set<Movie> grab(Collection<String> ids) {
        try {
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
