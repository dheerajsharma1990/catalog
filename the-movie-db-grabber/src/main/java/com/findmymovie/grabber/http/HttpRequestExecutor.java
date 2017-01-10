package com.findmymovie.grabber.http;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HttpRequestExecutor {

    private final CloseableHttpAsyncClient httpAsyncClient;

    public HttpRequestExecutor(CloseableHttpAsyncClient httpAsyncClient) {
        this.httpAsyncClient = httpAsyncClient;
    }


    public List<String> execute(Collection<HttpUriRequest> httpUriRequests) throws Exception {

        Queue<HttpUriRequest> queue = httpUriRequests
                .stream()
                .collect(ArrayDeque<HttpUriRequest>::new, ArrayDeque::offer, ArrayDeque::addAll);
        List<String> result = new ArrayList<>();

        while (!queue.isEmpty()) {
            Map<HttpUriRequest, Future<HttpResponse>> futures = IntStream.range(0, Math.min(40, queue.size()))
                    .mapToObj(num -> queue.poll())
                    .collect(Collectors.toMap(Function.identity(), httpUriRequest -> httpAsyncClient.execute(httpUriRequest, null)));

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

    public void close() throws IOException {
        this.httpAsyncClient.close();
    }

}
