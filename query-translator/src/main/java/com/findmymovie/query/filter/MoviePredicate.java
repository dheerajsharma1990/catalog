package com.findmymovie.query.filter;

import com.findmymovie.domain.Movie;

import java.util.function.Predicate;

@FunctionalInterface
public interface MoviePredicate {

    Predicate<Movie> getPredicate();
}
