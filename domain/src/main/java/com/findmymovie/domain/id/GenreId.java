package com.findmymovie.domain.id;

public class GenreId {

    private int genreId;

    public GenreId(int genreId) {
        this.genreId = genreId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GenreId genreId1 = (GenreId) o;

        return genreId == genreId1.genreId;

    }

    @Override
    public int hashCode() {
        return genreId;
    }

    @Override
    public String toString() {
        return "GenreId{" +
                "genreId=" + genreId +
                '}';
    }
}
