package com.thecatalog.grabber.http;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.util.EntityUtils;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Future;

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

}
