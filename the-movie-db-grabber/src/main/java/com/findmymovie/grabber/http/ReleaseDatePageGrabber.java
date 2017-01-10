package com.findmymovie.grabber.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.findmymovie.domain.ReleaseDateRange;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReleaseDatePageGrabber {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpRequestExecutor httpRequestExecutor;

    public ReleaseDatePageGrabber(HttpRequestExecutor httpRequestExecutor) {
        this.httpRequestExecutor = httpRequestExecutor;
    }


    public Set<String> fetch(ReleaseDateRange releaseDateRange) {
        System.out.println("Fetching for dates " + releaseDateRange);
        try {
            HttpUriRequest httpUriRequest = RequestBuilder.get("https://api.themoviedb.org/3/discover/movie")
                    .addParameter("api_key", "a5b5f4346233f9d54901fbc84c35ef74")
                    .addParameter("release_date.gte", releaseDateRange.getStartDate().format(DateTimeFormatter.ISO_DATE))
                    .addParameter("release_date.lte", releaseDateRange.getEndDate().format(DateTimeFormatter.ISO_DATE))
                    .build();
            List<String> jsonList = httpRequestExecutor.execute(Arrays.asList(httpUriRequest));
            String json = jsonList.get(0);
            JsonNode jsonNode = objectMapper.readTree(json);
            int totalPages = jsonNode.get("total_pages").intValue();
            Collection<HttpUriRequest> httpUriRequests = new ArrayList<>();
            for (int i = 1; i <= totalPages; i++) {
                httpUriRequests.add(RequestBuilder.get("https://api.themoviedb.org/3/discover/movie")
                        .addParameter("api_key", "a5b5f4346233f9d54901fbc84c35ef74")
                        .addParameter("release_date.gte", releaseDateRange.getStartDate().format(DateTimeFormatter.ISO_DATE))
                        .addParameter("release_date.lte", releaseDateRange.getEndDate().format(DateTimeFormatter.ISO_DATE))
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
