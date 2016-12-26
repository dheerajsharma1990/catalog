package com.thecatalog.grabber.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TheMovieGrabber {

    private static List<List<LocalDate>> distribute(CloseableHttpClient httpClient, LocalDate startDate, LocalDate endDate) throws Exception {
        if (startDate.isBefore(endDate) || startDate.isEqual(endDate)) {
            ObjectMapper objectMapper = new ObjectMapper();
            HttpUriRequest httpUriRequest = RequestBuilder.get("https://api.themoviedb.org/3/discover/movie")
                    .addParameter("api_key", "a5b5f4346233f9d54901fbc84c35ef74")
                    .addParameter("release_date.gte", startDate.format(DateTimeFormatter.ISO_DATE))
                    .addParameter("release_date.lte", endDate.format(DateTimeFormatter.ISO_DATE))
                    .build();
            try(CloseableHttpResponse response = httpClient.execute(httpUriRequest)) {
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    String json = EntityUtils.toString(entity);
                    JsonNode jsonNode = objectMapper.readValue(json, JsonNode.class);
                    int totalPages = jsonNode.get("total_pages").asInt();
                    if (totalPages <= 1000) {
                        return Arrays.asList(Arrays.asList(new LocalDate[]{startDate, endDate}));
                    } else {
                        long days = ChronoUnit.DAYS.between(startDate, endDate);
                        LocalDate mid = startDate.plusDays(days / 2);
                        List<List<LocalDate>> firstList = distribute(httpClient, startDate, mid);
                        List<List<LocalDate>> secondList = distribute(httpClient, mid.plusDays(1), endDate);
                        List<List<LocalDate>> newList = new ArrayList<>();
                        newList.addAll(firstList);
                        newList.addAll(secondList);
                        return newList;
                    }
                } else {
                    Header retryHeader = response.getFirstHeader("Retry-After");
                    Thread.sleep((retryHeader != null ? Integer.valueOf(retryHeader.getValue()) + 1 : 5) * 1000);
                    return distribute(httpClient, startDate, endDate);
                }
            }
        }
        return new ArrayList<>();
    }

    private static List<Integer> fetchRange(CloseableHttpClient httpClient, LocalDate startDate, LocalDate endDate) throws Exception {
        int pageCount = getPagesCount(httpClient, startDate, endDate);
        List<Integer> ans = new ArrayList<>();
        System.out.println("Fetching for range " + startDate + "  " + endDate + " for total pages " + pageCount + ".");
        for (int page = 1; page <= pageCount; page++) {
            ans.addAll(fetchIds(httpClient, startDate, endDate, page));
        }
        return ans;
    }

    private static List<Integer> fetchIds(CloseableHttpClient httpClient, LocalDate startDate, LocalDate endDate, int page) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        HttpUriRequest httpUriRequest = RequestBuilder.get("https://api.themoviedb.org/3/discover/movie")
                .addParameter("api_key", "a5b5f4346233f9d54901fbc84c35ef74")
                .addParameter("release_date.gte", startDate.format(DateTimeFormatter.ISO_DATE))
                .addParameter("release_date.lte", endDate.format(DateTimeFormatter.ISO_DATE))
                .addParameter("page", String.valueOf(page))
                .build();
        try (CloseableHttpResponse response = httpClient.execute(httpUriRequest)) {
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                JsonNode jsonNode = objectMapper.readValue(json, JsonNode.class);
                JsonNode results = jsonNode.get("results");
                int size = results.size();
                List<Integer> ids = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    ids.add(results.get(i).get("id").asInt());
                }
                return ids;
            } else if (statusCode == 429) {
                Header retryHeader = response.getFirstHeader("Retry-After");
                Thread.sleep(((Integer.valueOf(retryHeader.getValue()) + 1) * 1000));
                return fetchIds(httpClient, startDate, endDate, page);
            }
        }
        return new ArrayList<>();
    }

    private static int getPagesCount(CloseableHttpClient httpClient, LocalDate startDate, LocalDate endDate) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        HttpUriRequest httpUriRequest = RequestBuilder.get("https://api.themoviedb.org/3/discover/movie")
                .addParameter("api_key", "a5b5f4346233f9d54901fbc84c35ef74")
                .addParameter("release_date.gte", startDate.format(DateTimeFormatter.ISO_DATE))
                .addParameter("release_date.lte", endDate.format(DateTimeFormatter.ISO_DATE))
                .build();
        try(CloseableHttpResponse response = httpClient.execute(httpUriRequest)) {
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);
                JsonNode jsonNode = objectMapper.readValue(json, JsonNode.class);
                return jsonNode.get("total_pages").asInt();
            } else if (statusCode == 429) {
                Header retryHeader = response.getFirstHeader("Retry-After");
                Thread.sleep((retryHeader != null ? Integer.valueOf(retryHeader.getValue()) + 1 : 5) * 1000);
                return getPagesCount(httpClient, startDate, endDate);
            }
        }
        return -1;
    }

    public static void main(String args[]) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        long startTime = System.currentTimeMillis();
        List<List<LocalDate>> ranges = distribute(httpclient, LocalDate.of(2010, 1, 1), LocalDate.now());
        long endTime = System.currentTimeMillis();
        System.out.println("Total Ranges: " + ranges.size());
        System.out.println("Took " + ((endTime - startTime) / 1000) + " seconds to compute ranges.");
        for (List<LocalDate> range : ranges) {
            fetchRange(httpclient, range.get(0), range.get(1));
        }
        httpclient.close();
    }


}
