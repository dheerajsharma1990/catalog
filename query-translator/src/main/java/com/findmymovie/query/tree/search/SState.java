package com.findmymovie.query.tree.search;

import com.findmymovie.query.tree.State;

import static com.findmymovie.query.tree.search.EState.E_STATE;

public class SState implements State {

    public static final SState S_STATE = new SState();

    @Override
    public State accept(String string, int index) {
        char character = string.charAt(index);
        if (character == 'e' || character == 'E') {
            return E_STATE;
        }
        throw new RuntimeException("Character [" + character + "] not expected.");
    }
}
