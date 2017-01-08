package com.thecatalog.grabber;

import com.thecatalog.grabber.http.HttpRequestExecutor;
import com.thecatalog.grabber.http.LinksDistributor;
import com.thecatalog.grabber.http.ReleaseDateRange;
import com.thecatalog.grabber.http.ReleaseDatePageGrabber;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TheMovieDBGrabberRunner {

    /**
     * First Argument: YYYY-MM-DD (Release Start Date inclusive)
     * Second Argument: YYYY-MM-DD (Release End Date inclusive)
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            throw new RuntimeException("The program requires two arguments.Release start date inclusive (YYYY-MM-DD) & Release end date inclusive (YYYY-MM-DD).");
        }
        LocalDate startDate = LocalDate.parse(args[0], DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(args[1], DateTimeFormatter.ISO_DATE);

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
        LinksDistributor linksDistributor = new LinksDistributor(httpRequestExecutor);
        ReleaseDatePageGrabber releaseDatePageGrabber = new ReleaseDatePageGrabber(httpRequestExecutor);
        long startTime = System.currentTimeMillis();
        Collection<ReleaseDateRange> releaseDateRanges = linksDistributor.distribute(new ReleaseDateRange(startDate, endDate));
        System.out.println("Fetching for total of " + releaseDateRanges.size() + " pages.");
        Set<String> allMovieIds = releaseDateRanges.stream()
                .map(releaseDatePageGrabber::fetch)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + " millis to fetch " + allMovieIds.size() + " movie ids.");
        httpclient.close();
    }
}
