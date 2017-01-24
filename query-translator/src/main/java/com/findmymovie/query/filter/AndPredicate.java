package com.findmymovie.query.filter;

import com.findmymovie.domain.Movie;

import java.util.function.Predicate;

public class AndPredicate implements MoviePredicate {

    private final Predicate<Movie> leftPredicate;
    private final Predicate<Movie> rightPredicate;

    public AndPredicate(Predicate<Movie> leftPredicate, Predicate<Movie> rightPredicate) {
        this.leftPredicate = leftPredicate;
        this.rightPredicate = rightPredicate;
    }

    @Override
    public Predicate<Movie> getPredicate() {
        return leftPredicate.and(rightPredicate);
    }
}
