package com.findmymovie.query.filter;

import com.findmymovie.domain.Movie;

import java.util.function.Predicate;

public class HasPredicate implements MoviePredicate {

    private final String fieldName;
    private final String value;

    public HasPredicate(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
    }
    @Override
    public Predicate<Movie> getPredicate() {
        if (fieldName.equals("title")) {
            return movie -> movie.getTitle().contains(value);
        } else if (fieldName.equals("originalTitle")) {
            return movie -> movie.getOriginalTitle().contains(value);
        }
        throw new RuntimeException("No field found with name [" + fieldName + "].");
    }
}
