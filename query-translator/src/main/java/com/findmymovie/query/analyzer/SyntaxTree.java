package com.findmymovie.query.analyzer;

import static java.util.Arrays.asList;

public class SyntaxTree {

    public static NonEmptyTree getTree() {
        final EmptyTree emptyTree = new EmptyTree();

        NonEmptyTree isNode = new NonEmptyTree(Keyword.IS);
        NonEmptyTree andNode = new NonEmptyTree(Keyword.AND);
        NonEmptyTree orNode = new NonEmptyTree(Keyword.OR);
        StringToken identityToken = new StringToken();
        NonEmptyTree identityNode = new NonEmptyTree(identityToken);
        NonEmptyTree valueNode = new NonEmptyTree(identityToken);
        NonEmptyTree whoseNode = new NonEmptyTree(Keyword.WHOSE);
        NonEmptyTree moviesNode = new NonEmptyTree(Keyword.MOVIES);
        NonEmptyTree forNode = new NonEmptyTree(Keyword.FOR);
        NonEmptyTree searchNode = new NonEmptyTree(Keyword.SEARCH);

        searchNode.addChildrens(asList(forNode));
        forNode.addChildrens(asList(moviesNode));
        moviesNode.addChildrens(asList(whoseNode));
        whoseNode.addChildrens(asList(identityNode));
        identityNode.addChildrens(asList(isNode));
        isNode.addChildrens(asList(valueNode));
        valueNode.addChildrens(asList(andNode, orNode, emptyTree));
        andNode.addChildrens(asList(identityNode));
        orNode.addChildrens(asList(identityNode));
        return searchNode;
    }

}
