package com.findmymovie.query.tree;

import static com.findmymovie.query.tree.search.SState.S_STATE;

public class RootState implements State {

    public State accept(String string, int index) {
        char character = string.charAt(index);
        if (character == ' ') {
            return this;
        }
        if (character == 's' || character == 'S') {
            return S_STATE;
        }
        throw new RuntimeException("Character [" + character + "] not expected.");
    }
}
