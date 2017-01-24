package com.findmymovie.query.filter;

import com.findmymovie.domain.Movie;

import java.util.function.Predicate;

public class IsPredicate implements MoviePredicate {

    private final String fieldName;
    private final String value;

    public IsPredicate(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    @Override
    public Predicate<Movie> getPredicate() {
        if (fieldName.equals("title")) {
            return movie -> movie.getTitle().equals(value);
        } else if (fieldName.equals("adult")) {
            return movie -> movie.isAdult() == Boolean.valueOf(value);
        } else if (fieldName.equals("budget")) {
            return movie -> movie.getBudget() == Integer.valueOf(value);
        }
        throw new RuntimeException("No field found with name [" + fieldName + "].");
    }
}
