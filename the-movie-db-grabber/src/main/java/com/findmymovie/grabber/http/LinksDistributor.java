package com.findmymovie.grabber.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.findmymovie.domain.ReleaseDateRange;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LinksDistributor {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpRequestExecutor httpRequestExecutor;

    public LinksDistributor(HttpRequestExecutor httpRequestExecutor) {
        this.httpRequestExecutor = httpRequestExecutor;
    }

    public Collection<ReleaseDateRange> distribute(ReleaseDateRange releaseDateRange) throws Exception {
        if (releaseDateRange.isStartDateBeforeEndDate() || releaseDateRange.isStartDateEqualToEndDate()) {
            HttpUriRequest httpUriRequest = RequestBuilder.get("https://api.themoviedb.org/3/discover/movie")
                    .addParameter("api_key", "a5b5f4346233f9d54901fbc84c35ef74")
                    .addParameter("release_date.gte", releaseDateRange.getStartDate().format(DateTimeFormatter.ISO_DATE))
                    .addParameter("release_date.lte", releaseDateRange.getEndDate().format(DateTimeFormatter.ISO_DATE))
                    .build();
            Collection<String> responseList = httpRequestExecutor.execute(Arrays.asList(httpUriRequest));
            String jsonResponse = responseList.iterator().next();
            JsonNode jsonNode = objectMapper.readValue(jsonResponse, JsonNode.class);
            int totalPages = jsonNode.get("total_pages").asInt();
            if (totalPages <= 1000) {
                return Arrays.asList(releaseDateRange);
            } else {
                return Stream.concat(
                        distribute(releaseDateRange.getFirstHalf()).stream(),
                        distribute(releaseDateRange.getSecondHalf()).stream())
                        .collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }

}
