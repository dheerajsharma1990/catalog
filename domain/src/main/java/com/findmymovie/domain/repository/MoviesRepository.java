package com.findmymovie.domain.repository;

import com.findmymovie.domain.Movie;
import com.findmymovie.domain.ReleaseDateRange;

import java.util.Collection;

@FunctionalInterface
public interface MoviesRepository {

    Collection<Movie> getMovies(ReleaseDateRange releaseDateRange);

}
