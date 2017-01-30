package com.findmymovie.query.tree.search;

import com.findmymovie.query.tree.State;

import static com.findmymovie.query.tree.search.CState.C_STATE;

public class RState implements State {

    public static final RState R_STATE = new RState();

    @Override
    public State accept(String string, int index) {
        char character = string.charAt(index);
        if (character == 'c' || character == 'C') {
            return C_STATE;
        }
        throw new RuntimeException("Character [" + character + "] not expected.");
    }
}
