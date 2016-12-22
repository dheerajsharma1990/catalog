package com.thecatalog.domain;

import com.thecatalog.domain.id.GenreId;

public class Genre {

    private GenreId genreId;

    private String genre;

    public Genre(GenreId genreId, String genre) {
        this.genreId = genreId;
        this.genre = genre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Genre genre = (Genre) o;

        return genreId != null ? genreId.equals(genre.genreId) : genre.genreId == null;

    }

    @Override
    public int hashCode() {
        return genreId != null ? genreId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "genreId=" + genreId +
                ", genre='" + genre + '\'' +
                '}';
    }
}
