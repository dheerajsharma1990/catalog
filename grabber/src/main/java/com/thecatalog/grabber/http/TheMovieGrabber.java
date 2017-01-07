package com.thecatalog.grabber.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TheMovieGrabber {

    private static List<List<LocalDate>> distribute(CloseableHttpClient httpClient, LocalDate startDate, LocalDate endDate) throws Exception {
        if (startDate.isBefore(endDate) || startDate.isEqual(endDate)) {
            ObjectMapper objectMapper = new ObjectMapper();
            HttpUriRequest httpUriRequest = RequestBuilder.get("https://api.themoviedb.org/3/discover/movie")
                    .addParameter("api_key", "a5b5f4346233f9d54901fbc84c35ef74")
                    .addParameter("release_date.gte", startDate.format(DateTimeFormatter.ISO_DATE))
                    .addParameter("release_date.lte", endDate.format(DateTimeFormatter.ISO_DATE))
                    .build();
            try (CloseableHttpResponse response = httpClient.execute(httpUriRequest)) {
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
        try (CloseableHttpResponse response = httpClient.execute(httpUriRequest)) {
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
        Queue<HttpUriRequest> allRequest = new ConcurrentLinkedDeque<>();
        for (int i = 1; i <= 200; i++) {
            allRequest.offer(RequestBuilder.get("https://api.themoviedb.org/3/discover/movie")
                    .addParameter("api_key", "a5b5f4346233f9d54901fbc84c35ef74")
                    .addParameter("release_date.gte", "2015-01-01")
                    .addParameter("release_date.lte", "2015-12-31")
                    .addParameter("page", String.valueOf(i))
                    .build());
        }
        long totalStartTime = System.currentTimeMillis();
        List<Long> limit = new ArrayList<>();
        List<Long> timeAfterRetry = new ArrayList<>();
        while (!allRequest.isEmpty()) {
            limit.clear();
            CountDownLatch latch = new CountDownLatch(Math.min(allRequest.size(), 40));
            for (int i = 0; i < 40; i++) {
                timeAfterRetry.clear();
                if (allRequest.isEmpty()) {
                    break;
                }
                HttpUriRequest httpUriRequest = allRequest.poll();
                httpclient.execute(httpUriRequest, new FutureCallback<HttpResponse>() {
                    @Override
                    public void completed(HttpResponse result) {
                        StatusLine statusLine = result.getStatusLine();
                        int statusCode = statusLine.getStatusCode();
                        if (statusCode == 200) {
                            limit.add(Long.valueOf(result.getFirstHeader("X-RateLimit-Reset").getValue()));
                            try {
                                EntityUtils.toString(result.getEntity());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else if (statusCode == 429) {
                            Long val = Long.valueOf(result.getFirstHeader("Retry-After").getValue());
                            timeAfterRetry.add(val);
                            allRequest.offer(httpUriRequest);
                            System.out.println("Will have to wait for " + val + " seconds.");
                        } else {
                            System.out.println("Received status " + statusCode);
                        }
                        latch.countDown();
                    }

                    @Override
                    public void failed(Exception ex) {
                        latch.countDown();
                        System.out.println("Request failed..");
                        ex.printStackTrace();
                    }

                    @Override
                    public void cancelled() {
                        latch.countDown();
                        System.out.println("Request Cancelled..");
                    }
                });
            }
            latch.await();
            if (!allRequest.isEmpty()) {
                if (!timeAfterRetry.isEmpty()) {
                    long max = 0l;
                    for (Long time : timeAfterRetry) {
                        max = Math.max(max, time);
                        Thread.sleep(max + 1000);
                    }
                } else {
                    long toWait = limit.get(limit.size() - 1) * 1000 - System.currentTimeMillis();
                    System.out.println("Waiting for " + Math.min(10000, toWait + 1000) + " millis.");
                    Thread.sleep(Math.min(10000, toWait + 1000));
                }
            }
        }
        long totalEndTime = System.currentTimeMillis();
        System.out.println("Total time " + (totalEndTime - totalStartTime) + " seconds.");
        httpclient.close();
        /*CloseableHttpClient httpClient = HttpClients.createDefault();
        long totalStartTime = System.currentTimeMillis();
        for (int i = 1; i <= 100; i++) {
            //long startTime = System.currentTimeMillis();
            CloseableHttpResponse closeableHttpResponse = httpClient.execute(RequestBuilder.get("https://reqres.in/api/users")
                    .addParameter("page", String.valueOf((i % 4) + 1))
                    .build());
            closeableHttpResponse.close();
            // long endTime = System.currentTimeMillis();
            //System.out.println("Took " + (endTime - startTime) + " milliseconds to execute request.");

        }
        long totalEndTime = System.currentTimeMillis();
        System.out.println("Total time " + (totalEndTime - totalStartTime) + " seconds.");

        httpClient.close();*/
    }


}
