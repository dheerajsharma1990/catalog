package com.findmymovie.query.analyzer;

public class EmptyToken implements Token {
    @Override
    public boolean validate(String token) {
        return false;
    }
}
