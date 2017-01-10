package com.findmymovie.grabber;

import com.findmymovie.domain.Movie;
import com.findmymovie.domain.repository.MoviesRepository;
import com.findmymovie.grabber.http.HttpRequestExecutor;
import com.findmymovie.grabber.http.LinksDistributor;
import com.findmymovie.domain.ReleaseDateRange;
import com.findmymovie.grabber.http.MovieDetailsGrabber;
import com.findmymovie.grabber.http.ReleaseDatePageGrabber;
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

public class TheMovieDBGrabberRunner implements MoviesRepository {

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
        TheMovieDBGrabberRunner movieDBGrabberRunner = new TheMovieDBGrabberRunner();
        long startTime = System.currentTimeMillis();
        Collection<Movie> movies = movieDBGrabberRunner.getMovies(new ReleaseDateRange(startDate, endDate));
        long endTime = System.currentTimeMillis();
        long diff = endTime - startTime;
        System.out.println("Time taken: " + diff + " millis which is " + diff/1000 + " seconds which is " + diff/(60*1000) + " minutes.");
        System.out.println("Successfully fetched " + movies.size() + " movies.");
    }

    @Override
    public Collection<Movie> getMovies(ReleaseDateRange releaseDateRange) {
        try {
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
            try (CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
                    .setConnectionManager(connManager)
                    .build()) {

                httpclient.start();

                HttpRequestExecutor httpRequestExecutor = new HttpRequestExecutor(httpclient);
                LinksDistributor linksDistributor = new LinksDistributor(httpRequestExecutor);
                ReleaseDatePageGrabber releaseDatePageGrabber = new ReleaseDatePageGrabber(httpRequestExecutor);
                MovieDetailsGrabber movieDetailsGrabber = new MovieDetailsGrabber(httpRequestExecutor);

                Collection<ReleaseDateRange> releaseDateRanges = linksDistributor.distribute(releaseDateRange);
                System.out.println("Fetching for total of " + releaseDateRanges.size() + " pages.");
                Set<String> allMovieIds = releaseDateRanges.stream()
                        .map(releaseDatePageGrabber::fetch)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet());
                return movieDetailsGrabber.grab(allMovieIds);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
