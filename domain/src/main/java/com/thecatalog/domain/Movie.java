package com.thecatalog.domain;

import com.thecatalog.domain.id.MovieId;

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

    private Collection<ProductionCompany> productionCompanies;

    private int budget;



}
