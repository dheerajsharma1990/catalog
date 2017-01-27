package com.findmymovie.query.analyzer;

import java.util.List;

public class EmptyTree implements Tree {
    @Override
    public boolean validate(List<String> tokens, int i) {
        return tokens.size() == i;
    }
}
