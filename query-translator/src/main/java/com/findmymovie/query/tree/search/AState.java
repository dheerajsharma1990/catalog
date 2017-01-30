package com.findmymovie.query.tree.search;

import com.findmymovie.query.tree.State;

import static com.findmymovie.query.tree.search.RState.R_STATE;

public class AState implements State {

    public static final AState A_STATE = new AState();

    @Override
    public State accept(String string, int index) {
        char character = string.charAt(index);
        if (character == 'r' || character == 'R') {
            return R_STATE;
        }
        throw new RuntimeException("Character [" + character + "] not expected.");
    }
}
