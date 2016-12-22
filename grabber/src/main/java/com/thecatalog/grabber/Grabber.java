package com.thecatalog.grabber;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class Grabber {

    public static void main(String[] args) throws Exception {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(3000)
                .setConnectTimeout(3000).build();
        try (CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build()) {
            httpclient.start();
            final HttpGet httpGet = new HttpGet("https://api.themoviedb.org/3/discover/movie?api_key=a5b5f4346233f9d54901fbc84c35ef74&page=1&release_date.gte=1900-01-01&release_date.lte=2016-12-21");
            final CountDownLatch latch = new CountDownLatch(1);

            httpclient.execute(httpGet, new FutureCallback<HttpResponse>() {
                @Override
                public void completed(final HttpResponse response) {
                    latch.countDown();
                    System.out.println(httpGet.getRequestLine() + "->" + response.getStatusLine());
                    try {
                        System.out.println(EntityUtils.toString(response.getEntity()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failed(final Exception ex) {
                    latch.countDown();
                    System.out.println(httpGet.getRequestLine() + "->" + ex);
                }

                @Override
                public void cancelled() {
                    latch.countDown();
                    System.out.println(httpGet.getRequestLine() + " cancelled");
                }

            });

            latch.await();
            System.out.println("Shutting down");
        }
        System.out.println("Done");
    }
}
