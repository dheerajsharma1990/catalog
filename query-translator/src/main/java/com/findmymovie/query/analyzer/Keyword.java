package com.findmymovie.query.analyzer;

public class Keyword implements Token {

    public static final Keyword SEARCH = new Keyword("search");
    public static final Keyword FOR = new Keyword("for");
    public static final Keyword MOVIES = new Keyword("movies");
    public static final Keyword WHOSE = new Keyword("whose");
    public static final Keyword IS = new Keyword("is");
    public static final Keyword AND = new Keyword("and");
    public static final Keyword OR = new Keyword("or");

    private final String name;

    public Keyword(String name) {
        this.name = name;
    }


    @Override
    public boolean validate(String token) {
        return name.equals(token);
    }
}
