package com.findmymovie.domain.id;

public class MovieId {

    private int movieId;

    public MovieId(int movieId) {
        this.movieId = movieId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MovieId movieId1 = (MovieId) o;

        return movieId == movieId1.movieId;

    }

    @Override
    public int hashCode() {
        return movieId;
    }

    @Override
    public String toString() {
        return "MovieId{" +
                "movieId='" + movieId + '\'' +
                '}';
    }
}
