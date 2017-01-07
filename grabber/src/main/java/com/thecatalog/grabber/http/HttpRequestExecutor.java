package com.thecatalog.grabber.http;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.util.EntityUtils;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class HttpRequestExecutor {

    private final CloseableHttpAsyncClient httpAsyncClient;

    public HttpRequestExecutor(CloseableHttpAsyncClient httpAsyncClient) {
        this.httpAsyncClient = httpAsyncClient;
    }


    public List<String> execute(Collection<HttpUriRequest> httpUriRequests) throws Exception {
        List<String> result = new ArrayList<>();
        Queue<HttpUriRequest> queue = new ConcurrentLinkedDeque<>();

        for (HttpUriRequest httpUriRequest : httpUriRequests) {
            queue.offer(httpUriRequest);
        }

        while (!queue.isEmpty()) {
            Map<HttpUriRequest, Future<HttpResponse>> futures = new HashMap<>();
            for (int i = 0; i < 40 && !queue.isEmpty(); i++) {
                HttpUriRequest httpUriRequest = queue.poll();
                futures.put(httpUriRequest, httpAsyncClient.execute(httpUriRequest, null));
            }
            List<Long> rateResetTime = new ArrayList<>();
            List<Long> timeToWait = new ArrayList<>();
            long startTime = System.currentTimeMillis();
            for (Map.Entry<HttpUriRequest, Future<HttpResponse>> futureEntry : futures.entrySet()) {
                HttpUriRequest httpUriRequest = futureEntry.getKey();
                Future<HttpResponse> future = futureEntry.getValue();
                HttpResponse httpResponse = future.get();
                StatusLine statusLine = httpResponse.getStatusLine();
                if (statusLine.getStatusCode() == 200) {
                    rateResetTime.add(Long.valueOf(httpResponse.getFirstHeader("X-RateLimit-Reset").getValue()));
                    result.add(EntityUtils.toString(httpResponse.getEntity()));
                } else if (statusLine.getStatusCode() == 429) {
                    timeToWait.add(Long.valueOf(httpResponse.getFirstHeader("Retry-After").getValue()));
                    queue.offer(httpUriRequest);
                } else {
                    throw new RuntimeException("Received status code " + statusLine.getStatusCode() + " for request " + httpUriRequest.getURI());
                }
            }
            long endTime = System.currentTimeMillis();
            if (!queue.isEmpty()) {
                long toWait = 0l;
                if (!timeToWait.isEmpty()) {
                    for (Long time : timeToWait) {
                        toWait = Math.max(toWait, time * 1000);
                    }
                } else if (!rateResetTime.isEmpty()) {
                    for (Long time : rateResetTime) {
                        toWait = Math.max(toWait, time);
                    }
                }
                toWait = Math.min(toWait - (endTime - startTime) + 1000, 10000);
                Thread.sleep(Math.max(toWait, 0));
            }
        }
        return result;
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

        Collection<HttpUriRequest> allRequest = new ArrayList<>();
        for (int i = 1; i <= 600; i++) {
            allRequest.add(RequestBuilder.get("https://api.themoviedb.org/3/discover/movie")
                    .addParameter("api_key", "a5b5f4346233f9d54901fbc84c35ef74")
                    .addParameter("release_date.gte", "2015-01-01")
                    .addParameter("release_date.lte", "2015-12-31")
                    .addParameter("page", String.valueOf(i))
                    .build());
        }

        HttpRequestExecutor httpRequestExecutor = new HttpRequestExecutor(httpclient);
        long startTime = System.currentTimeMillis();
        Collection<String> result = httpRequestExecutor.execute(allRequest);
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + " millis.");
    }

}
