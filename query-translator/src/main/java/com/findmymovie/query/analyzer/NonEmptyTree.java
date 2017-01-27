package com.findmymovie.query.analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NonEmptyTree implements Tree {

    private final Token token;
    private final Collection<Tree> childrens = new ArrayList<>();

    public NonEmptyTree(Token token) {
        this.token = token;
    }

    public void addChildrens(List<Tree> childrens) {
        this.childrens.addAll(childrens);
    }

    @Override
    public boolean validate(List<String> tokens, int i) {
        return i < tokens.size()
                && token.validate(tokens.get(i))
                && childrens.stream().anyMatch(tree -> tree.validate(tokens, i + 1));
    }

}
