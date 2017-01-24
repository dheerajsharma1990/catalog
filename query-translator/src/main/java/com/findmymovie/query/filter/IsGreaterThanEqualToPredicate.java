package com.findmymovie.query.filter;

import com.findmymovie.domain.Movie;

import java.util.function.Predicate;

public class IsGreaterThanEqualToPredicate implements MoviePredicate {

    private final String fieldName;
    private final String value;

    public IsGreaterThanEqualToPredicate(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    @Override
    public Predicate<Movie> getPredicate() {
        if (fieldName.equals("budget")) {
            return movie -> movie.getBudget() >= Integer.valueOf(value);
        }
        throw new RuntimeException("No field found with name [" + fieldName + "].");
    }
}
