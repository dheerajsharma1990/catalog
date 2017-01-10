package com.findmymovie.domain;

import com.findmymovie.domain.id.MovieId;

import java.util.Collection;

public class Movie {

    private MovieId movieId;

    private String originalTitle;

    private String title;

    private String tagLine;

    private String overview;

    private String status;

    private String homePage;

    private boolean adult;

    private Collection<Genre> genres;

    private Collection<ProductionCompany> productionCompanies;

    private int budget;

    public Movie(MovieId movieId, String originalTitle, String title, String tagLine, String overview, String status,
                 String homePage, boolean adult, Collection<Genre> genres, Collection<ProductionCompany> productionCompanies, int budget) {
        this.movieId = movieId;
        this.originalTitle = originalTitle;
        this.title = title;
        this.tagLine = tagLine;
        this.overview = overview;
        this.status = status;
        this.homePage = homePage;
        this.adult = adult;
        this.genres = genres;
        this.productionCompanies = productionCompanies;
        this.budget = budget;
    }

    public MovieId getMovieId() {
        return movieId;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getTagLine() {
        return tagLine;
    }

    public String getOverview() {
        return overview;
    }

    public String getStatus() {
        return status;
    }

    public String getHomePage() {
        return homePage;
    }

    public boolean isAdult() {
        return adult;
    }

    public Collection<Genre> getGenres() {
        return genres;
    }

    public Collection<ProductionCompany> getProductionCompanies() {
        return productionCompanies;
    }

    public int getBudget() {
        return budget;
    }
}
